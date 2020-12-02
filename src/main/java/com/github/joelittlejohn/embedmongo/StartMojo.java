/**
 * Copyright © 2012 Joe Littlejohn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.joelittlejohn.embedmongo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;

import com.github.joelittlejohn.embedmongo.log.Loggers;
import com.github.joelittlejohn.embedmongo.log.Loggers.LoggingStyle;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.IArtifactStore;

/**
 * When invoked, this goal starts an instance of mongo. The required binaries
 * are downloaded if no mongo release is found in <code>~/.embedmongo</code>.
 * 
 * @see <a href=
 *      "http://github.com/flapdoodle-oss/embedmongo.flapdoodle.de">http://github.com/flapdoodle-oss/embedmongo.flapdoodle.de</a>
 */
@Mojo(name = "start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class StartMojo extends AbstractEmbeddedMongoMojo {

	private static final String PACKAGE_NAME = StartMojo.class.getPackage().getName();
	public static final String MONGOD_CONTEXT_PROPERTY_NAME = PACKAGE_NAME + ".mongod";

	@Override
	protected void savePortToProjectProperties(int port) {
		super.savePortToProjectProperties(port);
	}

	/**
	 * The location of a directory that will hold the MongoDB data files.
	 * 
	 * @since 0.1.0
	 */
	@Parameter(property = "embedmongo.databaseDirectory")
	private File databaseDirectory;

	/**
	 * An IP address for the MongoDB instance to be bound to during its execution.
	 * 
	 * @since 0.1.4
	 */
	@Parameter(property = "embedmongo.bindIp")
	private String bindIp;

	/**
	 * @since 0.1.3
	 */
	@Parameter(property = "embedmongo.logging", defaultValue = "console")
	private String logging;

	/**
	 * @since 0.1.7
	 */
	@Parameter(property = "embedmongo.logFile", defaultValue = "embedmongo.log")
	private String logFile;

	/**
	 * @since 0.1.7
	 */
	@Parameter(property = "embedmongo.logFileEncoding", defaultValue = "utf-8")
	private String logFileEncoding;

	/**
	 * The base URL to be used when downloading MongoDB
	 * 
	 * @since 0.1.10
	 */
	@Parameter(property = "embedmongo.downloadPath", defaultValue = "http://fastdl.mongodb.org/")
	private String downloadPath;

	/**
	 * Should authorization be enabled for MongoDB
	 */
	@Parameter(property = "embedmongo.authEnabled", defaultValue = "false")
	private boolean authEnabled;

	/**
	 * The path for the UNIX socket
	 * 
	 * @since 0.3.5
	 */
	@Parameter(property = "embedmongo.unixSocketPrefix")
	private String unixSocketPrefix;

	@Parameter(property = "embedmongo.journal", defaultValue = "false")
	private boolean journal;

	/**
	 * The storageEngine which shall be used
	 * 
	 * @since 0.3.4
	 */
	@Parameter(property = "embedmongo.storageEngine", defaultValue = "mmapv1")
	private String storageEngine;

	@Parameter(defaultValue = "${settings}", readonly = true)
	protected Settings settings;

	@Override
	protected void onSkip() {
		getLog().debug("skip=true, not starting embedmongo");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void executeStart() throws MojoExecutionException, MojoFailureException {
		MongodExecutable executable;

		// TODDO: Rian Vasconcelos
		try {
			loadBinaryMongoFromResource();
		} catch (Exception ioException) {
			throw new MojoExecutionException("Failed get MongoDB distribution from resources", ioException);
		}

		try {

			final List<String> mongodArgs = this.createMongodArgsList();
			final ICommandLinePostProcessor commandLinePostProcessor = new ICommandLinePostProcessor() {
				@Override
				public List<String> process(final Distribution distribution, final List<String> args) {
					args.addAll(mongodArgs);
					return args;
				}
			};

			IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD)
					.processOutput(getOutputConfig()).artifactStore(getArtifactStore())
					.commandLinePostProcessor(commandLinePostProcessor).build();

			int port = getPort();

			if (isRandomPort()) {
				port = NetworkUtils.allocateRandomPort();
			}
			savePortToProjectProperties(port);

			IMongodConfig config = new MongodConfigBuilder().version(getVersion())
					.net(new Net(bindIp, port, NetworkUtils.localhostIsIPv6()))
					.replication(new Storage(getDataDirectory(), null, 0)).cmdOptions(new MongoCmdOptionsBuilder()
							.enableAuth(authEnabled).useNoJournal(!journal).useStorageEngine(storageEngine).build())
					.build();

			executable = MongodStarter.getInstance(runtimeConfig).prepare(config);

		} catch (IOException e) {
			throw new MojoExecutionException("Unable to Config MongoDB: ", e);
		}

		try {
			MongodProcess mongod = executable.start();

			// TODO: CUNSTOM RIAN
//			while (isWait()) {
//				try {
//					TimeUnit.MINUTES.sleep(5);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//			}

			getPluginContext().put(MONGOD_CONTEXT_PROPERTY_NAME, mongod);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to start the mongod", e);
		}
	}

	private List<String> createMongodArgsList() {
		List<String> mongodArgs = new ArrayList<String>();

		if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1 && this.unixSocketPrefix != null
				&& !this.unixSocketPrefix.isEmpty()) {
			mongodArgs.add("--unixSocketPrefix=" + this.unixSocketPrefix);
		}

		return mongodArgs;
	}

	private ProcessOutput getOutputConfig() throws MojoFailureException {

		if (logging == null) {
			logging = LoggingStyle.NONE.name();
		}

		LoggingStyle loggingStyle = LoggingStyle.valueOf(logging.toUpperCase());

		switch (loggingStyle) {
		case CONSOLE:
			return Loggers.console();
		case FILE:
			return Loggers.file(logFile, logFileEncoding);
		default:
			return Loggers.none();
		}

	}

	private IArtifactStore getArtifactStore() {
		IDownloadConfig downloadConfig = new DownloadConfigBuilder().defaultsForCommand(Command.MongoD)
				.downloadPath(downloadPath).build();
		return new ExtractedArtifactStoreBuilder().defaults(Command.MongoD).download(downloadConfig).build();
	}

	private String getDataDirectory() {
		if (databaseDirectory != null) {
			return databaseDirectory.getAbsolutePath();
		} else {
			return null;
		}
	}

	public void loadBinaryMongoFromResource() throws Exception {
		File dirOut = new File(new LocalCheckDirPlataformDecorator(new LocalDirBinaryMongo()).buildPathOutputDir());
		if (dirOut.listFiles().length == 0) {
			LocalDirDecorator localDirDecorator = new LocalDirPlataformDecorator(new LocalDirBinaryMongo());
			String in = localDirDecorator.buildPathInputDir();
			String out = localDirDecorator.buildPathOutputDir();
			FileCopy.copy(in, out);
		}
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void setLogging(String logging) {
		this.logging = logging;
	}

}

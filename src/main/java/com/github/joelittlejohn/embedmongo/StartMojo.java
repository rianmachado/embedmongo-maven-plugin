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

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.config.store.IProxyFactory;
import de.flapdoodle.embed.process.config.store.NoProxyFactory;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.exceptions.DistributionException;
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
	private static Logger logger = LoggerFactory.getLogger(StartMojo.class);

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

		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
		MongodExecutable executable;
		
		BinariResource.copyMongoFromResource();
		
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
		} catch (DistributionException e) {
			throw new MojoExecutionException("Failed to download MongoDB distribution: " + e.withDistribution(), e);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to Config MongoDB: ", e);
		}

		try {
			MongodProcess mongod = executable.start();

//TODO: CUNSTOM RIAN
//			if (isWait()) {
			while (isWait()) {
				try {
					TimeUnit.MINUTES.sleep(5);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
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

		LoggingStyle loggingStyle = LoggingStyle.valueOf(logging.toUpperCase());

		switch (loggingStyle) {
		case CONSOLE:
			return Loggers.console();
		case FILE:
			return Loggers.file(logFile, logFileEncoding);
		case NONE:
			return Loggers.none();
		default:
			throw new MojoFailureException(
					"Unexpected logging style encountered: \"" + logging + "\" -> " + loggingStyle);
		}

	}

	private IArtifactStore getArtifactStore() {
		IDownloadConfig downloadConfig = new DownloadConfigBuilder().defaultsForCommand(Command.MongoD)
				.proxyFactory(getProxyFactory(settings)).downloadPath(downloadPath).build();
		return new ExtractedArtifactStoreBuilder().defaults(Command.MongoD).download(downloadConfig).build();
	}

	public IProxyFactory getProxyFactory(Settings settings) {
		URI downloadUri = URI.create(downloadPath);
		final String downloadHost = downloadUri.getHost();
		final String downloadProto = downloadUri.getScheme();

		if (settings.getProxies() != null) {
			for (org.apache.maven.settings.Proxy proxy : (List<org.apache.maven.settings.Proxy>) settings
					.getProxies()) {
				if (proxy.isActive() && equalsIgnoreCase(proxy.getProtocol(), downloadProto)
						&& !contains(proxy.getNonProxyHosts(), downloadHost)) {
					return new HttpProxyFactory(proxy.getHost(), proxy.getPort());
				}
			}
		}

		return new NoProxyFactory();
	}

	private String getDataDirectory() {
		if (databaseDirectory != null) {
			return databaseDirectory.getAbsolutePath();
		} else {
			return null;
		}
	}

	public static class BinariResource {

		public static final String MONGO_BINARI_WINDOWS = "/win32/mongodb-win32-x86_64-2.7.1.zip";
		public static final String MONGO_BINARI_MACOS = "/osx/mongodb-win32-x86_64-3.5.5.zip";
		public static final String MONGO_BINARI_LINUX = "/linux/mongodb-win32-x86_64-3.5.5.zip";

		public static void copyMongoFromResource() {
			Platform platform = Platform.detect();
			StringBuilder osDir = new StringBuilder();
			String imputDir = "";
			String outPutDir = "";
			if (Platform.Windows == platform) {
				File file = new File(java.nio.file.Paths.get(System.getProperty("user.home"))
						.resolve(".embedmongo/win32").toString());
				if (file.exists() && file.isDirectory()&&file.listFiles().length>0) {
					return;
				}
				file.mkdir();
				imputDir = "mongo-repo" + MONGO_BINARI_WINDOWS;
				outPutDir = java.nio.file.Paths.get(System.getProperty("user.home"))
						.resolve(".embedmongo" + MONGO_BINARI_WINDOWS).toString();
				osDir.append(outPutDir);
				osDir.append(outPutDir);
				osDir.append(java.io.File.separator);
				osDir.append("win32");
				osDir.append(java.io.File.separator);

			} else if (Platform.OS_X == platform) {
				File file = new File(
						java.nio.file.Paths.get(System.getProperty("user.home")).resolve(".embedmongo/osx").toString());
				if (file.exists() && file.isDirectory()) {
					return;
				}
				file.mkdir();
				imputDir = "mongo-repo" + MONGO_BINARI_MACOS;
				outPutDir = java.nio.file.Paths.get(System.getProperty("user.home"))
						.resolve(".embedmongo" + MONGO_BINARI_MACOS).toString();
				osDir.append(outPutDir);
				osDir.append(outPutDir);
				osDir.append(java.io.File.separator);
				osDir.append("osx");
				osDir.append(java.io.File.separator);

			} else if (Platform.Linux == platform) {
				File file = new File(java.nio.file.Paths.get(System.getProperty("user.home"))
						.resolve(".embedmongo/linux").toString());
				if (file.exists() && file.isDirectory()) {
					return;
				}
				file.mkdir();
				imputDir = "mongo-repo" + MONGO_BINARI_LINUX;
				outPutDir = java.nio.file.Paths.get(System.getProperty("user.home"))
						.resolve(".embedmongo" + MONGO_BINARI_LINUX).toString();
				osDir.append(outPutDir);
				osDir.append(outPutDir);
				osDir.append(java.io.File.separator);
				osDir.append("linux");
				osDir.append(java.io.File.separator);
			}

			try {
				FileCopy.copyFilePlainJava(imputDir, outPutDir);
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		}

	}

	public static class FileCopy {

		public static void copyFilePlainJava(String from, String to) throws IOException {
			InputStream inStream = null;
			OutputStream outStream = null;
			try {
				File toFile = new File(to);
				ClassLoader classLoader = StartMojo.class.getClassLoader();
				inStream = classLoader.getResourceAsStream(from);
				outStream = new FileOutputStream(toFile);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
					outStream.flush();
				}
			} finally {
				if (inStream != null)
					inStream.close();

				if (outStream != null)
					outStream.close();
			}
		}

	}

}

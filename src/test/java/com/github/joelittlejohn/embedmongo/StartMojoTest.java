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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.joelittlejohn.embedmongo.log.Loggers;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class StartMojoTest {

	private static String from;
	private static String to;
	private static Path outDirOs;

	@BeforeClass
	public static void init() {
		try {

			outDirOs = Paths.get(new LocalCheckDirPlataformDecorator(new LocalDirBinaryMongo()).buildPathOutputDir());

			if (!outDirOs.toString().contains(".embedmongo")) {
				throw new RuntimeException(
						"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
			}

			Files.walk(outDirOs).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

			new LocalCheckDirPlataformDecorator(new LocalDirBinaryMongo()).buildPathOutputDir();

			LocalDirDecorator localDirDecorator = new LocalDirPlataformDecorator(new LocalDirBinaryMongo());

			from = localDirDecorator.buildPathInputDir();

			to = localDirDecorator.buildPathOutputDir();

			FileCopy.copy(from, to);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@AfterClass
	public static void finish() {
		try {

			if (!outDirOs.toString().contains(".embedmongo")) {
				throw new RuntimeException(
						"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
			}

			Path out = Paths.get(to);
			if (Files.exists(out)) {
				Files.delete(out);
			}
			Files.delete(outDirOs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExecuteStartStop() {

		int port = 0;
		try {
			port = NetworkUtils.allocateRandomPort();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		StartMojo startMojo = new StartMojo();
		startMojo.setProject(new MavenProject());
		startMojo.setDownloadPath("");
		startMojo.setSettings(new Settings());
		startMojo.setPort(port);
		startMojo.setVersion("2.7.1");
		startMojo.setPluginContext(new HashMap<>());
		startMojo.setLogging(Loggers.LoggingStyle.CONSOLE.name());
		startMojo.onSkip();

		StopMojo stopMojo = new StopMojo();
		stopMojo.setProject(new MavenProject());
		stopMojo.setPort(port);
		stopMojo.setVersion("2.7.1");
		stopMojo.setPluginContext(startMojo.getPluginContext());
		try {
			startMojo.executeStart();
			stopMojo.execute();
			assertNotNull(startMojo.getPluginContext().get(StartMojo.MONGOD_CONTEXT_PROPERTY_NAME));
		} catch (MojoExecutionException | MojoFailureException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExecuteStartStopRandomPortTrue() {

		StartMojo startMojo = new StartMojo();
		startMojo.setProject(new MavenProject());
		startMojo.setDownloadPath("");
		startMojo.setSettings(new Settings());
		startMojo.setVersion("2.7.1");
		startMojo.setPluginContext(new HashMap<>());
		startMojo.setLogging(Loggers.LoggingStyle.CONSOLE.name());
		startMojo.onSkip();
		startMojo.setRandomPort(true);

		StopMojo stopMojo = new StopMojo();
		stopMojo.setProject(new MavenProject());
		stopMojo.setVersion("2.7.1");
		stopMojo.setPluginContext(startMojo.getPluginContext());
		try {
			startMojo.executeStart();
			stopMojo.execute();
			assertNotNull(startMojo.getPluginContext().get(StartMojo.MONGOD_CONTEXT_PROPERTY_NAME));
		} catch (MojoExecutionException | MojoFailureException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExecuteStartStopErro() {
		StartMojo startMojo = new StartMojo();
		startMojo.setPluginContext(new HashMap<>());
		startMojo.setLogging(Loggers.LoggingStyle.FILE.name());

		StopMojo stopMojo = new StopMojo();
		stopMojo.setProject(new MavenProject());
		stopMojo.setPort(27017);
		stopMojo.setVersion("2.7.1");
		HashMap<String, String> map = new HashMap<>();
		map.put(StartMojo.MONGOD_CONTEXT_PROPERTY_NAME, "");
		stopMojo.setPluginContext(startMojo.getPluginContext());
		startMojo.onSkip();
		try {
			stopMojo.execute();
		} catch (MojoExecutionException | MojoFailureException e) {
			assertNull(startMojo.getFeatures());
			assertNull(startMojo.getProject());
			assertTrue("No mongod process found, it appears embedmongo:start was not called"
					.equalsIgnoreCase(e.getLocalizedMessage()));
		}

	}

	@Test
	public void testExecuteStartStopPortNotEmpty() {

		StartMojo startMojo = new StartMojo();
		MavenProject mavenProject = new MavenProject();
		mavenProject.getProperties().put("embedmongo.port", "25118");
		startMojo.setProject(mavenProject);
		startMojo.setDownloadPath("");
		startMojo.setSettings(new Settings());
		startMojo.setVersion("2.7.1");
		startMojo.setPluginContext(new HashMap<>());
		startMojo.setLogging(Loggers.LoggingStyle.CONSOLE.name());
		startMojo.onSkip();

		StopMojo stopMojo = new StopMojo();
		stopMojo.setProject(new MavenProject());
		stopMojo.setPort(25118);
		stopMojo.setVersion("2.7.1");
		stopMojo.setPluginContext(startMojo.getPluginContext());
		try {
			startMojo.executeStart();
			stopMojo.execute();
			assertEquals(25118, startMojo.getPort().intValue());
		} catch (MojoExecutionException | MojoFailureException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testExecuteStartStopLogFile() {

		int port = 0;
		try {
			port = NetworkUtils.allocateRandomPort();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		StartMojo startMojo = new StartMojo();
		MavenProject mavenProject = new MavenProject();
		startMojo.setLogFile("embedmongo.log");
		startMojo.setLogFileEncoding("utf-8");
		startMojo.setProject(mavenProject);
		startMojo.setDownloadPath("");
		startMojo.setSettings(new Settings());
		startMojo.setPort(port);
		startMojo.setVersion("2.7.1");
		startMojo.setPluginContext(new HashMap<>());
		startMojo.setLogging(Loggers.LoggingStyle.FILE.name());
		startMojo.onSkip();

		StopMojo stopMojo = new StopMojo();
		stopMojo.setProject(mavenProject);
		stopMojo.setPort(port);
		stopMojo.setVersion("2.7.1");
		stopMojo.setPluginContext(startMojo.getPluginContext());
		try {
			startMojo.executeStart();
			stopMojo.execute();
			assertNotNull(startMojo.getPluginContext().get(StartMojo.MONGOD_CONTEXT_PROPERTY_NAME));
		} catch (MojoExecutionException | MojoFailureException e) {
			e.printStackTrace();
		}

	}

}

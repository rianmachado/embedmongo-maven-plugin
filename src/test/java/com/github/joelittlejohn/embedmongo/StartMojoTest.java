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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class StartMojoTest {

	private static String outDir;

	@BeforeClass
	public static void init() {
		LocalDirDecorator localDirDecorator = new LocalDirPlataformDecorator(new LocalDirBinaryMongo());
		outDir = localDirDecorator.buildPathOutputDir();
	}

	@AfterClass
	public static void finish() {
		try {
			Path out = Paths.get(outDir);
			if (Files.exists(out)) {
				Files.delete(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExecuteStart() {
		StartMojo startMojo = new StartMojo();
		startMojo.setProject(new MavenProject());
		startMojo.setDownloadPath("");
		startMojo.setSettings(new Settings());
		startMojo.setPort(27017);
		startMojo.setVersion("2.2.1");
		try {
			startMojo.executeStart();
		} catch (MojoExecutionException | MojoFailureException e) {
			assertTrue(e.getLocalizedMessage()
					.startsWith("Failed to download MongoDB distribution: GenericFeatureAwareVersion{2.2.1}"));
		}

	}

	@Test
	public void testExecuteImportMojoDataBaseNotStarted() {
		MongoScriptsMojo mongoScriptsMojo = new MongoScriptsMojo();
		File scriptsDirectory = java.nio.file.Paths.get(System.getProperty("user.home")).toFile();
		mongoScriptsMojo.setScriptsDirectory(scriptsDirectory);
		try {
			mongoScriptsMojo.execute();
		} catch (MojoExecutionException | MojoFailureException e) {
			assertEquals("Database name is missing", e.getLocalizedMessage());
		}
	}

}

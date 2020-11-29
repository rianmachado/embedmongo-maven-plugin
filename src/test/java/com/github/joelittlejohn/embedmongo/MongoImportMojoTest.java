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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MongoImportMojoTest {

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
	public void testExecuteImportMojoDataBaseNotStarted() {
		MongoImportMojo mongoImportMojo = new MongoImportMojo();
		ImportDataConfig config = new ImportDataConfig("demo", "collection-demo",
				"C:\\Users\\rndd\\.embedmongo\\demo-test.json", false, false, 1000);
		ImportDataConfig[] configs = new ImportDataConfig[1];
		configs[0] = config;
		mongoImportMojo.setVersion("2.7.1");
		mongoImportMojo.setPort(27017);
		mongoImportMojo.setProject(new MavenProject());
		mongoImportMojo.setImports(configs);
		try {
			mongoImportMojo.execute();
		} catch (MojoExecutionException | MojoFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

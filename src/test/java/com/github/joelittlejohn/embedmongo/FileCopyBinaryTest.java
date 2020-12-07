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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.joelittlejohn.embedmongo.configuration.ConfigurationDirectoryMongoBinary;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class FileCopyBinaryTest {

	private static String to;
	private static Path outDirOs;

	private static String plataform = Platform.detect().name();
	private static String valueBinaryPlataform;
	private static String directoryNamePlataform;

	private static String fileZipMock = "demo.mock.zip.nao.existe";

	@BeforeClass
	public static void init() throws IOException {
		try {

			valueBinaryPlataform = ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().get(plataform);

			directoryNamePlataform = ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName()
					.get(plataform);

			outDirOs = Paths.get(new LocalCheckDirPlataformDecorator(new LocalDirBinaryMongo()).buildPathOutputDir());

			if (!outDirOs.toString().contains(".embedmongo")) {
				throw new RuntimeException(
						"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
			}

			Files.walk(outDirOs).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

		} catch (IOException e) {
			e.printStackTrace();
		}
		to = new LocalDirPlataformDecorator(new LocalDirBinaryMongo()).buildPathOutputDir();
	}

	@AfterClass
	public static void finish() {

		if (!outDirOs.toString().contains(".embedmongo")) {
			throw new RuntimeException(
					"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
		}

		try {

			Path out = Paths.get(to);

			if (Files.exists(out)) {
				Files.delete(out);
			}
			if (Files.exists(outDirOs)) {
				Files.delete(outDirOs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void reconfigurationMapProperties() {
		try {

			if (ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().containsValue(fileZipMock)
					|| !ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().containsKey(plataform)
					|| !ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName().containsKey(plataform)) {

				ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().put(plataform,
						valueBinaryPlataform);

				ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName().put(plataform,
						directoryNamePlataform);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCopyBinaryFromResource() {

		boolean test = false;

		try {

			StartMojo startMojo = new StartMojo();

			startMojo.loadBinaryMongoFromResource();

			test = Files.exists(Paths.get(to));

			assertTrue(test);

		} catch (Exception e) {

			assertTrue(test);

		}
	}

	@Test
	public void testCopyBinaryFromResourceErro() {

		try {

			StartMojo startMojo = new StartMojo();

			ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().put(plataform, fileZipMock);

			startMojo.loadBinaryMongoFromResource();

		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

	}

	@Test
	public void testCopyFromToErro() {
		try {

			FileCopy.copy(null, "demo.test");

		} catch (Exception e) {

			assertTrue("Mongo binary not found".equalsIgnoreCase(e.getLocalizedMessage()));
		}
	}

	@Test
	public void testPlataformResolverInputPathErro() {
		try {

			LocalDirPlataformDecorator localDirPlataformDecorator = new LocalDirPlataformDecorator(
					new LocalDirBinaryMongo());

			ConfigurationDirectoryMongoBinary.getInstance().getMapMongoBinary().remove(plataform);

			localDirPlataformDecorator.plataformResolverInputPath();

		} catch (Exception e) {

			assertTrue("Input directory not found".equalsIgnoreCase(e.getLocalizedMessage()));
		}
	}

	@Test
	public void testPlataformResolverOutPutPathErro() {
		try {

			LocalDirPlataformDecorator localDirPlataformDecorator = new LocalDirPlataformDecorator(
					new LocalDirBinaryMongo());

			ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName().remove(plataform);

			localDirPlataformDecorator.plataformResolverOutputPath();

		} catch (Exception e) {

			assertTrue("Output directory not found".equalsIgnoreCase(e.getLocalizedMessage()));
		}
	}

	@Test
	public void testLoadBinaryMongoFromResourceErro() {
		try {

			StartMojo start = new StartMojo();

			ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName().remove(plataform);

			start.executeStart();

		} catch (MojoExecutionException | MojoFailureException e) {

			assertTrue(e instanceof MojoExecutionException);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
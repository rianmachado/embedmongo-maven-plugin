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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.joelittlejohn.embedmongo.configuration.ConfigurationDirectoryMongoBinary;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationDirectoryMongoTest {

	private static Path outDirOs;

	@BeforeClass
	public static void init() throws IOException {
		try {

			outDirOs = Paths.get(new LocalDirBinaryMongo().buildPathOutputDir());

			if (!outDirOs.toString().contains(".embedmongo")) {
				throw new RuntimeException(
						"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
			}

			if (Files.exists(outDirOs)) {
				Files.walk(outDirOs).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void finish() {

		if (!outDirOs.toString().contains(".embedmongo")) {
			throw new RuntimeException(
					"DANGER..... OS TESTES PODERAO PAGAR DADOS DA HOME CASO NAO ESTEJA COM O DIRETORIO .embedmongo CONFIGURADO CORRETAMENTE");
		}

		if (Files.exists(outDirOs)) {
			File directory = new File(outDirOs.toString());
			directory.delete();
		}
		
		try {
			ConfigurationDirectoryMongoBinary.setInstance(null);
			ConfigurationDirectoryMongoBinary.getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetInstanceCreateDirectoryHomeMongoEmbedded() {

		ConfigurationDirectoryMongoBinary.setInstance(null);

		try {

			ConfigurationDirectoryMongoBinary.getInstance();

			assertTrue(Files.exists(outDirOs));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
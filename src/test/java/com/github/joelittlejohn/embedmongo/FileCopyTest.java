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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class FileCopyTest {

	private static String from;
	private static String to;

	@BeforeClass
	public static void init() {
		LocalDirDecorator localDirDecorator = new LocalDirPlataformDecorator(new LocalDirBinaryMongo());
		from = localDirDecorator.buildPathInputDir();
		to = localDirDecorator.buildPathOutputDir();
	}

	@AfterClass
	public static void finish() {
		try {
			Path out = Paths.get(to);
			if (Files.exists(out)) {
				Files.delete(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCopy() {
		try {
			FileCopy.copy(from, to);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(Files.exists(Paths.get(to)));
	}



}
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.joelittlejohn.embedmongo.configuration.LoadConfiguration;

/**
 * @author rianmachado@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalDirBinaryMongoTest {

	private static LocalDirBinaryMongo localDirBinaryMongo;

	@BeforeClass
	public static void init() {
		localDirBinaryMongo = new LocalDirBinaryMongo();
	}

	@Test
	public void testBuildPathInputDir() {
		assertEquals(LoadConfiguration.ROOT_DIR, localDirBinaryMongo.buildPathInputDir());
	}

	@Test
	public void testBuildPathOutputDir() {
		assertTrue("Path must contain .embedmongo", localDirBinaryMongo.buildPathOutputDir().contains(".embedmongo"));
	}

}

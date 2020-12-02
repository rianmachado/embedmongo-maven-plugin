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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author rianmachado@gmail.com
 */
public final class FileCopy {
	
	private FileCopy(){
	}

	public static void copy(String from, String to) throws IOException {
		InputStream inStream = null;
		if (from == null) {
			throw new IOException("Mongo binary not found");
		}
		ClassLoader classLoader = FileCopy.class.getClassLoader();
		inStream = classLoader.getResourceAsStream(from);
		try (OutputStream outStream = Files.newOutputStream(Paths.get(to))) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
				outStream.flush();
			}
		}

	}

}
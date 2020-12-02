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

import com.github.joelittlejohn.embedmongo.configuration.ConfigurationDirectoryMongoBinary;

import de.flapdoodle.embed.process.distribution.Platform;

public class LocalCheckDirPlataformDecorator extends LocalDirDecorator {

	LocalCheckDirPlataformDecorator(LocalDir localDir) throws IOException {
		super(localDir);
	}

	@Override
	public String buildPathOutputDir() throws IOException {
		return checkPlatformOutputDirectory(super.buildPathOutputDir());
	}

	private String checkPlatformOutputDirectory(String basePath) throws IOException {
		String dir = ConfigurationDirectoryMongoBinary.getInstance().getMapDirectoryName()
				.get(Platform.detect().name());
		File file = new File(basePath + dir).getParentFile().getAbsoluteFile();
		if (!file.exists()) {
			boolean created = file.mkdir();
			if(!created) {
				throw new IOException("Directory: " + file.getName() + " not created");
			}
		}
		return file.getAbsolutePath();

	}

}

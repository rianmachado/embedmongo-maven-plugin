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

import com.github.joelittlejohn.embedmongo.configuration.ConfigurationDirectoryMongoBinary;

import de.flapdoodle.embed.process.distribution.Platform;

public class LocalCheckDirPlataformDecorator extends LocalDirDecorator {

	LocalCheckDirPlataformDecorator(LocalDir localDir) {
		super(localDir);
	}

	@Override
	public String buildPathOutputDir() {
		return checkPlatformOutputDirectory(super.buildPathOutputDir());
	}

	private String checkPlatformOutputDirectory(String basePath) {
		String dir = ConfigurationDirectoryMongoBinary.getInstance().getMAP_DIRECTORY_NAME()
				.get(Platform.detect().name());
		File file = new File(basePath + dir).getParentFile().getAbsoluteFile();
		if (!file.exists()) {
			file.mkdir();
		}
		return file.getAbsolutePath();

	}

}

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

import com.github.joelittlejohn.embedmongo.configuration.GlobalConfiguration;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
public class LocalDirPlataformDecorator extends LocalDirDecorator {

	LocalDirPlataformDecorator(LocalDir localDir) throws IOException {
		super(localDir);
	}

	@Override
	public String buildPathInputDir() throws IOException {
		String platformDir = plataformResolverInputPath();
		return super.buildPathInputDir().concat(platformDir);
	}

	@Override
	public String buildPathOutputDir() throws IOException {
		String platformDir = plataformResolverOutputPath();
		return super.buildPathOutputDir().concat(platformDir);
	}

	public String plataformResolverInputPath() throws IOException {
		String directoryMongoBinary = GlobalConfiguration.getInstance().getMapMongoBinary()
				.get(Platform.detect().name());
		if (directoryMongoBinary == null) {
			throw new IOException("Input directory not found");
		}
		return directoryMongoBinary;
	}

	public String plataformResolverOutputPath() throws IOException {
		String directoryName = GlobalConfiguration.getInstance().getMapDirectoryName()
				.get(Platform.detect().name());
		if (directoryName == null) {
			throw new IOException("Output directory not found");
		}
		return directoryName;
	}

}

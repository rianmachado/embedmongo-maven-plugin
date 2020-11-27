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

import com.github.joelittlejohn.embedmongo.constants.ParamLocalDir;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
public class LocalDirPlataformDecorator extends LocalDirDecorator {

	LocalDirPlataformDecorator(LocalDir localDir) {
		super(localDir);
	}

	@Override
	public String buildPathInputDir() {
		String platformDir = plataformResolvInputPath();
		return super.buildPathInputDir().concat(platformDir);
	}

	@Override
	public String buildPathOutputDir() {
		String platformDir = plataformResolvOutputPath();
		return super.buildPathOutputDir().concat(platformDir);
	}

	public String plataformResolvInputPath() {
		return ParamLocalDir.MAP_MONGO_BINARY.get(Platform.detect().name());
	}

	public String plataformResolvOutputPath() {
		return ParamLocalDir.MAP_DIRECTORY_NAME.get(Platform.detect().name());
	}

}

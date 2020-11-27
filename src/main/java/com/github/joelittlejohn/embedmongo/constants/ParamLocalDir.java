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
package com.github.joelittlejohn.embedmongo.constants;

import java.util.HashMap;
import java.util.Map;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
public final class ParamLocalDir {

	public static String MONGO_BINARI_WINDOWS_VERSION = "mongodb-win32-x86_64-2.7.1.zip";
	public static String MONGO_BINARI_MACOS_VERSION = "mongodb-osx-x86_64-2.7.1.zip";
	public static String MONGO_BINARI_LINUX_VERSION = "mongodb-osx-x86_64-2.7.1.zip";

	private static final String DIRECTORY_NAME_FOR_WINDOWS = "win32";
	private static final String DIRECTORY_NAME_FOR_MACOS = "osx";
	private static final String DIRECTORY_NAME_FOR_LINUX = "linux";

	public static String ROOT_DIR = "mongo-repo";

	public static final Map<String, String> MAP_MONGO_BINARY = new HashMap<String, String>();
	public static final Map<String, String> MAP_DIRECTORY_NAME = new HashMap<String, String>();

	static {

		MAP_MONGO_BINARY.put(Platform.Windows.name(), java.io.File.separator + DIRECTORY_NAME_FOR_WINDOWS
				+ java.io.File.separator + MONGO_BINARI_WINDOWS_VERSION);

		MAP_MONGO_BINARY.put(Platform.Linux.name(), java.io.File.separator + DIRECTORY_NAME_FOR_LINUX
				+ java.io.File.separator + MONGO_BINARI_LINUX_VERSION);

		MAP_MONGO_BINARY.put(Platform.OS_X.name(), java.io.File.separator + DIRECTORY_NAME_FOR_MACOS
				+ java.io.File.separator + MONGO_BINARI_MACOS_VERSION);

		MAP_DIRECTORY_NAME.put(Platform.Windows.name(), java.io.File.separator + DIRECTORY_NAME_FOR_WINDOWS
				+ java.io.File.separator + MONGO_BINARI_WINDOWS_VERSION);

		MAP_DIRECTORY_NAME.put(Platform.Linux.name(), java.io.File.separator + DIRECTORY_NAME_FOR_LINUX
				+ java.io.File.separator + MONGO_BINARI_LINUX_VERSION);

		MAP_DIRECTORY_NAME.put(Platform.OS_X.name(), java.io.File.separator + DIRECTORY_NAME_FOR_MACOS
				+ java.io.File.separator + MONGO_BINARI_MACOS_VERSION);
	}

	private ParamLocalDir() {
	}

}

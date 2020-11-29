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

/**
 * @author rianmachado@gmail.com
 */
public final class ParamLocalDir {

	public static String MONGO_BINARI_WINDOWS_VERSION = "mongo-binari-windows-version";
	public static String MONGO_BINARI_MACOS_VERSION = "mongo-binari-macos-version";
	public static String MONGO_BINARI_LINUX_VERSION = "mongo-binari-linux-version";

	public static final String DIRECTORY_NAME_FOR_WINDOWS = "directory-name-for-windows";
	public static final String DIRECTORY_NAME_FOR_MACOS = "directory-name-for-macos";
	public static final String DIRECTORY_NAME_FOR_LINUX = "directory-name-for-linux";

	public static final Map<String, String> MAP_MONGO_BINARY = new HashMap<String, String>();
	public static final Map<String, String> MAP_DIRECTORY_NAME = new HashMap<String, String>();

	private ParamLocalDir() {
	}

}

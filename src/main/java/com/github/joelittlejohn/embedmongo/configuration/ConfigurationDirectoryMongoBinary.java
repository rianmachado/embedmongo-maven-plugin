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
package com.github.joelittlejohn.embedmongo.configuration;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
public final class ConfigurationDirectoryMongoBinary {

	private static ConfigurationDirectoryMongoBinary instance = null;

	public static final String EMBEDMONGO_HOME = java.nio.file.Paths.get(System.getProperty("user.home"))
			.resolve(".embedmongo").toString();

	private static String MONGO_BINARI_WINDOWS_VERSION = "mongo-binari-windows-version";
	private static String MONGO_BINARI_MACOS_VERSION = "mongo-binari-macos-version";
	private static String MONGO_BINARI_LINUX_VERSION = "mongo-binari-linux-version";

	private static final String DIRECTORY_NAME_FOR_WINDOWS = "directory-name-for-windows";
	private static final String DIRECTORY_NAME_FOR_MACOS = "directory-name-for-macos";
	private static final String DIRECTORY_NAME_FOR_LINUX = "directory-name-for-linux";

	private Map<String, String> MAP_MONGO_BINARY = new HashMap<String, String>();
	private Map<String, String> MAP_DIRECTORY_NAME = new HashMap<String, String>();
	private String ROOT_DIR;

	public static synchronized ConfigurationDirectoryMongoBinary getInstance() {
		if (instance == null) {
			instance = new ConfigurationDirectoryMongoBinary();
			loadProperties(instance);
		}
		return instance;
	}

	private static void loadProperties(ConfigurationDirectoryMongoBinary app) {

		File embedmongo_home = new File(ConfigurationDirectoryMongoBinary.EMBEDMONGO_HOME);
		
		if (!embedmongo_home.exists()) {
			embedmongo_home.mkdir();
		}

		Yaml yaml = new Yaml();
		InputStream inputStream = ConfigurationDirectoryMongoBinary.class.getClassLoader()
				.getResourceAsStream("application.yaml");
		Map<String, Object> objPropertie = yaml.load(inputStream);

		app.ROOT_DIR = objPropertie.get("root-dir").toString();

		app.MAP_MONGO_BINARY.put(Platform.Windows.name(),
				objPropertie.get(DIRECTORY_NAME_FOR_WINDOWS) + "/" + objPropertie.get(MONGO_BINARI_WINDOWS_VERSION));

		app.MAP_MONGO_BINARY.put(Platform.Linux.name(),
				objPropertie.get(DIRECTORY_NAME_FOR_LINUX) + "/" + objPropertie.get(MONGO_BINARI_LINUX_VERSION));

		app.MAP_MONGO_BINARY.put(Platform.OS_X.name(),
				objPropertie.get(DIRECTORY_NAME_FOR_MACOS) + "/" + objPropertie.get(MONGO_BINARI_MACOS_VERSION));

		app.MAP_DIRECTORY_NAME.put(Platform.Windows.name(),
				java.io.File.separator + objPropertie.get(DIRECTORY_NAME_FOR_WINDOWS) + java.io.File.separator
						+ objPropertie.get(MONGO_BINARI_WINDOWS_VERSION));

		app.MAP_DIRECTORY_NAME.put(Platform.Linux.name(),
				java.io.File.separator + objPropertie.get(DIRECTORY_NAME_FOR_LINUX) + java.io.File.separator
						+ objPropertie.get(MONGO_BINARI_LINUX_VERSION));

		app.MAP_DIRECTORY_NAME.put(Platform.OS_X.name(),
				java.io.File.separator + objPropertie.get(DIRECTORY_NAME_FOR_MACOS) + java.io.File.separator
						+ objPropertie.get(MONGO_BINARI_MACOS_VERSION));
	}

	public String getROOT_DIR() {
		return ROOT_DIR;
	}

	public void setROOT_DIR(String rOOT_DIR) {
		ROOT_DIR = rOOT_DIR;
	}

	public Map<String, String> getMAP_MONGO_BINARY() {
		return MAP_MONGO_BINARY;
	}

	public Map<String, String> getMAP_DIRECTORY_NAME() {
		return MAP_DIRECTORY_NAME;
	}

	@Override
	public String toString() {
		return MAP_MONGO_BINARY.values().toString() + MAP_DIRECTORY_NAME.values().toString();
	}

}

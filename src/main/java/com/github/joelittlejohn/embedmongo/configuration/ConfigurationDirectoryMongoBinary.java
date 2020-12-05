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
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.yaml.snakeyaml.Yaml;

import de.flapdoodle.embed.process.distribution.Platform;

/**
 * @author rianmachado@gmail.com
 */
public final class ConfigurationDirectoryMongoBinary {

	private static ConfigurationDirectoryMongoBinary instance;

	public static final String EMBEDMONGOHOME = java.nio.file.Paths.get(System.getProperty("user.home"))
			.resolve(".embedmongo").toString();

	private static String mongoBinariWindowsVersion = "mongo-binari-windows-version";
	private static String mongoBinariMacosVersion = "mongo-binari-macos-version";
	private static String mongoBinariLinuxVersion = "mongo-binari-linux-version";

	private static final String DIRECTORYNAMEFORWINDOWS = "directory-name-for-windows";
	private static final String DIRECTORYNAMEFORMACOS = "directory-name-for-macos";
	private static final String DIRECTORYNAMEFORLINUX = "directory-name-for-linux";

	private Map<String, String> mapMongoBinary = new ConcurrentHashMap<>();
	private Map<String, String> mapDirectoryName = new ConcurrentHashMap<>();
	private String rootDir;

	public static ConfigurationDirectoryMongoBinary getInstance() throws IOException {

		synchronized (ConfigurationDirectoryMongoBinary.class) {
			if (instance == null) {
				instance = new ConfigurationDirectoryMongoBinary();
				loadProperties(instance);
			}
			return instance;
		}
	}

	private static void loadProperties(ConfigurationDirectoryMongoBinary app) throws IOException {

		File embedmongoHome = new File(ConfigurationDirectoryMongoBinary.EMBEDMONGOHOME);

		if (!embedmongoHome.exists()) {
			boolean createdHomeDirEmbedmongo = embedmongoHome.mkdir();
			if (!createdHomeDirEmbedmongo) {
				throw new IOException("Directory .embedmongo not created");
			}
		}

		Yaml yaml = new Yaml();
		InputStream inputStream = ConfigurationDirectoryMongoBinary.class.getClassLoader()
				.getResourceAsStream("application.yaml");
		Map<String, Object> objPropertie = yaml.load(inputStream);

		app.rootDir = objPropertie.get("root-dir").toString();

		app.mapMongoBinary.put(Platform.Windows.name(),
				objPropertie.get(DIRECTORYNAMEFORWINDOWS) + "/" + objPropertie.get(mongoBinariWindowsVersion));

		app.mapMongoBinary.put(Platform.Linux.name(),
				objPropertie.get(DIRECTORYNAMEFORLINUX) + "/" + objPropertie.get(mongoBinariLinuxVersion));

		app.mapMongoBinary.put(Platform.OS_X.name(),
				objPropertie.get(DIRECTORYNAMEFORMACOS) + "/" + objPropertie.get(mongoBinariMacosVersion));

		app.mapDirectoryName.put(Platform.Windows.name(),
				java.io.File.separator + objPropertie.get(DIRECTORYNAMEFORWINDOWS) + java.io.File.separator
						+ objPropertie.get(mongoBinariWindowsVersion));

		app.mapDirectoryName.put(Platform.Linux.name(), java.io.File.separator + objPropertie.get(DIRECTORYNAMEFORLINUX)
				+ java.io.File.separator + objPropertie.get(mongoBinariLinuxVersion));

		app.mapDirectoryName.put(Platform.OS_X.name(), java.io.File.separator + objPropertie.get(DIRECTORYNAMEFORMACOS)
				+ java.io.File.separator + objPropertie.get(mongoBinariMacosVersion));
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public Map<String, String> getMapMongoBinary() {
		return mapMongoBinary;
	}
	

	public Map<String, String> getMapDirectoryName() {
		return mapDirectoryName;
	}

	public static void setInstance(ConfigurationDirectoryMongoBinary instance) {
		ConfigurationDirectoryMongoBinary.instance = instance;
	}
	
	

}

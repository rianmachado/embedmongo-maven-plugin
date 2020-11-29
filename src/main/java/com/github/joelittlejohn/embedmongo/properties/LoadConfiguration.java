package com.github.joelittlejohn.embedmongo.properties;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.github.joelittlejohn.embedmongo.constants.ParamLocalDir;

import de.flapdoodle.embed.process.distribution.Platform;
import lombok.Builder;

/**
 * @author rianmachado@gmail.com
 */
@Builder
public final class LoadConfiguration {

	public static String ROOT_DIR;

	static {
		Yaml yaml = new Yaml();
		InputStream inputStream = LoadConfiguration.class.getClassLoader().getResourceAsStream("application.yaml");
		Map<String, Object> objPropertie = yaml.load(inputStream);

		ROOT_DIR = objPropertie.get("root-dir").toString();
		
		ParamLocalDir.MAP_MONGO_BINARY.put(Platform.Windows.name(), 
				objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_WINDOWS) + "/"
						+ objPropertie.get(ParamLocalDir.MONGO_BINARI_WINDOWS_VERSION));

		ParamLocalDir.MAP_MONGO_BINARY.put(Platform.Linux.name(),
				objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_LINUX) + "/"
						+ objPropertie.get(ParamLocalDir.MONGO_BINARI_LINUX_VERSION));

		ParamLocalDir.MAP_MONGO_BINARY.put(Platform.OS_X.name(),
				objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_MACOS) + "/"
						+ objPropertie.get(ParamLocalDir.MONGO_BINARI_MACOS_VERSION));

		ParamLocalDir.MAP_DIRECTORY_NAME.put(Platform.Windows.name(),
				java.io.File.separator + objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_WINDOWS)
						+ java.io.File.separator + objPropertie.get(ParamLocalDir.MONGO_BINARI_WINDOWS_VERSION));

		ParamLocalDir.MAP_DIRECTORY_NAME.put(Platform.Linux.name(),
				java.io.File.separator + objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_LINUX)
						+ java.io.File.separator + objPropertie.get(ParamLocalDir.MONGO_BINARI_LINUX_VERSION));

		ParamLocalDir.MAP_DIRECTORY_NAME.put(Platform.OS_X.name(),
				java.io.File.separator + objPropertie.get(ParamLocalDir.DIRECTORY_NAME_FOR_MACOS)
						+ java.io.File.separator + objPropertie.get(ParamLocalDir.MONGO_BINARI_MACOS_VERSION));


	}

}

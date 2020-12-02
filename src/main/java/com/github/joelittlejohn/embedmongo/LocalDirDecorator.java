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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.joelittlejohn.embedmongo.configuration.ConfigurationDirectoryMongoBinary;

public class LocalDirDecorator implements LocalDir {
	
	private static Logger logger = LoggerFactory.getLogger(LocalDirDecorator.class);

	private LocalDir wrappee;

	LocalDirDecorator(LocalDir localDir) throws IOException {
		ConfigurationDirectoryMongoBinary config = ConfigurationDirectoryMongoBinary.getInstance();
		logger.info(config.toString());
		this.wrappee = localDir;
	}

	@Override
	public String buildPathInputDir() throws IOException {
		return wrappee.buildPathInputDir();
	}

	@Override
	public String buildPathOutputDir() throws IOException {
		return wrappee.buildPathOutputDir();
	}

}

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
package com.github.joelittlejohn.embedmongo.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;

import de.flapdoodle.embed.process.io.IStreamProcessor;

public class FileOutputStreamProcessor implements IStreamProcessor {
	private static BufferedWriter stream;

	private String logFile;
	private String encoding;

	public FileOutputStreamProcessor(String logFile, String encoding) {
		setLogFile(logFile);
		setEncoding(encoding);
	}

	@Override
	public synchronized void process(String block) {
		try {
			if (stream == null) {
				stream = Files.newBufferedWriter(Paths.get(logFile), Charset.forName(encoding.toUpperCase().trim()),
						StandardOpenOption.WRITE);
			}
			stream.write(block);
			stream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onProcessed() {
		process("\n");
	}

	private synchronized void setLogFile(String logFile) {
		if (StringUtils.isAllBlank(logFile)) {
			throw new IllegalArgumentException("no logFile given");
		}
		this.logFile = logFile;
	}

	private synchronized void setEncoding(String encoding) {
		if (StringUtils.isAllBlank(encoding)) {
			throw new IllegalArgumentException("no encoding given");
		}
		this.encoding = encoding;
	}

	public static BufferedWriter getStream() {
		return stream;
	}

	@Override
	public String toString() {
		return "FileOutputStreamProcessor [logFile=" + logFile + ", encoding=" + encoding + "]";
	}
}

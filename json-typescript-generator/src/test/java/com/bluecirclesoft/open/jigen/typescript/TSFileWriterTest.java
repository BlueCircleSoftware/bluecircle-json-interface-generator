/*
 * Copyright 2018 Blue Circle Software, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bluecirclesoft.open.jigen.typescript;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO document me
 */
public class TSFileWriterTest {

	@Test
	public void testRelativize() {
		FileSystem defaultFs = FileSystems.getDefault();
		Path p1;
		Path p2;

		p1 = defaultFs.getPath("./x.txt");
		p2 = defaultFs.getPath("./y.txt");
		Assert.assertEquals(fixPath("./y.txt"), TSFileWriter.relativize(p1, p2).toString());

		p1 = defaultFs.getPath("/a/a/x.txt");
		p2 = defaultFs.getPath("/a/a/y.txt");
		Assert.assertEquals(fixPath("./y.txt"), TSFileWriter.relativize(p1, p2).toString());

		p1 = defaultFs.getPath("/a/a/x.txt");
		p2 = defaultFs.getPath("/a/b/y.txt");
		Assert.assertEquals(fixPath("../b/y.txt"), TSFileWriter.relativize(p1, p2).toString());
	}

	private String fixPath(String s) {
		return s.replace("/", File.separator);
	}
}

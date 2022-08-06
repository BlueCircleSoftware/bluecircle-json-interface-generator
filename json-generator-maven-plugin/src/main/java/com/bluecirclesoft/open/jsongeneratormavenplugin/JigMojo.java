/*
 * Copyright 2022 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jsongeneratormavenplugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * Maven plugin to run the interface generator.
 */
@Mojo(name = "generate-interfaces",
		defaultPhase = LifecyclePhase.PROCESS_CLASSES,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class JigMojo extends AbstractMojo {

	@Parameter
	private List<com.bluecirclesoft.open.jigen.jee7.Options> jeeReaders;

	@Parameter
	private List<com.bluecirclesoft.open.jigen.spring.Options> springReaders;

	@Parameter
	private List<com.bluecirclesoft.open.jigen.typescript.Options> typescriptWriters;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException {

		try {
			Set<URL> urls = new HashSet<>();
			List<String> elements = new ArrayList<>(project.getCompileClasspathElements());
			elements.addAll(project.getRuntimeClasspathElements());
			for (String element : elements) {
				urls.add(new File(element).toURI().toURL());
			}

			ClassLoader contextClassLoader =
					URLClassLoader.newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			Model model = new Model();

			// read any and all Java EE endpoints into the model
			List<String> errors = new ArrayList<>();
			if (jeeReaders != null) {
				getLog().info("Reading Java EE endpoints...");
				for (com.bluecirclesoft.open.jigen.jee7.Options jeeOptions : jeeReaders) {
					com.bluecirclesoft.open.jigen.jee7.Reader jee7Reader = new com.bluecirclesoft.open.jigen.jee7.Reader();
					jee7Reader.acceptOptions(jeeOptions, errors);
					if (!errors.isEmpty()) {
						for (String error : errors) {
							getLog().error(error);
						}
						throw new MojoExecutionException("Errors encountered in JEE processing");
					}
					jee7Reader.model(model);
				}
			}

			// read any and all Spring endpoints into the model
			if (springReaders != null) {
				getLog().info("Reading Spring endpoints...");
				for (com.bluecirclesoft.open.jigen.spring.Options springOptions : springReaders) {
					com.bluecirclesoft.open.jigen.spring.Reader springReader = new com.bluecirclesoft.open.jigen.spring.Reader();
					springReader.acceptOptions(springOptions, errors);
					if (!errors.isEmpty()) {
						for (String error : errors) {
							getLog().error(error);
						}
						throw new MojoExecutionException("Errors encountered in Spring processing");
					}
					springReader.model(model);
				}
			}

			// fix up the model
			model.doGlobalCleanups();

			// write the model out to TypeScript
			if (typescriptWriters != null) {
				getLog().info("Writing TypeScript definitions...");
				for (com.bluecirclesoft.open.jigen.typescript.Options typescriptOptions : typescriptWriters) {
					com.bluecirclesoft.open.jigen.typescript.Writer tsWriter = new com.bluecirclesoft.open.jigen.typescript.Writer();
					tsWriter.acceptOptions(typescriptOptions, errors);
					if (!errors.isEmpty()) {
						for (String error : errors) {
							getLog().error(error);
						}
						throw new MojoExecutionException("Errors encountered in TypeScript  processing");
					}

					tsWriter.output(model);
				}
			}
		} catch (DependencyResolutionRequiredException | IOException e) {
			throw new MojoExecutionException(e);
		}
	}
}

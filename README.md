# bluecircle-json-interface-generator

A utility to read your Java JAX-RS methods, and generate TypeScript interfaces and AJAX calls to use those interfaces.

*THIS IS A WORK IN PROGRESS* - So far, this has only been used internally. Please report any bugs you find.

## Sample usage

This utility needs to be run after your Java is compiled, but before your TypeScript is bundled (if you're doing that).

Currently, you'll need to invoke Java to run the generator utility. TODO - plugin
```xml
            <dependency>
                <groupId>com.bluecirclesoft.open</groupId>
                <artifactId>json-typescript-generator</artifactId>
                <version>0.8</version>
                <scope>test</scope>
            </dependency>
	
			...

            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.4.0</version>
                <executions>
                    <!-- generate TypeScript from JAX-RS classes -->
                    <execution>
                        <id>generateInterface</id>
                        <phase>process-classes</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                            <executable>java</executable>
                            <classpathScope>test</classpathScope>
                            <arguments>
                                <argument>-cp</argument>
                                <classpath/>
                                <argument>com.bluecirclesoft.open.jigen.jeeReader.Main</argument>
                                <argument>--package</argument>
                                <argument>com.bluecirclesoft</argument>
                                <argument>--url-prefix</argument>
                                <argument>/${project.artifactId}</argument>
                                <argument>--output-file</argument>
                                <argument>${basedir}/target/generated-typescript/propmgmt-interface.ts</argument>
                            </arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

```

## Options

Option | Description
-------|------------
--package \<packages\> | List of packages to recursively scan for JAX-RS annotations.
--url-prefix \<prefix\> | Prefix to add to all AJAX URLs (this will probably be the context-root of your application)
--output-file \<file\> | File in which to save the resulting TypeScript file
--typings-index-path \<path\> | Insert a "/// \<reference path="..." />" in the resulting output
--strip-common-packages | By default, TypeScript interfaces are put into a namespace structure which mirrors the Java packages of the source classes.  If --strip-common-packages is selected, then any top-level packages that only contain one subpackage will be removed. For example, if you have com.foo.a.ClassA and com.foo.b.ClassB, then "com" will be skipped, and "foo" will be the top-level namespace in the output. 
--no-immutables | By default, immutable wrappers are generated for all interfaces. Selecting this option disables those wrappers.

## Miscellaneous Weirdness

### What's up with my methods that return String?

If you want to return structured data from a JAX-RS method, you shouldn't return it as String.  You should return an object, which will 
provide structure.

See https://github.com/dropwizard/dropwizard/issues/231 for more info.
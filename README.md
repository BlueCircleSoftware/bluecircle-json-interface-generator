# bluecircle-json-interface-generator

A utility to read your Java JAX-RS methods, and generate TypeScript interfaces and AJAX calls to use those interfaces.

*THIS IS A WORK IN PROGRESS* - So far, this has only been used internally. Bugs, comments, suggestions? Please tell us!

## Version

```xml
	<dependency>
		<groupId>com.bluecirclesoft.open</groupId>
		<artifactId>json-interface-generator</artifactId>
		<version>0.25</version> <!-- latest version -->
	</dependency>
```

## What does it do?

It takes Java JAX-RS code like this:

```java
	@POST
	@Path("/doubleUpBody")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonResponse doubleUpBody(JsonRequest x) {
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setDoubleA(x.getA() + x.getA());
		jsonResponse.setDoubleB(x.getB() + x.getB());
		jsonResponse.setDoubleBoth(x.getA() + x.getB() + x.getA() + x.getB());
		return jsonResponse;
	}

```

and generates TypeScript like this:

```typescript
	export interface JsonRequest {
		a: string | null;
		b: string | null;
	}

	export interface JsonResponse {
		doubleA: string | null;
		doubleB: string | null;
		doubleBoth: string | null;
	}
                    
	export function doubleUpBody(arg0 : com.bluecirclesoft.open.jigen.integration.JsonRequest, options : jsonInterfaceGenerator.JsonOptions<com.bluecirclesoft.open.jigen.integration.JsonResponse>) : void {
		const submitData = JSON.stringify(arg0);
		jsonInterfaceGenerator.callAjax('/testServicesObject/doubleUpBody', 'POST', submitData, true, options);
	}
```

## Sample usage

This utility needs to be run after your Java is compiled, but before your TypeScript is bundled (if you're doing that).

Currently, you'll need to invoke Java to run the generator utility. TODO - plugin
```xml
            <dependency>
                <groupId>com.bluecirclesoft.open</groupId>
                <artifactId>json-jee7-reader</artifactId>
                <version>0.25</version> <!-- latest version -->
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>com.bluecirclesoft.open</groupId>
                <artifactId>json-typescript-generator</artifactId>
                <version>0.25</version> <!-- latest version -->
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
                                <argument>com.bluecirclesoft.open.jigen.Main</argument>
                            </arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

```

## Usage and Options

### General

To launch: 

```
java -cp ... com.bluecirclesoft.open.jigen.Main \
    [ --config <config file> ]
```

Specifies a config file with a number of "input" processors to read and create a JSON model, and "output" processors to create output from 
the resulting model.

If the config file is not specified, the file defaults to ```./jig-config.yaml```

The "input" processors are all run first, followed by the "output" processors.

To specify a processor, you can specify a json-interface-generator package, or a fully-qualified class name.  To write your own 
processor, implement either `com.bluecirclesoft.open.jigen.ModelCreator` for --input or `com.bluecirclesoft.open.jigen.CodeProducer` for 
--output.


### Configuration

Configuration is done through a YAML file. By default, this file is "jig-config.yaml" in the current directory, but the file can be 
overridden by the --config option. The file has the format:

```yaml
readers:
  procesorClass1:
    ...options...
  ...
writers:
  procesorClass2:
    ...options...
  ...
```

The processor class is either a fully qualified class name, or for builtin processors, an abbreviated spec based on the input/output name
 (for example, "jee7")

Example:
```yaml
readers:
  jee7:
    packages:
      - com.bluecirclesoft
writers:
  typescript:
    outputFile: "./target/myOutput.ts"
```

### JEE7 Reader ("readers.jee7"):

Option | Type | Description
-------|------|------------
packages | List\<String>| (required) Array of packages to recursively scan for JAX-RS annotations.
classSubstitutions | List\<ClassSubstitution> | Array of '{ ifSeen: \<class>, replaceWith: \<class>}' When encountering 'ifSeen', substitute 'replaceWith' while building the model.
defaultStringEnums | boolean | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false)
includeSubclasses | boolean | When modelling a class, also model its subclasses (default: true)
 
### TypeScript Generator ("writers.typescript"):

Option | Type | Description
-------|------|------------
outputFile | String | (required) The TypeScript file or folder to generate (path will be created if necessary)
outputStructure | String | Specify how to map packages to TypeScript files: 
 &nbsp; | &nbsp; |   FILES_IN_TREE: one file per package, in a folder hierarchy matching the package hierarchy
 &nbsp; | &nbsp; |   FILES_IN_ONE_FOLDER: one file per package, but all at the top of the output folder (default)
 &nbsp; | &nbsp; |   NAMESPACES: one file for all the output, with namespaces that match the package hierarchy
stripCommonPackages | boolean | Strip any common leading packages from all produced classes. By default, TypeScript interfaces are put into a namespace structure which mirrors the Java packages of the source classes.  If --strip-common-packages is selected, then any top-level packages that only contain one subpackage will be removed. For example, if you have com.foo.a.ClassA and com.foo.b.ClassB, then "com" will be skipped, and "foo" will be the top-level namespace in the output. 
produceImmutables | boolean | Produce immutable wrappers along with interfaces (default: false)
immutableSuffix | String | If producing immutables, this is the suffix to add to the wrapper classes (default: '$Imm')
nullIsUndefined | boolean | Treat nullable fields as also undefined, and mark them optional in interface definitions. (default: false)
useUnknown | boolean | Use the new 'unknown' type in TypeScript 3.0 instead of 'any' (default: false)
## Making AJAX calls

I try to be agnostic as to which AJAX library you're using (if any).  So on startup, you'll need to set jsonInterfaceGenerator.callAjax with
the handler you want to use to invoke AJAX calls. The function you'll need to implement looks like this:
 
```typescript
export interface JsonOptions<R> {
	/**
	 * Is this call async?
	 */
	async?: boolean;

	/**
	 * Completion callback
	 * @param {boolean} success true if error() was not called
	 */
	complete? (success: boolean): void;

	/**
	 * Error callback
	 * @param {string} errorThrown
	 * @returns {any}
	 */
	error? (errorThrown: string): any;

	/**
	 * Success callback
	 * @param {R} data
	 * @returns {any}
	 */
	success? (data: R): any;
}

/**
 * A type for a function that will handle AJAX calls
 */
type AjaxInvoker = (url: string, method: string, data: any, isBodyParam: boolean, options: JsonOptions<any>) => void;
```

Here is the implementation we use for jQuery:

```typescript

jsonInterfaceGenerator.setCallAjax((url: string, method: string, data: any, isBodyParam: boolean, options: JsonOptions<any>) => {
    let error = false;
    let settings: JQueryAjaxSettings = {
        method: method,
        data: data,
        async: options.hasOwnProperty("async") ? options.async : true
    };
    if (options.success) {
        let fn = options.success;
        settings["success"] = (responseData: any, textStatus: string, jqXHR: JQueryXHR) => {
            fn(responseData);
        };
    }
    if (options.error) {
        let fn = options.error;
        settings["error"] = (jqXHR: JQueryXHR, textStatus: string, errorThrown: string) => {
            error = true;
            fn(errorThrown);
        };
    }
    if (options.complete) {
        let fn = options.complete;
        settings["complete"] = (jqXHR: JQueryXHR, textStatus: string) => {
            fn(error);
        };
    }
    if (isBodyParam) {
        settings["contentType"] = "application/json; charset=utf-8";
    } else {
        settings["dataType"] = "json";
    }
    $.ajax(jsonInterfaceGenerator.getPrefix() + url, settings);
});
```

## Miscellaneous Weirdness

### What's up with my methods that return String?

If you want to return structured data from a JAX-RS method, you shouldn't return it as String.  You should return an object, which will 
provide structure.

See https://github.com/dropwizard/dropwizard/issues/231 for more info.
# bluecircle-json-interface-generator

A utility to read your Java JAX-RS methods, and generate TypeScript interfaces and AJAX calls to use those interfaces.

*THIS IS A WORK IN PROGRESS* - So far, this has only been used internally. Bugs, comments, suggestions? Please tell us!

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
                <version>0.13</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>com.bluecirclesoft.open</groupId>
                <artifactId>json-typescript-generator</artifactId>
                <version>0.13</version>
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
                                <argument>--input</argument>
                                <argument>jee7</argument>
                                <argument>--package</argument>
                                <argument>com.bluecirclesoft</argument>
                                <argument>--output</argument>
                                <argument>typescript</argument>
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

## Usage and Options

### General

To launch: 

```
java -cp ... com.bluecirclesoft.open.jigen.Main \
    (--input|--output) <processor> <processor arg>... \
    [ (--input|--output) <processor> <processor arg>... ] ...
```

Specifies a number of "input" processors to read and create a JSON model, and "output" processors to create output from the resulting model.

The "input" processors are all run first, followed by the "output" processors.

To specify a processor, you can specify a json-interface-generator package, or a fully-qualified class name.  To write your own 
processor, implement either `com.bluecirclesoft.open.jigen.ModelCreator` for --input or `com.bluecirclesoft.open.jigen.CodeProducer` for 
--output.

### JEE7 Reader ("--input jee7"):

Option | Description
-------|------------
--package \<packages\> | (required) Comma-separated list of packages to recursively scan for JAX-RS annotations.

### TypeScript Generator ("--output typescript"):

Option | Description
-------|------------
--url-prefix \<prefix\> | (required) Prefix to add to all AJAX URLs (this will probably be the context-root of your application)
--output-file \<file\> | (required) The TypeScript file to generate (path will be created if necessary)
--strip-common-packages | Strip any common leading packages from all produced classes. By default, TypeScript interfaces are put into a namespace structure which mirrors the Java packages of the source classes.  If --strip-common-packages is selected, then any top-level packages that only contain one subpackage will be removed. For example, if you have com.foo.a.ClassA and com.foo.b.ClassB, then "com" will be skipped, and "foo" will be the top-level namespace in the output. 
--immutables | Produce immutable wrappers along with interfaces.

## Making AJAX calls

I try to be agnostic as to which AJAX library you're using (if any).  So on startup, you'll need to set jsonInterfaceGenerator.callAjax with
the handler you want to use to invoke AJAX calls. The function you'll need to implement looks like this:
 
```typescript
export namespace jsonInterfaceGenerator {
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

    /**
     * The ajax caller used by generated code.
     */
    export let callAjax: AjaxInvoker;
}
```

Here is the implementation we use for jQuery:

```typescript

jsonInterfaceGenerator.callAjax = (url: string, method: string, data: any, isBodyParam: boolean, options: JsonOptions<any>) => {
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
};
```

## Miscellaneous Weirdness

### What's up with my methods that return String?

If you want to return structured data from a JAX-RS method, you shouldn't return it as String.  You should return an object, which will 
provide structure.

See https://github.com/dropwizard/dropwizard/issues/231 for more info.
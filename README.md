# bluecircle-json-interface-generator

BC-JIG is a utility to read your Java JAX-RS methods, and generate TypeScript interfaces and AJAX calls to use those interfaces.

*THIS IS A WORK IN PROGRESS* - So far, this has only been used internally. Bugs, comments, suggestions? Please tell us!

## Version

```xml
	<dependency>
		<groupId>com.bluecirclesoft.open</groupId>
		<artifactId>json-interface-generator</artifactId>
		<version>1.1</version> <!-- latest version -->
	</dependency>
```

## What is BC-JIG for? What's the intended use case?

BC-JIG eliminates the need to maintain a separate document describing the API of a Java server, by reflecting upon the server and 
producing stubs in other languages (TypeScript at the moment).

This is highly useful for projects that have a server, written in Java, that has client(s) that are **tightly bound** to 
the server. Tightly bound could mean:

* Whenever the server is deployed, the clients are always deployed as well, or
* You can't imagine multiple clients using different versions of an API, or
* The server's services are internal to the app. Nobody else would use them, nor would you want them to.

Basically, this project is for RESTful Java services that are meant to be internal, and the client and server are updated in lock-step. 
The best example consumer of BC-JIG is a single-page app where the backend is Java, the frontend is TypeScript, and the API used by the 
frontend needs to be rebuilt when the Java is rebuilt.

Also, BC-JIG is extensible: you can implement your own
[ModelCreator](json-interface-generator-core/src/main/java/com/bluecirclesoft/open/jigen/ModelCreator.java) that produces a 
[Model](json-interface-generator-core/src/main/java/com/bluecirclesoft/open/jigen/model/Model.java), and/or implement a 
[CodeProducer](json-interface-generator-core/src/main/java/com/bluecirclesoft/open/jigen/CodeProducer.java) that receives the Model and 
produces stubs in whatever target language you choose. 

## What is BC-JIG *not*?

It is most assuredly *not* [Swagger](https://swagger.io/) or OpenAPI. The scope of Swagger is much different and much more broad.  BC-JIG
is intended as an inside-the-project tool. 

It is also not [JSON Schema](https://json-schema.org/). BC-JIG is about producing JSON that maps (more or less) to Java objects. There are
many things you can express using JSON Schema that Java would not be able to understand, 
[and vice versa.](https://github.com/json-schema-org/json-schema-org.github.io/issues/148)

It is also probably not going to be a good fit for servers other than Java, for a couple of reasons:

* The internal model is built on a sort of synthesis of the Java type system, the TypeScript type system, and the capabilities of JSON. 
Java's and TypeScript's type systems have quite a bit of synergy, especially when it comes to generics. But, the further afield a server 
language gets from the Java type system, the worse the fit with the internal model.
* The Java readers rely on reflection to easily determine the endpoints, and [Jackson](https://github.com/FasterXML/jackson) to inspect 
the parameters/return types and determine the JSON structure to be sent along the wire. If you can't reflect upon the server, then you'll
 spend a lot of time maintaining a separate document that describes the API, and, well, you might as well be using Swagger at that point.

## What does it look like?

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

export namespace JsonRequest {
    export function make(): JsonRequest {
        return {"a": null, "b": null};
    }
}

export interface JsonResponse {
    doubleA: string | null;
    doubleB: string | null;
    doubleBoth: string | null;
}
export namespace JsonResponse {
    export function make() : JsonResponse {
        return {"doubleA":null,"doubleB":null,"doubleBoth":null};
    }
}

export function doubleUpBody(arg0 : JsonRequest, options? : jsonInterfaceGenerator.JsonOptions<JsonResponse>) : Promise<JsonResponse>;
export function doubleUpBody(arg0 : JsonRequest, options? : jsonInterfaceGenerator.JsonOptions<JsonResponse>) : Promise<JsonResponse> {
    const submitData = JSON.stringify(arg0);
    return jsonInterfaceGenerator.callAjax("/testServicesObject/doubleUpBody", "POST", submitData, "json", "application/json", options);
}
```

## Sample usage

Using the Maven plugin:

```xml
    <plugin>
        <groupId>com.bluecirclesoft.open</groupId>
        <artifactId>json-generator-maven-plugin</artifactId>
        <version>${json.generator.version}</version>
        <executions>
            <execution>
                <goals>
                    <goal>generate-interfaces</goal>
                </goals>
                <configuration>
                    <jeeReaders>
                        <jeeReader>
                            <packages>
                                <package>com.foo</package>
                            </packages>
                            <includeSubclasses>true</includeSubclasses>
                        </jeeReader>
                    </jeeReaders>
                    <typescriptWriters>
                        <typescriptWriter>
                            <outputFile>${project.build.directory}/generated-typescript</outputFile>
                        </typescriptWriter>
                    </typescriptWriters>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

## Usage and Options

### Maven

The Maven plugin works as follows:
1. You specify a number of Java EE and/or Spring reader instances, and a number of TypeScript writer instances.
2. These reader instances introspect the Java code finding endpoints and classes, and put all these together into one common "model" 
   (see [Model.java](json-interface-generator-core/src/main/java/com/bluecirclesoft/open/jigen/model/Model.java)).
3. The plugin will then output the entire model using each writer instance.

If you want to output different sets of classes to different folders, or other stuff that doesn't fall into the workflow above, you 
probably want to have multiple executions of the plugin. Each execution will only ever give you one model, which is written out in its 
entirety.

To use the Maven plugin, invoke the plugin as usual in your `build/plugins` section:

```xml
    <plugin>
        <groupId>com.bluecirclesoft.open</groupId>
        <artifactId>json-generator-maven-plugin</artifactId>
        <version>1.1</version> <!-- latest version -->
        <executions>
            <execution>
                <goals>
                    <goal>generate-interfaces</goal>
                </goals>
                <configuration>
                    <jeeReaders>
                         <!-- JEE reader configurations, one <jeeReader/> per config -->
                    </jeeReaders>
                    <springReaders>
                        <!-- Spring reader configurations, one <springReader/> per config -->
                    </springReaders>
                    <typescriptWriters>
                        <!-- TypeScript writer configurations, one <typescriptWriter/> per config -->
                    </typescriptWriters>
                </configuration>
            </execution>
        </executions>
    </plugin>
```
### Maven Configuration

### JEE7 Reader:

See [JEE7 Options.java](json-jee7-reader/src/main/java/com/bluecirclesoft/open/jigen/jee7/Options.java) for the implementation class.

| Option             | Type                     | Description                                                                                            |
|--------------------|--------------------------|--------------------------------------------------------------------------------------------------------|
| packages           | List\<String>            | (required) List of packages to recursively scan for JAX-RS annotations.                                |
| classSubstitutions | List\<ClassSubstitution> | List of substitutions. When encountering 'ifSeen', substitute 'replaceWith' while building the model.  |
| defaultStringEnums | boolean                  | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false) |
| includeSubclasses  | boolean                  | When modelling a class, also model its subclasses (default: true)                                      |

### Spring Reader:

See [Spring Options.java](json-spring-reader/src/main/java/com/bluecirclesoft/open/jigen/spring/Options.java) for the implementation class.

| Option             | Type                     | Description                                                                                            |
|--------------------|--------------------------|--------------------------------------------------------------------------------------------------------|
| packages           | List\<String>            | (required) List of packages to recursively scan for JAX-RS annotations.                                |
| classSubstitutions | List\<ClassSubstitution> | List of substitutions. When encountering 'ifSeen', substitute 'replaceWith' while building the model.  |
| defaultStringEnums | boolean                  | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false) |
| includeSubclasses  | boolean                  | When modelling a class, also model its subclasses (default: true)                                      |
| defaultContentType | String                   | Content type to assume for endpoints if a content type isn't specified                                 |

### TypeScript Generator:

See [TypeScript Options.java](json-typescript-generator/src/main/java/com/bluecirclesoft/open/jigen/typescript/Options.java) for the 
implementation class.

| Option              | Type    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|---------------------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| outputFile          | String  | (required) The TypeScript file or folder to generate (path will be created if necessary)                                                                                                                                                                                                                                                                                                                                                                             |
| outputStructure     | String  | Specify how to map packages to TypeScript files:                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &nbsp;              | &nbsp;  | FILES_IN_TREE: one file per package, in a folder hierarchy matching the package hierarchy                                                                                                                                                                                                                                                                                                                                                                            |
| &nbsp;              | &nbsp;  | FILES_IN_ONE_FOLDER: one file per package, but all at the top of the output folder (default)                                                                                                                                                                                                                                                                                                                                                                         |
| &nbsp;              | &nbsp;  | NAMESPACES: one file for all the output, with namespaces that match the package hierarchy                                                                                                                                                                                                                                                                                                                                                                            |
| stripCommonPackages | boolean | Strip any common leading packages from all produced classes. By default, TypeScript interfaces are put into a namespace structure which mirrors the Java packages of the source classes.  If --strip-common-packages is selected, then any top-level packages that only contain one subpackage will be removed. For example, if you have com.foo.a.ClassA and com.foo.b.ClassB, then "com" will be skipped, and "foo" will be the top-level namespace in the output. |
| produceImmutables   | boolean | Produce immutable wrappers along with interfaces (default: false)                                                                                                                                                                                                                                                                                                                                                                                                    |
| immutableSuffix     | String  | If producing immutables, this is the suffix to add to the wrapper classes (default: '$Imm')                                                                                                                                                                                                                                                                                                                                                                          |
| nullIsUndefined     | boolean | Treat nullable fields as also undefined, and mark them optional in interface definitions. (default: false)                                                                                                                                                                                                                                                                                                                                                           |
| useUnknown          | boolean | Use the new 'unknown' type in TypeScript 3.0 instead of 'any' (default: true)                                                                                                                                                                                                                                                                                                                                                                                        |
| generateHeader      | String  | Don't generate the "jsonInterfaceGenerator.ts" header (useful for a project with a bunch in independent WARS) (default: true)                                                                                                      <br/>                                                                                                                                                                                                                             |

### Command-line + YAML

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


### YAML Configuration

Command-line configuration is done through a YAML file. By default, this file is "jig-config.yaml" in the current directory, but the file 
can be overridden by the --config option. The file has the format:

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

### Jakarta EE Reader ("readers.jakartaee"):

See [Jakarta EE Options.java](json-jakartaee-reader/src/main/java/com/bluecirclesoft/open/jigen/jakartaee/Options.java) for the implementation class.

| Option             | Type                     | Description                                                                                                                          |
|--------------------|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| packages           | List\<String>            | (required) Array of packages to recursively scan for JAX-RS annotations.                                                             |
| classSubstitutions | List\<ClassSubstitution> | Array of '{ ifSeen: \<class>, replaceWith: \<class>}' When encountering 'ifSeen', substitute 'replaceWith' while building the model. |
| defaultStringEnums | boolean                  | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false)                               |
| includeSubclasses  | boolean                  | When modelling a class, also model its subclasses (default: true)                                                                    |
| urlPrefix          | String                   | Prefix to prepend to the generated URL (i.e., for the context path)                                                                  |

### Java EE 7 and 8 Reader ("readers.jee7"):

See [JEE7 Options.java](json-jee7-reader/src/main/java/com/bluecirclesoft/open/jigen/jee7/Options.java) for the implementation class.

| Option             | Type                     | Description                                                                                                                          |
|--------------------|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| packages           | List\<String>            | (required) Array of packages to recursively scan for JAX-RS annotations.                                                             |
| classSubstitutions | List\<ClassSubstitution> | Array of '{ ifSeen: \<class>, replaceWith: \<class>}' When encountering 'ifSeen', substitute 'replaceWith' while building the model. |
| defaultStringEnums | boolean                  | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false)                               |
| includeSubclasses  | boolean                  | When modelling a class, also model its subclasses (default: true)                                                                    |
| urlPrefix          | String                   | Prefix to prepend to the generated URL (i.e., for the context path)                                                                  |

### Spring 5 Reader ("readers.spring"):

See [Spring Options.java](json-spring-reader/src/main/java/com/bluecirclesoft/open/jigen/spring/Options.java) for the implementation class.

| Option             | Type                     | Description                                                                                            |
|--------------------|--------------------------|--------------------------------------------------------------------------------------------------------|
| packages           | List\<String>            | (required) List of packages to recursively scan for JAX-RS annotations.                                |
| classSubstitutions | List\<ClassSubstitution> | List of substitutions. When encountering 'ifSeen', substitute 'replaceWith' while building the model.  |
| defaultStringEnums | boolean                  | Unless otherwise specified, treat enums as 'string' enums, instead of integer-valued. (default: false) |
| includeSubclasses  | boolean                  | When modelling a class, also model its subclasses (default: true)                                      |
| defaultContentType | String                   | Content type to assume for endpoints if a content type isn't specified                                 |
| urlPrefix          | String                   | Prefix to prepend to the generated URL (i.e., for the context path)                                    |

### TypeScript Generator ("writers.typescript"):

See [TypeScript Options.java](json-typescript-generator/src/main/java/com/bluecirclesoft/open/jigen/typescript/Options.java) for the
implementation class.

| Option              | Type    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|---------------------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| outputFile          | String  | (required) The TypeScript file or folder to generate (path will be created if necessary)                                                                                                                                                                                                                                                                                                                                                                             |
| outputStructure     | String  | Specify how to map packages to TypeScript files:                                                                                                                                                                                                                                                                                                                                                                                                                     |
| &nbsp;              | &nbsp;  | FILES_IN_TREE: one file per package, in a folder hierarchy matching the package hierarchy                                                                                                                                                                                                                                                                                                                                                                            |
| &nbsp;              | &nbsp;  | FILES_IN_ONE_FOLDER: one file per package, but all at the top of the output folder (default)                                                                                                                                                                                                                                                                                                                                                                         |
| &nbsp;              | &nbsp;  | NAMESPACES: one file for all the output, with namespaces that match the package hierarchy                                                                                                                                                                                                                                                                                                                                                                            |
| stripCommonPackages | boolean | Strip any common leading packages from all produced classes. By default, TypeScript interfaces are put into a namespace structure which mirrors the Java packages of the source classes.  If --strip-common-packages is selected, then any top-level packages that only contain one subpackage will be removed. For example, if you have com.foo.a.ClassA and com.foo.b.ClassB, then "com" will be skipped, and "foo" will be the top-level namespace in the output. |
| produceImmutables   | boolean | Produce immutable wrappers along with interfaces (default: false)                                                                                                                                                                                                                                                                                                                                                                                                    |
| immutableSuffix     | String  | If producing immutables, this is the suffix to add to the wrapper classes (default: '$Imm')                                                                                                                                                                                                                                                                                                                                                                          |
| nullIsUndefined     | boolean | Treat nullable fields as also undefined, and mark them optional in interface definitions. (default: false)                                                                                                                                                                                                                                                                                                                                                           |
| useUnknown          | boolean | Use the new 'unknown' type in TypeScript 3.0 instead of 'any' (default: true)                                                                                                                                                                                                                                                                                                                                                                                        |
| generateHeader      | String  | Don't generate the "jsonInterfaceGenerator.ts" header (useful for a project with a bunch in independent WARS) (default: true)                                                                                                      <br/>                                                                                                                                                                                                                             |

## Making AJAX calls

I try to be agnostic as to which AJAX library you're using (if any).  So on startup, you'll need to set jsonInterfaceGenerator.callAjax with
the handler you want to use to invoke AJAX calls.

Your job is to make a function that returns a `Promise` to actually make an AJAX call and resolve to the response object. You may use 
whatever method you'd like. 

```typescript
/**
 * A type for a function that will handle AJAX calls
 * @param url the URL to send the request to
 * @param method the HTTP method
 * @param data the data to send to the server (will be an empty object for bodyless requests e.g. GET or DELETE)
 * @param bodyType "json" for JSON, "form" for url-encoded form data, "none" for bodyless requests
 * @param consumes the endpoint's listed "consumes" MIME type
 */
type AjaxInvoker<T> = (url: string, method: string, data: UnknownType, bodyType: BodyType, consumes: string | null) => Promise<T>;

export function setCallAjax(newCallAjax: AjaxInvoker<UnknownType>): void {
    callAjaxFn = newCallAjax;
}
```

Here is an example implementation for jQuery:

```typescript

jsonInterfaceGenerator.setCallAjax((url: string,
                                    method: string,
                                    data: any,
                                    bodyType: BodyType,
                                    consumes: string | null) => {
    return new Promise((resolve, reject) => {
        const settings: JQueryAjaxSettings = {
            async: true,
            data,
            method,
        };
        settings.success = (responseData: any, textStatus: string, jqXHR: JQueryXHR) => {
            resolve(responseData);
        };
        settings.error = (jqXHR: JQueryXHR, textStatus: string, errorThrown: string) => {
            reject(new Error(errorThrown));
        };
        switch (bodyType) {
            case "json":
                if (consumes !== null) {
                    settings.headers = {"Content-Type": consumes};
                }
                settings.dataType = "json";
                break;
            case "form":
                settings.enctype = "application/x-www-form-urlencoded";
                break;
            case "none":
                break;
            default:
                throw new Error("unhandled body type " + bodyType);
        }

        $.ajax(jsonInterfaceGenerator.getPrefix() + url, settings);
    });
});
```

## Miscellaneous Weirdness

### What's up with my methods that return String?

If you want to return structured data from a JAX-RS method, you shouldn't return it as String.  You should return an object, which will 
provide structure.

See https://github.com/dropwizard/dropwizard/issues/231 for more info.

### Spring content type on GET requests

If you put `consumes = MediaType.APPLICATION_JSON_VALUE` on a GET endpoint, newer versions of Spring _will_ expect you to send the 
`Content-Type: application/json` header, even though a GET request has no content! Seems like best practice should be to leave off the 
`consumes` on the server, and the `Content-Type` header on the request, since the header is optional, and conveys no functional meaning in 
this situation.

The spring-reader package will read When you write your AJAX handler, be sure to use the provided "consumes" parameter to set your 
Content-Type header when appropriate.
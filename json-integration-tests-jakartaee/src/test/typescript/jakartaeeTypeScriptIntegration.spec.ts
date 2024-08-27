/*
 * Copyright 2017 Blue Circle Software, LLC
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
 */

import * as $ from "jquery";
import {com, jsonInterfaceGenerator} from "../../../target/generated-sources/jakartaeeToTypeScript";
import {BodyType} from "../../../../json-typescript-generator/src/main/resources/header";
import JsonOptions = jsonInterfaceGenerator.JsonOptions;
import integrationJakartaee = com.bluecirclesoft.open.jigen.integrationJakartaee;

declare const __karma__: any;

function ck<T>(val: T | undefined | null): T {
    if (val === undefined || val === null) {
        throw new Error("undefined or null " + val);
    }
    return val;
}

jsonInterfaceGenerator.setCallAjax((url: string,
                                    method: string,
                                    data: any,
                                    bodyType: BodyType,
                                    consumes: string | null) => {
    return new Promise((resolve, reject) => {
        console.log("--> Initiating call to ", url, " with method ", method);
        let error = false;
        const settings: JQueryAjaxSettings = {
            async: true,
            data,
            method,
        };
        settings.success = (responseData: any, textStatus: string, jqXHR: JQueryXHR) => {
            resolve(responseData);
        };
        settings.error = (jqXHR: JQueryXHR, textStatus: string, errorThrown: string) => {
            console.error("Error!");
            console.error("url: ", url);
            console.error("jqXHR.status: ", jqXHR.status);
            console.error("jqXHR.readyState: ", jqXHR.readyState);
            console.error("textStatus: ", textStatus);
            console.error("errorThrown: ", errorThrown);
            error = true;
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

(window as any).$ = $;
(window as any).jQuery = $;

// pull base URL from command line
const baseUrl = __karma__.config.baseUrl;
console.log("Initializing with base URL of ", baseUrl);
jsonInterfaceGenerator.init(baseUrl);

describe("test @JsonProperty on enums", () => {
    it("has correct enum names", () => {
        let val: integrationJakartaee.testPackage2.EnumB = integrationJakartaee.testPackage2.EnumB.NUMBER_ONE;
        expect(val === integrationJakartaee.testPackage2.EnumB.NUMBER_ONE).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values.NumeroUno).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values[0]).toBeTruthy();
        val = integrationJakartaee.testPackage2.EnumB.NUMBER_TWO;
        expect(val === integrationJakartaee.testPackage2.EnumB.NUMBER_TWO).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values.NumeroDos).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values[2]).toBeTruthy();
        val = integrationJakartaee.testPackage2.EnumB.NUMBER_THREE;
        expect(val === integrationJakartaee.testPackage2.EnumB.NUMBER_THREE).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values.NumeroTres).toBeTruthy();
        expect(val === integrationJakartaee.testPackage2.EnumB_values[1]).toBeTruthy();
    });
});

describe("test TestServicesString", () => {

    // Standard handler
    const simpleHandler: JsonOptions<unknown> = {
        complete: () => {
            console.log("complete");
        },
        error: (errorThrown: string) => {
            console.log("errorThrown=", errorThrown);
        },
        success: (s: unknown) => {
            console.log("success: result ", s);
        },
    }

    it("can execute doubleUpGetQ", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleUpGetQ("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpGetP", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleUpGetP("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    // TODO figure out how to get JQuery to pass query params when doing POST
    // it("can execute doubleUpPostQ", () => {
    //     let result: string = "";
    //     integration.TestServicesString.doubleUpPostQ("abc", simpleHandler((s: string) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual("abcabc");
    // });

    it("can execute doubleUpPostP", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleUpPostP("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpPostF", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleUpPostF("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleArrGetQ", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleArrGetQ("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpGetP", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleArrGetP("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    // TODO figure out how to get JQuery to pass query params when doing POST
    // it("can execute doubleUpPostQ", () => {
    //     let result: string[] = [];
    //     integration.TestServicesString.doubleArrPostQ("abc", simpleHandler((s: string[]) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual(["abc", "abc", "abc"]);
    // });

    it("can execute doubleUpPostP", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleArrPostP("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpPostF", async () => {
        let result = await integrationJakartaee.TestServicesString.doubleArrPostF("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });
});

describe("test TestServicesObject", () => {

    // Standard handler
    const simpleHandler: JsonOptions<unknown> = {
        complete: () => {
            console.log("complete");
        },
        error: (errorThrown: string) => {
            console.log("errorThrown=", errorThrown);
        },
        success: (s: unknown) => {
            console.log("success: result ", s);
        },
    }

    it("can execute doubleUpBody", async () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/doubleUpBody");

        const arg0 = {
            a: "one",
            b: "two",
        };
        console.log("Data: " + JSON.stringify(arg0));
        let result = await integrationJakartaee.TestServicesObject.doubleUpBody(arg0, simpleHandler);
        expect(result).toEqual({doubleA: "oneone", doubleB: "twotwo", doubleBoth: "onetwoonetwo"});
    });

    it("can execute doubleUpNested", async () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/doubleUpNested");

        const arg0: integrationJakartaee.NestedOuter = {
            a: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            b: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            c: 12,
            d: "qwerty",
        };
        console.log("Data: " + JSON.stringify(arg0));
        let result = await integrationJakartaee.TestServicesObject.doubleUpNested(arg0, simpleHandler);
        expect(result).toEqual({...arg0, c: 24, d: "qwertyqwerty"});
    });

    it("can execute getClassB", async () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/getClassB");

        let result = await integrationJakartaee.TestServicesObject.getClassB(simpleHandler);
    });

    it("can use immutables", async () => {
        const base: integrationJakartaee.NestedOuter = {
            a: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            b: {a: "ef", b: "gh", c: 3, d: 4, e: [4, 5, 6]},
            c: 12,
            d: "qwerty",
        };

        function asrt<T>(input: T | null | undefined): T {
            if (input === null || input === undefined) {
                throw new Error("was not defined");
            }
            return input;
        }

        {
            const root = new jsonInterfaceGenerator.ChangeRoot<integrationJakartaee.NestedOuter>(base);
            const imm = new integrationJakartaee.NestedOuter$Imm(root);
            expect(imm.d).toEqual("qwerty");
            const b = asrt(imm.b);
            expect(b.b).toEqual("gh");
            const ver1 = root.current;
            b.b = "ij";
            expect(b.b).toEqual("ij");
            const ver2 = root.current;
            expect(root.history.length).toEqual(2);
            expect(ver1 === ver2).toBeFalsy("version not updated");
            expect(root.history[1]).toEqual(ver1);
            expect(root.history[0]).toEqual(ver2);

            const eAcc = asrt(b.e);
            expect(eAcc.get(1)).toEqual(5);
            eAcc.set(1, 23);
            expect(root.history.length).toEqual(3);
            expect(eAcc.get(1)).toEqual(23);

            imm.d = null;
            expect(root.history.length).toEqual(4);
            expect(imm.d).toEqual(null);
        }

        // again, but with watchers
        {
            let dChange = 0;
            let bChange = 0;
            let bbChange = 0;
            const root = new jsonInterfaceGenerator.ChangeRoot<integrationJakartaee.NestedOuter>(base);
            root.watch(["b"], () => bChange++);
            root.watch(["b", "b"], () => bbChange++);
            const imm = new integrationJakartaee.NestedOuter$Imm(root);
            imm.$watch("d", (newVal, oldVal, path): void => {
                console.log("Change: ", oldVal, " -> ", newVal, " at ", path);
                dChange++;
            });
            console.log("Root now ", root);
            expect(imm.d).toEqual("qwerty");
            const b = asrt(imm.b);
            expect(b.b).toEqual("gh");
            const ver1 = root.current;
            b.b = "ij"; // b: 1, b.b: 1
            expect(b.b).toEqual("ij");
            const ver2 = root.current;
            expect(root.history.length).toEqual(2);
            expect(ver1 === ver2).toBeFalsy("version not updated");
            expect(root.history[1]).toEqual(ver1);
            expect(root.history[0]).toEqual(ver2);

            const eAcc = asrt(b.e);
            expect(eAcc.get(1)).toEqual(5);
            eAcc.set(1, 23); // b: 2 b.e: 1 b.e.1: 1
            expect(root.history.length).toEqual(3);
            expect(eAcc.get(1)).toEqual(23);

            imm.d = null; // d: 1
            expect(root.history.length).toEqual(4);
            expect(imm.d).toEqual(null);

            expect(dChange).toBe(1);
            expect(bChange).toBe(2);
            expect(bbChange).toBe(1);
        }

    });

    it("can handle subclasses", async () => {

        // These will be compilation errors if something's wrong
        let sub1Instance = integrationJakartaee.testPackage3.Sub1.make();
        let superRef: integrationJakartaee.testPackage3.Super = sub1Instance;
        let hyperRef: integrationJakartaee.testPackage3.Hyper = sub1Instance;

        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/getGenericListSupers");
        let result = ck(await integrationJakartaee.TestServicesObject.getGenericListSupers(simpleHandler));

        expect(integrationJakartaee.testPackage3.Sub1.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Sub2.isInstance(ck(ck(result).list)[0])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub3.isInstance(ck(ck(result).list)[0])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub1.isInstance(ck(ck(result).list)[1])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub2.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Sub3.isInstance(ck(ck(result).list)[1])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub1.isInstance(ck(ck(result).list)[2])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub2.isInstance(ck(ck(result).list)[2])).toBeFalsy();
        expect(integrationJakartaee.testPackage3.Sub3.isInstance(ck(ck(result).list)[2])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Super.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Super.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Super.isInstance(ck(ck(result).list)[2])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Hyper.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Hyper.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJakartaee.testPackage3.Hyper.isInstance(ck(ck(result).list)[2])).toBeTruthy();
    })
});

describe("test TestAllCombosTwoParameters", () => {

    // Standard handler
    const simpleHandler: JsonOptions<unknown> = {
        complete: (success: boolean) => {
            console.log("complete: success ", success);
        },
        error: (errorThrown: string) => {
            console.log("errorThrown=", errorThrown);
        },
        success: (s: unknown) => {
            console.log("success: result ", s);
        },
    };

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeFoFo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeFoFo('p0', 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a":"P0P1","b":"P0P1","c":"P0P1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGePaFo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaFo('p0', 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a":"P0P1","b":"P0P1","c":"P0P1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeQuFo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuFo('p0', 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a":"P0P1","b":"P0P1","c":"P0P1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeBoFo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeBoFo({
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCP1", "b": "DEFP1", "c": "GHIP1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeFoPa", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeFoPa('p0', 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a":"P0P1","b":"P0P1","c":"P0P1"});
    // });

    it("can execute testAllCombosTwoParametersGePaPa", async () => {
        let result: object = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaPa("p0",
            "p1",
            simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersGeQuPa", async () => {
        let result: object = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuPa("p0",
            "p1",
            simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    // MALFORMED - no Body on GET
    // it("can execute testAllCombosTwoParametersGeBoPa", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeBoPa({
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCP1", "b": "DEFP1", "c": "GHIP1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeFoQu", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeFoQu('p0', 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a":"P0P1","b":"P0P1","c":"P0P1"});
    // });

    it("can execute testAllCombosTwoParametersGePaQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersGeQuQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    // MALFORMED - no Body on GET
    // it("can execute testAllCombosTwoParametersGeBoQu", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeBoQu({
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCP1", "b": "DEFP1", "c": "GHIP1"});
    // });

    // MALFORMED - no @FormParam on GET
    // it("can execute testAllCombosTwoParametersGeFoBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeFoBo('p0', {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "P0ABC", "b": "P0DEF", "c": "P0GHI"});
    // });

    // MALFORMED - no Body on GET
    // it("can execute testAllCombosTwoParametersGePaBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaBo('p0', {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "P0ABC", "b": "P0DEF", "c": "P0GHI"});
    // });

    // MALFORMED - no Body on GET
    // it("can execute testAllCombosTwoParametersGeQuBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuBo('p0', {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "P0ABC", "b": "P0DEF", "c": "P0GHI"});
    // });

    // MALFORMED - no Body on GET
    // it("can execute testAllCombosTwoParametersGeBoBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersGeBoBo({"a": "abc", "b": "def", "c": "ghi"}, {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCABC", "b": "DEFDEF", "c": "GHIGHI"});
    // });

    it("can execute testAllCombosTwoParametersPoFoFo", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoFo("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaFo", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaFo("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuFo", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuFo("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    // MALFORMED - can't have body param and form param
    // it("can execute testAllCombosTwoParametersPoBoFo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoFo({
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, 'p1', simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCP1", "b": "DEFP1", "c": "GHIP1"});
    // });

    it("can execute testAllCombosTwoParametersPoFoPa", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaPa", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuPa", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoBoPa", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoPa({
            a: "abc",
            b: "def",
            c: "ghi",
        }, "p1", simpleHandler);
        expect(result).toEqual({a: "ABCP1", b: "DEFP1", c: "GHIP1"});
    });

    it("can execute testAllCombosTwoParametersPoFoQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoBoQu", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoQu({
            a: "abc",
            b: "def",
            c: "ghi",
        }, "p1", simpleHandler);
        expect(result).toEqual({a: "ABCP1", b: "DEFP1", c: "GHIP1"});
    });

    // MALFORMED - can't have body param and form param
    // it("can execute testAllCombosTwoParametersPoFoBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoBo('p0', {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "P0ABC", "b": "P0DEF", "c": "P0GHI"});
    // });

    it("can execute testAllCombosTwoParametersPoPaBo", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaBo("p0", {
            a: "abc",
            b: "def",
            c: "ghi",
        }, simpleHandler);
        expect(result).toEqual({a: "P0ABC", b: "P0DEF", c: "P0GHI"});
    });

    it("can execute testAllCombosTwoParametersPoQuBo", async () => {
        let result = await integrationJakartaee.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuBo("p0", {
            a: "abc",
            b: "def",
            c: "ghi",
        }, simpleHandler);
        expect(result).toEqual({a: "P0ABC", b: "P0DEF", c: "P0GHI"});
    });

    // MALFORMED - can't have more than one Body param
    // it("can execute testAllCombosTwoParametersPoBoBo", () => {
    //     let result: object = {};
    //     integration.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoBo({"a": "abc", "b": "def", "c": "ghi"}, {
    //         "a": "abc",
    //         "b": "def",
    //         "c": "ghi"
    //     }, simpleHandler((s: object) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual({"a": "ABCABC", "b": "DEFDEF", "c": "GHIGHI"});
    // });

});

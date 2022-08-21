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
import {com, jsonInterfaceGenerator} from "../../../target/generated-sources/jeeToTypeScript";
import JsonOptions = jsonInterfaceGenerator.JsonOptions;
import integrationJee7 = com.bluecirclesoft.open.jigen.integrationJee7;

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
                                    isBodyParam: boolean,
                                    onSuccess: (data: any) => void,
                                    onFailure: (errorMsg: string) => void) => {
    let error = false;
    const settings: JQueryAjaxSettings = {
        async: true,
        data,
        method,
    };
    settings.success = (responseData: any, textStatus: string, jqXHR: JQueryXHR) => {
        onSuccess(responseData);
    };
    settings.error = (jqXHR: JQueryXHR, textStatus: string, errorThrown: string) => {
        console.error("Error!");
        console.error("jqXHR: ", jqXHR.status);
        console.error("textStatus: ", textStatus);
        error = true;
        onFailure(textStatus);
    };
    if (isBodyParam) {
        settings.headers = {"Content-Type": "application/json; charset=utf-8"};
    }
    settings.dataType = "json";
    $.ajax(jsonInterfaceGenerator.getPrefix() + url, settings);
});

(window as any).$ = $;
(window as any).jQuery = $;

// pull base URL from command line
jsonInterfaceGenerator.init(__karma__.config.baseUrl);

describe("test @JsonProperty on enums", () => {
    it("has correct enum names", () => {
        let val: integrationJee7.testPackage2.EnumB = integrationJee7.testPackage2.EnumB.NUMBER_ONE;
        expect(val === integrationJee7.testPackage2.EnumB.NUMBER_ONE).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values.NumeroUno).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values[0]).toBeTruthy();
        val = integrationJee7.testPackage2.EnumB.NUMBER_TWO;
        expect(val === integrationJee7.testPackage2.EnumB.NUMBER_TWO).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values.NumeroDos).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values[2]).toBeTruthy();
        val = integrationJee7.testPackage2.EnumB.NUMBER_THREE;
        expect(val === integrationJee7.testPackage2.EnumB.NUMBER_THREE).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values.NumeroTres).toBeTruthy();
        expect(val === integrationJee7.testPackage2.EnumB_values[1]).toBeTruthy();
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
        let result = await integrationJee7.TestServicesString.doubleUpGetQ("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpGetP", async () => {
        let result = await integrationJee7.TestServicesString.doubleUpGetP("abc", simpleHandler);
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
        let result = await integrationJee7.TestServicesString.doubleUpPostP("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpPostF", async () => {
        let result = await integrationJee7.TestServicesString.doubleUpPostF("abc", simpleHandler);
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleArrGetQ", async () => {
        let result = await integrationJee7.TestServicesString.doubleArrGetQ("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpGetP", async () => {
        let result = await integrationJee7.TestServicesString.doubleArrGetP("abc", simpleHandler);
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
        let result = await integrationJee7.TestServicesString.doubleArrPostP("abc", simpleHandler);
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpPostF", async () => {
        let result = await integrationJee7.TestServicesString.doubleArrPostF("abc", simpleHandler);
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
        let result = await integrationJee7.TestServicesObject.doubleUpBody(arg0, simpleHandler);
        expect(result).toEqual({doubleA: "oneone", doubleB: "twotwo", doubleBoth: "onetwoonetwo"});
    });

    it("can execute doubleUpNested", async () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/doubleUpNested");

        const arg0: integrationJee7.NestedOuter = {
            a: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            b: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            c: 12,
            d: "qwerty",
        };
        console.log("Data: " + JSON.stringify(arg0));
        let result = await integrationJee7.TestServicesObject.doubleUpNested(arg0, simpleHandler);
        expect(result).toEqual({...arg0, c: 24, d: "qwertyqwerty"});
    });

    it("can execute getClassB", async () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/getClassB");

        let result = await integrationJee7.TestServicesObject.getClassB(simpleHandler);
    });

    it("can use immutables", async () => {
        const base: integrationJee7.NestedOuter = {
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
            const root = new jsonInterfaceGenerator.ChangeRoot<integrationJee7.NestedOuter>(base);
            const imm = new integrationJee7.NestedOuter$Imm(root);
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
            const root = new jsonInterfaceGenerator.ChangeRoot<integrationJee7.NestedOuter>(base);
            root.watch(["b"], () => bChange++);
            root.watch(["b", "b"], () => bbChange++);
            const imm = new integrationJee7.NestedOuter$Imm(root);
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
        let sub1Instance = integrationJee7.testPackage3.Sub1.make();
        let superRef: integrationJee7.testPackage3.Super = sub1Instance;
        let hyperRef: integrationJee7.testPackage3.Hyper = sub1Instance;

        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + "/testServicesObject/getGenericListSupers");
        let result = ck(await integrationJee7.TestServicesObject.getGenericListSupers(simpleHandler));

        expect(integrationJee7.testPackage3.Sub1.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJee7.testPackage3.Sub2.isInstance(ck(ck(result).list)[0])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub3.isInstance(ck(ck(result).list)[0])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub1.isInstance(ck(ck(result).list)[1])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub2.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJee7.testPackage3.Sub3.isInstance(ck(ck(result).list)[1])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub1.isInstance(ck(ck(result).list)[2])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub2.isInstance(ck(ck(result).list)[2])).toBeFalsy();
        expect(integrationJee7.testPackage3.Sub3.isInstance(ck(ck(result).list)[2])).toBeTruthy();
        expect(integrationJee7.testPackage3.Super.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJee7.testPackage3.Super.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJee7.testPackage3.Super.isInstance(ck(ck(result).list)[2])).toBeTruthy();
        expect(integrationJee7.testPackage3.Hyper.isInstance(ck(ck(result).list)[0])).toBeTruthy();
        expect(integrationJee7.testPackage3.Hyper.isInstance(ck(ck(result).list)[1])).toBeTruthy();
        expect(integrationJee7.testPackage3.Hyper.isInstance(ck(ck(result).list)[2])).toBeTruthy();
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
        let result: object = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersGeQuPa", async () => {
        let result: object = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuPa("p0", "p1", simpleHandler);
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
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersGePaQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersGeQuQu", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersGeQuQu("p0", "p1", simpleHandler);
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
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoFo("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaFo", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaFo("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuFo", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuFo("p0", "p1", simpleHandler);
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
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaPa", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuPa", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuPa("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoBoPa", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoPa({
            a: "abc",
            b: "def",
            c: "ghi",
        }, "p1", simpleHandler);
        expect(result).toEqual({a: "ABCP1", b: "DEFP1", c: "GHIP1"});
    });

    it("can execute testAllCombosTwoParametersPoFoQu", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoFoQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoPaQu", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoQuQu", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuQu("p0", "p1", simpleHandler);
        expect(result).toEqual({a: "P0P1", b: "P0P1", c: "P0P1"});
    });

    it("can execute testAllCombosTwoParametersPoBoQu", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoBoQu({
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
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoPaBo("p0", {
            a: "abc",
            b: "def",
            c: "ghi",
        }, simpleHandler);
        expect(result).toEqual({a: "P0ABC", b: "P0DEF", c: "P0GHI"});
    });

    it("can execute testAllCombosTwoParametersPoQuBo", async () => {
        let result = await integrationJee7.TestAllCombosTwoParameters.testAllCombosTwoParametersPoQuBo("p0", {
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

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

declare const __karma__: any;
import JsonOptions = jsonInterfaceGenerator.JsonOptions;

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

(<any>window).$ = $;
(<any>window).jQuery = $;

// pull base URL from command line
jsonInterfaceGenerator.init(__karma__.config.baseUrl);

describe("test TestServicesString", () => {

    // Standard handler
    function simpleHandler<T>(handler: (x: T) => void): JsonOptions<T> {
        return {
            success: (s: T) => {
                console.log("success: result ", s);
                handler(s);
            },
            error: (errorThrown: string) => {
                console.log("errorThrown=", errorThrown);
            },
            complete: () => {
                console.log("complete");
            },
            async: false
        };
    }

    it("can execute doubleUpGetQ", () => {
        let result: string = "";
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleUpGetQ("abc", simpleHandler((s: string) => {
            result = s;
        }));
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpGetP", () => {
        let result: string = "";
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleUpGetP("abc", simpleHandler((s: string) => {
            result = s;
        }));
        expect(result).toEqual("abcabc");
    });

    // TODO figure out how to get JQuery to pass query params when doing POST
    // it("can execute doubleUpPostQ", () => {
    //     let result: string = "";
    //     com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleUpPostQ("abc", simpleHandler((s: string) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual("abcabc");
    // });

    it("can execute doubleUpPostP", () => {
        let result: string = "";
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleUpPostP("abc", simpleHandler((s: string) => {
            result = s;
        }));
        expect(result).toEqual("abcabc");
    });

    it("can execute doubleUpPostF", () => {
        let result: string = "";
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleUpPostF("abc", simpleHandler((s: string) => {
            result = s;
        }));
        expect(result).toEqual("abcabc");
    });


    it("can execute doubleArrGetQ", () => {
        let result: string[] = [];
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleArrGetQ("abc", simpleHandler((s: string[]) => {
            result = s;
        }));
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpGetP", () => {
        let result: string[] = [];
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleArrGetP("abc", simpleHandler((s: string[]) => {
            result = s;
        }));
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    // TODO figure out how to get JQuery to pass query params when doing POST
    // it("can execute doubleUpPostQ", () => {
    //     let result: string[] = [];
    //     com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleArrPostQ("abc", simpleHandler((s: string[]) => {
    //         result = s;
    //     }));
    //     expect(result).toEqual(["abc", "abc", "abc"]);
    // });

    it("can execute doubleUpPostP", () => {
        let result: string[] = [];
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleArrPostP("abc", simpleHandler((s: string[]) => {
            result = s;
        }));
        expect(result).toEqual(["abc", "abc", "abc"]);
    });

    it("can execute doubleUpPostF", () => {
        let result: string[] = [];
        com.bluecirclesoft.open.jigen.integration.TestServicesString.doubleArrPostF("abc", simpleHandler((s: string[]) => {
            result = s;
        }));
        expect(result).toEqual(["abc", "abc", "abc"]);
    });
});

describe("test TestServicesObject", () => {

    // Standard handler
    function simpleHandler<T>(handler: (x: T) => void): JsonOptions<T> {
        return {
            success: (s: T) => {
                console.log("success: result ", s);
                handler(s);
            },
            error: (errorThrown: string) => {
                console.log("errorThrown=", errorThrown);
            },
            complete: () => {
                console.log("complete");
            },
            async: false
        };
    }

    it("can execute doubleUpBody", () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + '/testServicesObject/doubleUpBody');


        let result: com.bluecirclesoft.open.jigen.integration.JsonResponse = {doubleA: null, doubleB: null, doubleBoth: null};
        let arg0 = {
            a: "one",
            b: "two"
        };
        console.log("Data: " + JSON.stringify(arg0));
        com.bluecirclesoft.open.jigen.integration.TestServicesObject.doubleUpBody(arg0, simpleHandler((s: com.bluecirclesoft.open.jigen.integration.JsonResponse) => {
            result = s;
        }));
        expect(result).toEqual({doubleA: "oneone", doubleB: "twotwo", doubleBoth: "onetwoonetwo"});
    });

    it("can execute doubleUpNested", () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + '/testServicesObject/doubleUpNested');


        let result: com.bluecirclesoft.open.jigen.integration.NestedOuter | undefined;
        let arg0: com.bluecirclesoft.open.jigen.integration.NestedOuter = {
            a: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            b: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            c: 12,
            d: "qwerty"
        };
        console.log("Data: " + JSON.stringify(arg0));
        com.bluecirclesoft.open.jigen.integration.TestServicesObject.doubleUpNested(arg0, simpleHandler((s: com.bluecirclesoft.open.jigen.integration.NestedOuter) => {
            result = s;
        }));
        expect(result).toEqual({...arg0, c: 24, d: "qwertyqwerty"});
    });

    it("can execute getClassB", () => {
        console.log("URL: " + jsonInterfaceGenerator.getPrefix() + '/testServicesObject/getClassB');


        let result: com.bluecirclesoft.open.jigen.integration.testPackage2.ClassB | undefined;
        com.bluecirclesoft.open.jigen.integration.TestServicesObject.getClassB(simpleHandler((s: com.bluecirclesoft.open.jigen.integration.testPackage2.ClassB) => {
            result = s;
        }));
    });

    it("can use immutables", () => {
        let base: com.bluecirclesoft.open.jigen.integration.NestedOuter = {
            a: {a: "ab", b: "cd", c: 1, d: 2, e: [1, 2, 3]},
            b: {a: "ef", b: "gh", c: 3, d: 4, e: [4, 5, 6]},
            c: 12,
            d: "qwerty"
        };

        let root = new jsonInterfaceGenerator.ChangeRoot<com.bluecirclesoft.open.jigen.integration.NestedOuter>(base);
        let imm = new com.bluecirclesoft.open.jigen.integration.NestedOuter$Imm(root);
        expect(imm.d).toEqual("qwerty");
        let b = imm.b;
        expect(b.b).toEqual("gh");
        let ver1 = root.getCurrent();
        b.b = "ij";
        expect(b.b).toEqual("ij");
        let ver2 = root.getCurrent();
        expect(root.getHistorySize()).toEqual(2);
        expect(ver1 === ver2).toBeFalsy("version not updated");
        expect(root.getHistory(0)).toEqual(ver1);
        expect(root.getHistory(1)).toEqual(ver2);

        let eAcc = b.e;
        expect(eAcc.get(1)).toEqual(5);
        eAcc.set(1, 23);
        expect(root.getHistorySize()).toEqual(3);
        expect(eAcc.get(1)).toEqual(23);

        imm.d = null;
        expect(root.getHistorySize()).toEqual(4);
        expect(imm.d).toEqual(null);


    });

});

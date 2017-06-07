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

(<any>window).$ = $;
(<any>window).jQuery = $;

// pull base URL from command line
jsonInterfaceGenerator.init(__karma__.config.baseUrl);

describe("test TestServicesString", () => {

    // Standard handler
    function simpleHandler<T>(handler: (x: T) => void): JsonOptions<T> {
        return {
            success: (s: T, textStatus: string, jqXHR: JQueryXHR) => {
                console.log("success: result ", s);
                handler(s);
            },
            error: (jqXHR: JQueryXHR, textStatus: string, errorThrown: string) => {
                console.log("error: jqXHR=", jqXHR, " textStatus=", textStatus, " errorThrown=", errorThrown);
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
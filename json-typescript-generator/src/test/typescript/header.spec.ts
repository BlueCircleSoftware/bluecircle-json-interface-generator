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
import {jsonInterfaceGenerator} from "../../main/resources/header";

interface A {
    s: string;
}

interface B {
    a: A;
}

interface C {
    cstr?: string;
}

interface D {
    dstr?: string;
}

interface E {
    estr?: string;
    d?: D;
}

interface F {
    c?: C;
    d?: D;
    e?: E;
}

describe("jsonInterfaceGenerator", () => {

    it("TODO move tests", () => {
        expect(1).toEqual(1);
    });

});

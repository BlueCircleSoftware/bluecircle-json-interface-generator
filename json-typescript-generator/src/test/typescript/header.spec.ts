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
import AccessorBuilder = jsonInterfaceGenerator.AccessorBuilder;

interface A {
    s: string;
}

interface B {
    a: A;
}

function A$s(): jsonInterfaceGenerator.MemberAccessorImpl<A,string> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<A,string>('s', () => '');
}

function B$a(): jsonInterfaceGenerator.MemberAccessorImpl<B,A | undefined> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<B,A | undefined>('a', () => {
        return {s: ''};
    });
}

interface C {
    cstr?: string;
}

function C$cstr(): jsonInterfaceGenerator.MemberAccessorImpl<C,string> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<C,string>('cstr', () => '');
}

interface D {
    dstr?: string;
}

function D$dstr(): jsonInterfaceGenerator.MemberAccessorImpl<D,string> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<D,string>('dstr', () => '');
}

interface E {
    estr?: string;
    d?: D;
}

function E$estr(): jsonInterfaceGenerator.MemberAccessorImpl<E,string> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<E,string>('estr', () => '');
}

function E$d(): jsonInterfaceGenerator.MemberAccessorImpl<E,D | undefined> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<E,D | undefined>('d', () => {
        return {};
    });
}

interface F {
    c?: C;
    d?: D;
    e?: E;
}

function F$c(): jsonInterfaceGenerator.MemberAccessorImpl<F,C | undefined> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<F,C | undefined>('c', () => {
        return {};
    });
}
function F$d(): jsonInterfaceGenerator.MemberAccessorImpl<F,D | undefined> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<F,D | undefined>('d', () => {
        return {};
    });
}
function F$e(): jsonInterfaceGenerator.MemberAccessorImpl<F,E | undefined> {
    return new jsonInterfaceGenerator.MemberAccessorImpl<F,E | undefined>('e', () => {
        return {};
    });
}


describe("jsonInterfaceGenerator", () => {
    it("can create AccessorBuilder", () => {
        let builder: AccessorBuilder<B> = AccessorBuilder.make(() => {
            return {a: {s: ""}};
        });
        expect(builder.get()).toBeUndefined();
    });

    it("can clone AccessorBuilder", () => {
        let builder: AccessorBuilder<B> = AccessorBuilder.make(() => {
            return {a: {s: ""}};
        });
        let builder2 = builder.add(B$a());
        expect(builder.list.length).toEqual(0);
    });

    it("can get/set 1", () => {
        let builder: AccessorBuilder<B> = AccessorBuilder.make(() => {
            return {a: {s: ""}};
        });
        let builder2 = builder.add(B$a());
        expect(builder.list.length).toEqual(0);
        builder2.set({s: 'quack'});
        expect(builder.list.length).toEqual(1);
        expect(builder.get()).toEqual({a: {s: 'quack'}});
        expect(builder2.get()).toEqual({s: 'quack'});
        builder2.set({s: 'woof'});
        expect(builder.list.length).toEqual(2);
        expect(builder.get()).toEqual({a: {s: 'woof'}});
        expect(builder2.get()).toEqual({s: 'woof'});
        expect(builder.list[0]).toEqual({a: {s: 'quack'}});
        expect(builder.list[1]).toEqual({a: {s: 'woof'}});

        let builder3 = builder2.add(A$s());
        builder3.set("meow");
        expect(builder3.get()).toEqual("meow");
        expect(builder2.get()).toEqual({s: "meow"});
        expect(builder.get()).toEqual({a: {s: "meow"}});
        expect(builder.list.length).toEqual(3);
        expect(builder.list[0]).toEqual({a: {s: 'quack'}});
        expect(builder.list[1]).toEqual({a: {s: 'woof'}});
        expect(builder.list[2]).toEqual({a: {s: 'meow'}});
    });

    it("can get/set 2", () => {
        let builder = AccessorBuilder.make(() => {
            return {};
        });
        let f_c = builder.add(F$c());
        let f_d = builder.add(F$d());
        let f_e = builder.add(F$e());
        let f_c_cstr = f_c.add(C$cstr());
        let f_d_dstr = f_d.add(D$dstr());
        let f_e_estr = f_e.add(E$estr());
        let f_e_d = f_e.add(E$d());
        let f_e_d_dstr = f_e_d.add(D$dstr());

        builder.reset();
        expect(builder.list.length).toEqual(0);
        f_e_d_dstr.set("abc");
        expect(builder.list.length).toEqual(1);
        expect(builder.list[0]).toEqual({e: {d: {dstr: 'abc'}}});
        f_e_d_dstr.set("def");
        expect(builder.list.length).toEqual(2);
        expect(builder.list[0]).toEqual({e: {d: {dstr: 'abc'}}});
        expect(builder.list[1]).toEqual({e: {d: {dstr: 'def'}}});
        f_c_cstr.set("ghi");
        expect(builder.list.length).toEqual(3);
        expect(builder.list[0]).toEqual({e: {d: {dstr: 'abc'}}});
        expect(builder.list[1]).toEqual({e: {d: {dstr: 'def'}}});
        expect(builder.list[2]).toEqual({c: {cstr: 'ghi'}, e: {d: {dstr: 'def'}}});
    });
});
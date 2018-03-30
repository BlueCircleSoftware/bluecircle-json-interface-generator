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

export namespace jsonInterfaceGenerator {

    /**
     * Generic options for an AJAX call.  I try to be ajax-lib-agnostic here, but my main dev library is jQuery.
     * TODO more testing to see if this makes sense for, e.g., Axios
     */
    export interface JsonOptions<R> {
        /**
         * Is this call async?
         */
        async?: boolean;

        /**
         * Completion callback
         * @param {boolean} success true if error() was not called
         */
        complete?(success: boolean): void;

        /**
         * Error callback
         * @param {string} errorThrown
         * @returns {any}
         */
        error?(errorThrown: string): any;

        /**
         * Success callback
         * @param {R} data
         * @returns {any}
         */
        success?(data: R): any;
    }

    export function joinPath(...parts: string[]): string {
        const goodParts: string[] = [];
        for (let i = 0; i < parts.length; i++) {
            const part = parts[i];
            if (part !== null && part !== undefined && part.length > 0) {
                goodParts.push(part);
            }
        }
        if (goodParts.length === 0) {
            return "";
        }
        let builtPath = goodParts[0];
        for (let i = 1; i < goodParts.length; i++) {
            const p2 = goodParts[i];
            const trail = builtPath.charAt(builtPath.length - 1) === "/";
            const lead = p2.charAt(0) === "/";
            if (trail && lead) {
                builtPath = builtPath + p2.substring(1);
            } else if (!trail && !lead) {
                builtPath = builtPath + "/" + p2;
            } else {
                builtPath = builtPath + p2;
            }
        }
        return builtPath;
    }

    /**
     * Prefix to be appended to all URLs
     * TODO is this necessary since the client now installs an AJAX caller?
     * @type {string} the prefix
     */
    let ajaxUrlPrefix: string | null = null;

    /**
     * Get the prefix to be appended to all URLs
     * @returns {string} the prefix
     */
    export function getPrefix(): string {
        if (!ajaxUrlPrefix) {
            throw new Error("The URL prefix has not been set, so no AJAX calls can be made. Set the URL prefix by calling" +
                " jsonInterfaceGenerator.init()");
        }
        return ajaxUrlPrefix;
    }

    /**
     * Get the prefix to be appended to all URLs
     * @returns {string} the prefix
     */
    export function applyPrefix(following: string): string {
        return joinPath(getPrefix(), following);
    }

    /**
     * A type for a function that will handle AJAX calls
     */
    type AjaxInvoker = (url: string, method: string, data: any, isBodyParam: boolean, options: JsonOptions<any>) => void;

    /**
     * The ajax caller used by generated code.
     */
    export let callAjax: AjaxInvoker;

    export let logDebug: boolean = false;

    function debug(...args: any[]) {
        if (logDebug) {
            console.log.apply(null, args);
        }
    }

    export function init(prefix: string) {
        ajaxUrlPrefix = prefix;
    }

    /**
     * Interface used during enum generation for enum reverse lookups
     */
    export interface EnumReverseLookup<EnumType> {
        [key: string]: EnumType;

        [index: number]: EnumType;
    }

    export enum WriteOption {
        WRITE,
        READ_FILL,
    }

    /**
     * Thing that collects changes - may be a ChangeRoot, which has a list of versions of the tree
     */
    export abstract class ChangeCollector<T> {

        private differentiator: T | undefined = undefined;

        public abstract get(key: string | number): any;

        public abstract set(key: string | number, val: any): void;
    }

    export abstract class DirectWrapper<T> extends ChangeCollector<T> {

        public abstract getObj(write: WriteOption): T;
    }

    /**
     * A "change root" - contains a list of changes to an object/array
     */
    export class ChangeRoot<T> extends DirectWrapper<T> {
        private l: T[] = [];
        private readonly factory: () => T;

        public constructor(initial: T | (() => T)) {
            super();
            if (typeof initial === "function") {
                this.factory = initial;
            } else {
                this.factory = () => initial;
            }
        }

        public getHistory(index: number): T {
            return this.l[index];
        }

        public getHistorySize(): number {
            return this.l.length;
        }

        public getCurrent(): T {
            return this.getObj(WriteOption.READ_FILL);
        }

        public getObj(write: WriteOption): T {
            let cur: T;
            switch (write) {
                case WriteOption.WRITE:
                    if (this.l.length === 0) {
                        cur = this.factory();
                    } else {
                        cur = this.l[this.l.length - 1];
                    }
                    let nxt: T;
                    if (typeof cur === "object") {
                        nxt = (Object as any).assign({}, cur);
                    } else {
                        nxt = (cur as any[]).slice() as any as T;
                    }
                    this.l.push(nxt);
                    return nxt;
                case WriteOption.READ_FILL:
                    if (this.l.length === 0) {
                        cur = this.factory();
                        this.l.push(cur);
                    } else {
                        cur = this.l[this.l.length - 1];
                    }
                    return cur;
                default:
                    throw new Error("Unhandled " + write);
            }
        }

        public get<K extends keyof T>(key: K): T[K] {
            return this.getObj(WriteOption.READ_FILL)[key];
        }

        public set<K extends keyof T>(key: K, val: T[K]): void {
            this.getObj(WriteOption.WRITE)[key] = val;
        }
    }

    // wrapper to build descendants
    export class ObjectWrapper<T> extends DirectWrapper<T> {
        private parent: DirectWrapper<any>;
        private readonly myIndex: string | number;
        private readonly factory: () => T;

        public constructor(parent: DirectWrapper<any>, myIndex: string | number, factory: () => T) {
            super();
            this.parent = parent;
            this.myIndex = myIndex;
            this.factory = factory;
        }

        public getObj(write: WriteOption): T {
            const parentObj = this.parent.getObj(write);
            let myObj = parentObj[this.myIndex];

            switch (write) {
                case WriteOption.WRITE:
                case WriteOption.READ_FILL:
                    if (myObj === undefined) {
                        myObj = this.factory();
                        parentObj[this.myIndex] = myObj;
                    }
                    return myObj;
                default:
                    throw new Error("Unhandled " + write);
            }
        }

        public get<K extends keyof T>(key: K): T[K] {
            return this.getObj(WriteOption.READ_FILL)[key];
        }

        public set<K extends keyof T>(key: K, val: T[K]): void {
            this.getObj(WriteOption.WRITE)[key] = val;
        }
    }

    export class OLeafAcc<DataType, WrapperType> extends ChangeCollector<DataType> {
        private parent: ObjectWrapper<DataType>;
        private myIndex: keyof DataType;

        public constructor(parent: ObjectWrapper<DataType>, myIndex: keyof DataType) {
            super();
            this.parent = parent;
            this.myIndex = myIndex;
        }

        public get<K extends keyof DataType>(key: K): DataType[K] {
            return this.parent.getObj(WriteOption.READ_FILL)[key];
        }

        public set<K extends keyof DataType>(key: K, val: DataType[K]): void {
            this.parent.getObj(WriteOption.WRITE)[key] = val;
        }
    }

    export class ArrayWrapper<T> extends DirectWrapper<T> {
        private parent: DirectWrapper<any>;
        private readonly myIndex: string | number;
        private readonly factory: () => T[];

        public constructor(parent: DirectWrapper<any>, myIndex: string | number, factory: () => T[]) {
            super();
            this.parent = parent;
            this.myIndex = myIndex;
            this.factory = factory;
        }

        public getObj(write: WriteOption): T {
            const parentObj = this.parent.getObj(write);
            let myObj = parentObj[this.myIndex];

            switch (write) {
                case WriteOption.WRITE:
                case WriteOption.READ_FILL:
                    if (myObj === undefined) {
                        myObj = this.factory();
                        parentObj[this.myIndex] = myObj;
                    }
                    return myObj;
                default:
                    throw new Error("Unhandled " + write);
            }
        }

        public get(key: number): T {
            return (this.getObj(WriteOption.READ_FILL) as any as T[])[key];
        }

        public set(key: number, val: T): void {
            (this.getObj(WriteOption.WRITE) as any as T[])[key] = val;
        }

        public toArray(): T[] {
            return this.getObj(WriteOption.READ_FILL) as any as T[];
        }

        public fromArray(newArray: T[]): void {
            const arr = this.getObj(WriteOption.WRITE) as any as T[];
            arr.length = 0;
            arr.push(...newArray);
        }
    }

    export class WrappedElementArrayWrapper<DataType, WrapperType> {
        private readonly parent: ArrayWrapper<DataType>;
        private readonly leafWrapper: (parent: ArrayWrapper<DataType>, index: number) => WrapperType;

        public constructor(parent: ArrayWrapper<DataType>, leafWrapper: (parent: ArrayWrapper<DataType>, index: number) => WrapperType) {
            this.parent = parent;
            this.leafWrapper = leafWrapper;
        }

        public get(key: number): WrapperType {
            return this.leafWrapper(this.parent, key);
        }
    }

}

/*
 * Copyright 2019 Blue Circle Software, LLC
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
 *
 */

export type UnknownType = unknown;

/**
 * Generic options for an AJAX call.  I try to be ajax-lib-agnostic here, but my main dev library is jQuery.
 * TODO more testing to see if this makes sense for, e.g., Axios
 */
export interface JsonOptions<R> {
    /**
     * Completion callback
     * @param {boolean} success true if error() was not called
     */
    complete?(success: boolean): void;

    /**
     * Error callback
     * @param {unknown} errorThrown
     * @returns {any}
     */
    error?(errorThrown: unknown): void;

    /**
     * Success callback
     * @returns {any}
     */
    success?(data: R): void;
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

export type BodyType = "json" | "form" | "none";

/**
 * A type for a function that will handle AJAX calls
 * @param url the URL to send the request to
 * @param method the HTTP method
 * @param data the data to send to the server (will be an empty object for bodyless requests e.g. GET or DELETE)
 * @param bodyType "json" for JSON, "form" for url-encoded form data, "none" for bodyless requests
 * @param consumes the endpoint's listed "consumes" MIME type
 */
type AjaxInvoker<T> = (url: string, method: string, data: UnknownType, bodyType: BodyType, consumes: string | null) => Promise<T>;

/**
 * The ajax caller used by generated code.
 */
let callAjaxFn: AjaxInvoker<UnknownType>;

export function setCallAjax(newCallAjax: AjaxInvoker<UnknownType>): void {
    callAjaxFn = newCallAjax;
}

export async function callAjax<S, R>(url: string,
                                     method: string,
                                     data: S,
                                     bodyType: BodyType,
                                     consumes: string | null,
                                     options?: JsonOptions<R>): Promise<R> {
    let success = false;
    try {
        const result = await (callAjaxFn as AjaxInvoker<R>)(url, method, data, bodyType, consumes);
        success = true;
        if (options?.success) {
            options.success(result);
        }
        return result;
    } catch (errorThrown) {
        if (options?.error) {
            options.error(errorThrown);
        }
        throw errorThrown;
    } finally {
        if (options?.complete) {
            options.complete(success);
        }
    }
}

export type DebugLoggerType = (args: any[]) => void;
let debugLogger: DebugLoggerType;

export function setDebugLogger(newDebugLogger: DebugLoggerType) {
    debugLogger = newDebugLogger;
}

export function logDebug(...args: any[]): void {
    debugLogger(args);
}

// set default debug logger to none
debugLogger = () => {
};

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

export type Selector = string | number;

export type SelectorList = Selector[];

export function join(selector: SelectorList, next: Selector) {
    const result = selector.slice();
    result.push(next);
    return result;
}

export type ChangeWatcher<T> = (newVal: Readonly<T>, oldVal: Readonly<T>, path: Readonly<SelectorList>) => void;

interface Mapish<V> {
    [key: string]: V;

    [index: number]: V;
}

interface WatcherTree {
    watchers: ChangeWatcher<UnknownType>[];
    children: Mapish<WatcherTree>;
}

export class ChangeRoot<T> {
    private _watchers: WatcherTree = {
        watchers: [],
        children: {},
    };

    public constructor(start: T) {
        this._history = [start];
    }

    private _history: T[];

    public get history(): Readonly<T[]> {
        return this._history;
    }

    public get current(): Readonly<T> {
        return this._history[0];
    }

    private static clone<U>(val: U): U {
        if (typeof val === "object") {
            if (Array.isArray(val)) {
                return (val as any).slice();
            } else {
                return {...(val as any)};
            }
        } else {
            return val;
        }
    }

    public watch(selector: SelectorList, watcher: ChangeWatcher<any>) {
        let lastChild = this._watchers;
        for (const elem of selector) {
            let child = lastChild.children[elem];
            if (!child) {
                child = {watchers: [], children: {}};
                lastChild.children[elem] = child;
            }
            lastChild = child;
        }
        const lastWatchers = lastChild.watchers;
        if (lastWatchers.indexOf(watcher) < 0) {
            lastWatchers.push(watcher);
        }
    }

    public unwatch(selector: SelectorList, watcher: ChangeWatcher<any>) {
        let lastChild = this._watchers;
        for (const elem of selector) {
            let child = lastChild.children[elem];
            if (!child) {
                child = {watchers: [], children: {}};
                lastChild.children[elem] = child;
            }
            lastChild = child;
        }
        lastChild.watchers = lastChild.watchers.filter(elem => elem !== watcher);
    }

    public getVal(selector: SelectorList, start?: Readonly<T>, undef?: boolean): UnknownType {
        let current = start ? start : this.current;
        for (const elem of selector) {
            if (current === undefined || current === null) {
                if (undef) {
                    return undefined;
                } else {
                    throw new Error(current + " encountered at " + elem + " in path " + selector);
                }
            }
            current = (current as any)[elem];
        }
        return current;
    }

    public setVal(selector: SelectorList, newVal: UnknownType): void {
        // part 1: clone tree up until change point
        let lastObj: UnknownType;

        lastObj = ChangeRoot.clone(this.current);
        this._history.unshift(lastObj as T);

        for (let i = 0; i < selector.length - 1; i++) {
            const elem = selector[i];
            const cl = ChangeRoot.clone((lastObj as any)[elem]);
            (lastObj as any)[elem] = cl;
            lastObj = cl;
        }

        if (selector.length > 0) {
            (lastObj as any)[selector[selector.length - 1]] = newVal;
        }

        // part 2: notify watchers
        let lastWatchers = this._watchers;
        const changePath: SelectorList = [];
        for (const elem of selector) {
            if (!lastWatchers) {
                return;
            }
            for (const watcher of lastWatchers.watchers) {
                watcher(this.getVal(changePath, this._history[0], true) as Readonly<UnknownType>,
                    this.getVal(changePath, this._history[1], true) as Readonly<UnknownType>,
                    changePath);
            }
            lastWatchers = lastWatchers.children[elem];
        }
        this.notifyRestOfWatchers(changePath, lastWatchers);
    }

    private notifyRestOfWatchers(startPath: SelectorList, watchers: WatcherTree): void {
        if (watchers) {
            for (const watcher of watchers.watchers) {
                watcher(this.getVal(startPath, this._history[0], true) as Readonly<UnknownType>,
                    this.getVal(startPath, this._history[1], true) as Readonly<UnknownType>,
                    startPath);
            }
            for (const child of Object.getOwnPropertyNames(watchers.children)) {
                const nextPath = startPath.slice();
                nextPath.push(child);
                this.notifyRestOfWatchers(nextPath, watchers.children[child]);
            }
        }
    }
}

export class ChangeWrapper<T> {
    private readonly _selector: SelectorList;
    private _extensionCache: { [key: string]: SelectorList } = {};

    public constructor(root: ChangeRoot<UnknownType>, selector?: SelectorList) {
        this._root = root;
        this._selector = selector ? selector : [];
    }

    private readonly _root: ChangeRoot<UnknownType>;

    public get root(): ChangeRoot<UnknownType> {
        return this._root;
    }

    public makeChild<U>(next: Selector): ChangeWrapper<U> {
        return new ChangeWrapper<U>(this._root, this.extend(next));
    }

    public extend(next: Selector): SelectorList {
        if (this._extensionCache.hasOwnProperty(next)) {
            return this._extensionCache[next];
        } else {
            const nextPath = this._selector.slice();
            nextPath.push(next);
            this._extensionCache[next] = nextPath;
            return nextPath;
        }
    }

    public get(): Readonly<T> {
        return this._root.getVal(this._selector) as T;
    }

    public set(newVal: T): void {
        this._root.setVal(this._selector, newVal);
    }

    public getSub<U>(next: Selector): Readonly<U> {
        return this._root.getVal(this.extend(next)) as U;
    }

    public setSub(next: Selector, newVal: UnknownType): void {
        this._root.setVal(this.extend(next), newVal);
    }

    public watch(watcher: ChangeWatcher<T>): void {
        this._root.watch(this._selector, watcher as ChangeWatcher<UnknownType>);
    }

    public unwatch(watcher: ChangeWatcher<T>): void {
        this._root.unwatch(this._selector, watcher as ChangeWatcher<UnknownType>);
    }

    public watchSub<U>(next: Selector, watcher: ChangeWatcher<U>): void {
        this._root.watch(this.extend(next), watcher as ChangeWatcher<UnknownType>);
    }

    public unwatchSub<U>(next: Selector, watcher: ChangeWatcher<U>): void {
        this._root.unwatch(this.extend(next), watcher as ChangeWatcher<UnknownType>);
    }
}

export class PrimitiveArrayWrapper<T> {
    private _delegate: ChangeWrapper<T[]>;

    public constructor(base: ChangeRoot<UnknownType>, path: SelectorList) {
        this._delegate = new ChangeWrapper<T[]>(base, path);
    }

    public get(index: number): Readonly<T> {
        return this._delegate.getSub(index);
    }

    public set(index: number, newVal: T): void {
        return this._delegate.setSub(index, newVal);
    }
}

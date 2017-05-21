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
    export interface JsonOptions<R> {
        async?: boolean;

        complete? (jqXHR: JQueryXHR, textStatus: string): any;

        error? (jqXHR: JQueryXHR, textStatus: string, errorThrown: string): any;

        success? (data: R, textStatus: string, jqXHR: JQueryXHR): any;
    }

    let ajaxUrlPrefix: string | null = null;

    export function getPrefix(): string {
        if (!ajaxUrlPrefix) {
            throw "The URL prefix has not been set, so no AJAX calls can be made. Set the URL prefix by calling" +
            " jsonInterfaceGenerator.init()";
        }
        return ajaxUrlPrefix;
    }

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
     * Defines something that is indexable by string.  This is used below for getters and setters to ensure that the object can be
     * addressed by the index operator (i.e., [])
     */
    interface StringIndexable<ValType> {
        [name: string]: ValType;
    }

    /**
     * Defines an accessor of a given type (getter/setter).  Meant to work in a non-static way (that is, the accessor has a reference to
     * whatever object it's reading from).
     */
    export abstract class Accessor<ValType> {
        // this is not used - it's only here to force TypeScript to treat different type instances as
        // not-convertible.
        private unusedVal: ValType;

        /**
         * Get the current value
         */
        abstract get(): ValType;

        /**
         * Set the value to the new value
         * @param val the new value
         */
        abstract set(val: ValType): void;
    }

    /**
     * Defines an accessor of a field on a given type (getter/setter) plus a child object creator.  Meant to work in a static way (that
     * is, the object to operate upon is passed in as a parameter).
     */
    export abstract class MemberAccessor<OwnerType, ValType> {
        // this is not used - it's only here to force TypeScript to treat different type instances as
        // not-convertible.
        private unusedVal: OwnerType;

        /**
         * Get the current value of a member on the supplied object
         * @param obj the object to operate upon.
         */
        abstract get(obj: OwnerType & StringIndexable<ValType | ValType[]>): ValType;

        /**
         * Set the value of a member on the supplied object
         * @param obj the object to operate upon
         * @param val the new value
         */
        abstract set(obj: OwnerType & StringIndexable<ValType | ValType[]>, val: ValType): void;

        /**
         * Create a child object to be assigned to the member.
         */
        abstract createChild(): ValType;
    }

    /**
     * Default implementation of MemberAccessor
     */
    export class MemberAccessorImpl<OwnerType, ValType> extends MemberAccessor<OwnerType, ValType> {
        // this is not used - it's only here to force TypeScript to treat different type instances as
        // not-convertible.
        private unusedVa2: OwnerType;

        fieldName: string;
        childCtor: () => ValType;

        /**
         * Create a member accessor
         * @param fieldName name of the field to access
         * @param childCtor constructor function to make a new child object.
         */
        constructor(fieldName: string, childCtor: () => ValType) {
            super();
            this.fieldName = fieldName;
            this.childCtor = childCtor;
        }

        /**
         * Get the current value of a member on the supplied object
         * @param obj the object to operate upon.
         */
        get(obj: OwnerType & StringIndexable<ValType>): ValType {
            return obj[this.fieldName];
        }

        /**
         * Set the value of a member on the supplied object
         * @param obj the object to operate upon
         * @param val the new value
         */
        set(obj: OwnerType & StringIndexable<ValType>, val: ValType): void {
            obj[this.fieldName] = val;
        }

        /**
         * Create a child object to be assigned to the member.
         */
        createChild(): ValType {
            return this.childCtor();
        }
    }


    /**
     * Accessor array members, which includes an index number
     */
    export class SubscriptedMemberAccessorImpl<OwnerType, ValType> extends MemberAccessor<OwnerType, ValType> {
        // this is not used - it's only here to force TypeScript to treat different type instances as
        // not-convertible.
        private unusedVa2: OwnerType;
        fieldName: string;
        index: number;
        childCtor: () => ValType;

        /**
         * Create an accessor for the specified element number of an array
         * @param fieldName the name of the field to access
         * @param index the array element number to access
         * @param childCtor constructor function to make a new child object.
         */
        constructor(fieldName: string, index: number, childCtor: () => ValType) {
            super();
            this.fieldName = fieldName;
            this.childCtor = childCtor;
            this.index = index;
        }

        /**
         * Get the current value of a member on the supplied object
         * @param obj the object to operate upon.
         */
        get(obj: OwnerType & StringIndexable<ValType[]>): ValType {
            let arr = obj[this.fieldName];
            if (!arr) {
                arr = [];
                obj[this.fieldName] = arr;
            }
            return arr[this.index];
        }

        /**
         * Set the value of a member on the supplied object
         * @param obj the object to operate upon
         * @param val the new value
         */
        set(obj: OwnerType & StringIndexable<ValType[]>, val: ValType): void {
            let arr = obj[this.fieldName];
            if (!arr) {
                arr = [];
            } else {
                arr = arr.slice();
            }
            obj[this.fieldName] = arr;
            arr[this.index] = val;
        }

        /**
         * Create a child object to be assigned to the member.
         */
        createChild(): ValType {
            return this.childCtor();
        }
    }

    export class AccessorBuilder<T> extends Accessor<T> {
        // this is not used - it's only here to force TypeScript to treat different type instances as
        // not-convertible.
        private unusedVa2: T;
        list: any[];
        accessors: MemberAccessor<any, any>[] = [];
        private toplevelCreator: () => any;

        private constructor(l: any[], creator: () => T) {
            super();
            this.list = l;
            this.toplevelCreator = creator;
        }

        public static make<T>(creator: () => T, initial?: T | undefined): AccessorBuilder<T> {
            let initArr = (initial === undefined ? [] : [initial]);
            return new AccessorBuilder<T>(initArr, creator);
        }

        // Here's what I'd like to do: add a ValType type argument to AccessorBuilder, and use keyof ValType in the add method to return an
        // AccessorBuilder<ValType[K]>.  This works fine for required props, but falls down currently for oprional props, which have a type
        // of ValType[K]|undefined.  Then the next add() can't accept any properties, because there are no properties in common between the
        // two. https://github.com/Microsoft/TypeScript/issues/12215 and others have been proposed apparently which might allow me to
        // subtract undefined from the returned AccessorBuilder's type.
        public add<S>(acc: MemberAccessor<T, S>): AccessorBuilder<S> {
            let newBuilder = new AccessorBuilder<S>(this.list, this.toplevelCreator);
            newBuilder.accessors = this.accessors.slice();
            newBuilder.accessors.push(acc);
            return newBuilder;
        }

        public reset() {
            this.list.length = 0;
        }

        get(): T {
            let obj: any = this.list[this.list.length - 1];
            let index = 0;
            while (obj && index < this.accessors.length) {
                let acc = this.accessors[index];
                obj = acc.get(obj);
                index++;
            }
            return obj;
        }

        set(val: T) {
            debug("+++ set called with ", val);
            // basic algorithm here is to copy each parent object down to the point where we're actually making the mutation.
            let obj;
            if (this.list.length === 0) {
                obj = this.toplevelCreator();
            } else {
                let lastElem = this.list[this.list.length - 1];
                obj = {...lastElem};
            }
            this.list.push(obj);

            let index = 0;
            let accessorLength = this.accessors.length;
            while (index < accessorLength - 1) {
                debug("index now ", index, " accessorLength is ", accessorLength);
                let acc = this.accessors[index];
                debug("accessor is ", acc);
                const parent = obj;
                debug("parent is ", parent);
                let child = acc.get(parent);
                debug("child is ", child);
                let newChild;
                if (child === null || child === undefined) {
                    newChild = acc.createChild();
                } else {
                    newChild = {...child};
                }
                acc.set(parent, newChild);
                debug("after set - parent is now ", parent);
                child = newChild;
                obj = child;
                index++;
            }
            debug("index now ", index, " accessorLength is ", accessorLength);
            this.accessors[index].set(obj, val);
            debug("--- all done; this.list now ", this.list);
            return obj;
        }
    }
}
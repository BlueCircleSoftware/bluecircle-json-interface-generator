/*
 * Copyright 2016 Blue Circle Software, LLC
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

namespace jsonInterfaceGenerator {
    export interface JsonOptions<R> {
        async?: boolean;
        complete? (jqXHR: JQueryXHR, textStatus: string): any;
        error? (jqXHR: JQueryXHR, textStatus: string, errorThrown: string): any;
        success? (data: R, textStatus: string, jqXHR: JQueryXHR): any;
    }
    export let ajaxUrlPrefix: string|null = null;

    export function init(prefix: string) {
        ajaxUrlPrefix = prefix;
    }

    /**
     * Defines an accessor of a given type (getter/setter).  Meant to work in a non-static way (that is, the accessor has a reference to
     * whatever object it's reading from).
     */
    export abstract class Accessor<ValType> {
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
    export abstract class MemberAccessor<OwnerType,ValType> {
        /**
         * Get the current value of a member on the supplied object
         * @param obj the object to operate upon.
         */
        abstract get(obj: OwnerType): ValType;

        /**
         * Set the value of a member on the supplied object
         * @param obj the object to operate upon
         * @param val the new value
         */
        abstract set(obj: OwnerType, val: ValType): void;

        /**
         * Create a child object to be assigned to the member.
         */
        abstract createChild(): ValType;
    }

    /**
     * Default implementation of MemberAccessor
     */
    export class MemberAccessorImpl<OwnerType,ValType> extends MemberAccessor<OwnerType,ValType> {
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
        get(obj: OwnerType): ValType {
            return obj[this.fieldName];
        }

        /**
         * Set the value of a member on the supplied object
         * @param obj the object to operate upon
         * @param val the new value
         */
        set(obj: OwnerType, val: ValType): void {
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
    export class SubscriptedMemberAccessorImpl<OwnerType,ValType> extends MemberAccessor<OwnerType,ValType> {
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
        get(obj: OwnerType): ValType {
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
        set(obj: OwnerType, val: ValType): void {
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

    export class AccessorBuilder<RootType,ValType> extends Accessor<ValType> {
        list: RootType[];
        accessors: MemberAccessor<any,any>[] = [];

        constructor(l: RootType[]) {
            super();
            this.list = l;
        }

        public add<SubValType>(acc: MemberAccessor<ValType,SubValType>): AccessorBuilder<RootType,SubValType> {
            let newBuilder = new AccessorBuilder<RootType,SubValType>(this.list);
            newBuilder.accessors = this.accessors.slice();
            newBuilder.accessors.push(acc);
            return newBuilder;
        }

        get(): ValType {
            let obj: any = this.list[this.list.length - 1];
            let index = 0;
            while (obj && index < this.accessors.length) {
                let acc = this.accessors[index];
                obj = acc.get(obj);
                index++;
            }
            return obj;
        }

        set(val: ValType) {
            // basic algorithm here is to copy each parent object down to the point where we're actually making the mutation.
            let lastElem = this.list[this.list.length - 1];
            let obj: any = Object.create(lastElem);
            this.list.push(obj);
            let index = 0;
            let accessorLength = this.accessors.length;
            let acc: MemberAccessor<any,any>|undefined = undefined;
            while (index < accessorLength - 1) {
                acc = this.accessors[index];
                const parent = obj;
                let child = acc.get(parent);
                const newChild = acc.createChild();
                acc.set(parent, newChild);
                child = newChild;
                obj = child;
                index++;
            }
            if (acc) {
                this.accessors[accessorLength - 1].set(obj, val);
            } else {
                console.log("No accessor - too short?");
            }
            return obj;
        }
    }
}
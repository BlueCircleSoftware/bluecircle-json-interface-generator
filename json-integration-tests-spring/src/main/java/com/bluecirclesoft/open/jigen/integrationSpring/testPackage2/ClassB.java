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

package com.bluecirclesoft.open.jigen.integrationSpring.testPackage2;

/**
 * TODO document me
 */
public class ClassB {

	private ClassA one;

	private ClassA two;

	private ClassC<Integer, String> three;

	private ClassC<Integer, Integer> four;

	private ClassA[] five;

	public ClassA getOne() {
		return one;
	}

	public void setOne(ClassA one) {
		this.one = one;
	}

	public ClassA getTwo() {
		return two;
	}

	public void setTwo(ClassA two) {
		this.two = two;
	}

	public ClassC<Integer, String> getThree() {
		return three;
	}

	public void setThree(ClassC<Integer, String> three) {
		this.three = three;
	}

	public ClassC<Integer, Integer> getFour() {
		return four;
	}

	public void setFour(ClassC<Integer, Integer> four) {
		this.four = four;
	}

	public ClassA[] getFive() {
		return five;
	}

	public void setFive(ClassA[] five) {
		this.five = five;
	}
}

package com.bluecirclesoft.open.jigen.inputJackson;

/**
 * TODO document me
 */
public class ClassB {

	private ClassA one;

	private ClassA two;

	private ClassC<Integer, String> three;

	private ClassC<Integer, Integer> four;

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
}

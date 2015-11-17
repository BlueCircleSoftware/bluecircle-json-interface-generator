package com.bluecirclesoft.open.jigen.inputJackson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO document me
 */
public class ClassA {

	private int prop1;

	private String prop2;

	private List<String> prop3 = new ArrayList<>();

	private EnumA prop4;

	private List<EnumA> prop5 = new ArrayList<>();

	private Set<Integer> prop6 = new HashSet<>();

	private Map<String, String> prop7 = new HashMap<>();

	private Map<Integer, String> prop8 = new HashMap<>();

	private Map<Map<String, String>, Map<Integer, Integer>> prop9 = new HashMap<>();

	public int getProp1() {
		return prop1;
	}

	public void setProp1(int prop1) {
		this.prop1 = prop1;
	}

	public String getProp2() {
		return prop2;
	}

	public void setProp2(String prop2) {
		this.prop2 = prop2;
	}

	public List<String> getProp3() {
		return prop3;
	}

	public void setProp3(List<String> prop3) {
		this.prop3 = prop3;
	}

	public EnumA getProp4() {
		return prop4;
	}

	public void setProp4(EnumA prop4) {
		this.prop4 = prop4;
	}

	public List<EnumA> getProp5() {
		return prop5;
	}

	public void setProp5(List<EnumA> prop5) {
		this.prop5 = prop5;
	}

	public Set<Integer> getProp6() {
		return prop6;
	}

	public void setProp6(Set<Integer> prop6) {
		this.prop6 = prop6;
	}

	public Map<String, String> getProp7() {
		return prop7;
	}

	public void setProp7(Map<String, String> prop7) {
		this.prop7 = prop7;
	}

	public Map<Integer, String> getProp8() {
		return prop8;
	}

	public void setProp8(Map<Integer, String> prop8) {
		this.prop8 = prop8;
	}

	public Map<Map<String, String>, Map<Integer, Integer>> getProp9() {
		return prop9;
	}

	public void setProp9(Map<Map<String, String>, Map<Integer, Integer>> prop9) {
		this.prop9 = prop9;
	}
}

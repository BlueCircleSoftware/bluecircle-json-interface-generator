package com.bluecirclesoft.open.jigen.model;

import java.lang.reflect.Type;

@FunctionalInterface
public interface PropertyEnumerator {

	void enumerateProperties(Model model, Type... types);
}

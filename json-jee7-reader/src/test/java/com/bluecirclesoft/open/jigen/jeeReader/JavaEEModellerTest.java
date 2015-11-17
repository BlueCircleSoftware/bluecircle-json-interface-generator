package com.bluecirclesoft.open.jigen.jeeReader;

import org.junit.Test;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public class JavaEEModellerTest {

	@Test
	public void testModeller() {
		JavaEEModeller modeller = new JavaEEModeller();

		Model model = modeller.createModel("/", "com.bluecirclesoft");

		System.out.println(model);
	}

}

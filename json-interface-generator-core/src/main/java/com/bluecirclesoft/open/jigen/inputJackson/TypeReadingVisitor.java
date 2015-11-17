package com.bluecirclesoft.open.jigen.inputJackson;

import com.bluecirclesoft.open.jigen.model.JType;

/**
 * TODO document me
 */
interface TypeReadingVisitor<Type extends JType> {

	Type getResult();
}

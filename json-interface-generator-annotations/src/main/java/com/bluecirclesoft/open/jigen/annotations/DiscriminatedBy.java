package com.bluecirclesoft.open.jigen.annotations;

/**
 * Tell JIG how the actual types should be discriminated on the client side.
 */
public enum DiscriminatedBy {
	/**
	 * The method has a runtime-defined value (JIG must be able to create an instance of the object to get this value)
	 */
	RETURN_VALUE,
	/**
	 * The method will be returning the class name (JIG can use this without instantiating the class)
	 */
	CLASS_NAME,
}

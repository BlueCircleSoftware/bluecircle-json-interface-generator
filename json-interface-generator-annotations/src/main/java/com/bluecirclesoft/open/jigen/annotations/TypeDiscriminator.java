package com.bluecirclesoft.open.jigen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this will be used to create is...() methods in the client, to allow for "casting" on the client side.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeDiscriminator {

	/**
	 * Determine how JIG should discriminate between different types
	 *
	 * @return the discrimination algorithm
	 */
	DiscriminatedBy discriminatedBy();
}

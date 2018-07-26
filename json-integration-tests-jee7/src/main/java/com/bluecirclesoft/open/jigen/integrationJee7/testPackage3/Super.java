package com.bluecirclesoft.open.jigen.integrationJee7.testPackage3;

import com.bluecirclesoft.open.jigen.annotations.DiscriminatedBy;
import com.bluecirclesoft.open.jigen.annotations.TypeDiscriminator;

/**
 * TODO document me
 */
abstract public class Super {

	@TypeDiscriminator(discriminatedBy = DiscriminatedBy.CLASS_NAME)
	public String getType() {
		return this.getClass().getName();
	}

}

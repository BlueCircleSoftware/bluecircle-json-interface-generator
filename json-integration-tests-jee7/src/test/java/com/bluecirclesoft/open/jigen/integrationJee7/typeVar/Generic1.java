package com.bluecirclesoft.open.jigen.integrationJee7.typeVar;

/**
 * TODO document me
 */
public class Generic1<Ty extends ABase> {
	private Ty val;

	public Ty getVal() {
		return val;
	}

	public void setVal(Ty val) {
		this.val = val;
	}
}

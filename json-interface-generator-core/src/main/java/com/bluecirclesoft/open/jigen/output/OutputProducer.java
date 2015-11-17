package com.bluecirclesoft.open.jigen.output;

import java.io.IOException;

import com.bluecirclesoft.open.jigen.model.Model;

/**
 * TODO document me
 */
public interface OutputProducer {

	void output(Model model) throws IOException;

}

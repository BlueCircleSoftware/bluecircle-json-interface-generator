package com.bluecirclesoft.open.jigen;

import java.util.List;

/**
 * TODO document me
 */
public interface ConfigurableProcessor<T> {

	/**
	 * Get the class which contains the options. Will be passed to Jackson.
	 *
	 * @return the class
	 */
	Class<T> getOptionsClass();

	/**
	 * Validate whatever values were obtained from the config file.
	 *
	 * @param options the options object
	 * @param errors  a list of errors to add this object's specific errors to.
	 */
	void acceptOptions(T options, List<String> errors);
}

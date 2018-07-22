package com.bluecirclesoft.open.jigen;

import java.util.List;

/**
 * TODO document me
 */
public class Jee7Processor implements ConfigurableProcessor<Jee7Options> {

	Jee7Options options;

	@Override
	public Class<Jee7Options> getOptionsClass() {
		return Jee7Options.class;
	}

	@Override
	public void acceptOptions(Jee7Options options, List<String> errors) {
		this.options = options;
	}

	public Jee7Options getOptions() {
		return options;
	}
}

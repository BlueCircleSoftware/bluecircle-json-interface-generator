package com.bluecirclesoft.open.jigen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * TODO document me
 */
class ConfigurationReader {

	private Map<String, Object> config;

	void read(File file) throws IOException {
		read(file == null ? null : new FileInputStream(file));
	}

	void read(InputStream configFile) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		Map<String, Object> config = null;
		if (configFile != null) {
			config = mapper.readValue(configFile, Map.class);
		}

		this.config = config;

	}

	public void configureOneProcessor(List<String> errors, ConfigurableProcessor processor, String readerWriter, String configLabel) {
		ObjectMapper mapper = new ObjectMapper();

		Class<?> optionsClass = processor.getOptionsClass();
		Object configMap = null;
		if (config != null) {
			configMap = ((Map<String, Object>) config.get(readerWriter)).get(configLabel);
		}
		if (configMap == null) {
			configMap = new HashMap<String, Object>();
		}
		Object configObj = mapper.convertValue(configMap, optionsClass);

		processor.acceptOptions(configObj, errors);
	}

	public Map<String, Object> getReaderEntries() {
		Map<String, Object> readers = null;
		if (config != null) {
			readers = (Map<String, Object>) config.get("readers");
		}
		if (readers == null) {
			readers = new HashMap<>();
		}
		return readers;
	}

	public Map<String, Object> getWriterEntries() {
		Map<String, Object> writers = null;
		if (config != null) {
			writers = (Map<String, Object>) config.get("writers");
		}
		if (writers == null) {
			writers = new HashMap<>();
		}
		return writers;
	}

	// test only
	public Map<String, Object> getMap() {
		return config;
	}
}

package com.bluecirclesoft.open.jigen.jeeReader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.bluecirclesoft.open.getopt.GetOpt;
import com.bluecirclesoft.open.jigen.model.Model;
import com.bluecirclesoft.open.jigen.output.typeScript.TypeScriptProducer;

/**
 * TODO document me
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		GetOpt getOpt = GetOpt.createFromReceptacle(options, Main.class);
		getOpt.processParams(args);

		StringBuilder errors = new StringBuilder();
		if (StringUtils.isBlank(options.getPackageName())) {
			errors.append("Package name to process is required.\n");
		}
		if (StringUtils.isBlank(options.getUrlPrefix())) {
			errors.append("URL prefix is required.\n");
		}
		if (StringUtils.isBlank(options.getOutputFile())) {
			errors.append("Output file is required.\n");
		}
		if (errors.length() > 0) {
			getOpt.usage(errors);
			System.err.println(errors.toString());
			System.exit(1);
		}

		JavaEEModeller modeller = new JavaEEModeller();
		Model model = modeller.createModel(options.getUrlPrefix(), options.getPackageName());


		TypeScriptProducer outputTypeScript = new TypeScriptProducer(new File(options.getOutputFile()));
		outputTypeScript.output(model);

	}

}

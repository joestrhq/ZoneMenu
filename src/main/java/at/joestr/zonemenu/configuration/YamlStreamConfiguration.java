// 
// Private License
// 
// Copyright (c) 2019-2020 Joel Strasser <strasser999@gmail.com>
// 
// Only the copyright holder is allowed to use this software.
// 
package at.joestr.zonemenu.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Joel
 */
public class YamlStreamConfiguration extends YamlConfiguration {

	private InputStream configInputStream;

	public YamlStreamConfiguration(InputStream configInputStream) throws FileNotFoundException {
		super(new Yaml().load(configInputStream));
		this.configInputStream = configInputStream;
	}

	public InputStream getConfigInputStream() {
		return configInputStream;
	}

	public void setConfigInputStream(InputStream configInputStream) {
		this.configInputStream = configInputStream;
	}

	public void saveConfigAsFile(File configFile) throws IOException {
		FileOutputStream fOS = new FileOutputStream(configFile);
		fOS.write(
			new Yaml().dumpAsMap(this.config).getBytes(StandardCharsets.UTF_8)
		);
	}
}

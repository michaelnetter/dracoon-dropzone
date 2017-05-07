package org.mn.dropzone.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.mn.dropzone.model.ScreenPosition.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class ConfigIO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigIO.class);
	private static final String DEFAULT_FILE_NAME = "dropzone.config";
	private static final String APP_NAME = "Dropzone";

	private static final String USER_NAME = "dropzone.username";
	private static final String SERVER_URL = "dropzone.serverurl";
	private static final String STORAGE_PATH = "dropzone.storagepath";
	private static final String SCREEN_ID = "dropzone.screenid";
	private static final String SCREEN_POSITION_ID = "dropzone.screenposid";
	private static final String USE_MASTER_PWD = "dropzone.usemasterpwd";
	private static final String ACCESS_PWD = "dropzone.password";

	private String masterPassword;
	private String authToken;

	private static ConfigIO instance;
	private Properties PROP;

	private ConfigIO() {
		initConfig();
	}

	public static synchronized ConfigIO getInstance() {
		if (ConfigIO.instance == null) {
			ConfigIO.instance = new ConfigIO();
		}
		// instance.readConfig();
		return ConfigIO.instance;
	}

	/**
	 * Writes config to disk.
	 */
	public void save() {
		writeConfig();
	}

	public void setUsername(String value) {
		PROP.setProperty(ConfigIO.USER_NAME, value);
	}

	public void setPassword(String value) {
		if (isMasterPwdEnabled()) {
			CryptoUtil crypto = new CryptoUtil();
			String encrypted = crypto.encrypt(value, getMasterPassword());
			PROP.setProperty(ConfigIO.ACCESS_PWD, encrypted);
		} else {
			PROP.setProperty(ConfigIO.ACCESS_PWD, value);
		}
	}

	public void setMasterPassword(String value) {
		masterPassword = value;
	}

	public void setAuthToken(String value) {
		authToken = value;
	}

	public void setScreenId(String value) {
		PROP.setProperty(ConfigIO.SCREEN_ID, value);
	}

	public void setScreenPositionId(String value) {
		PROP.setProperty(ConfigIO.SCREEN_POSITION_ID, value);
	}

	public void setMasterPwdEnabled(boolean value) {
		PROP.setProperty(ConfigIO.USE_MASTER_PWD, String.valueOf(value));
	}

	public void setServerUrl(String value) {
		// sanitize URL
		if (value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		PROP.setProperty(ConfigIO.SERVER_URL, value);
	}

	public void setStoragePath(String value) {
		PROP.setProperty(ConfigIO.STORAGE_PATH, value);
	}

	public String getUsername() {
		return PROP.getProperty(ConfigIO.USER_NAME);
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getPassword() {
		String password = PROP.getProperty(ConfigIO.ACCESS_PWD);

		if (isMasterPwdEnabled()) {
			CryptoUtil crypto = new CryptoUtil();
			password = crypto.decrypt(password, getMasterPassword());
		}
		return password;
	}

	public String getMasterPassword() {
		return masterPassword;
	}

	public String getScreenId() {
		return PROP.getProperty(ConfigIO.SCREEN_ID);
	}

	/**
	 * Returns the {@link Pos} id. If id is not an integer, the default
	 * {@link Pos} id is returned.
	 * 
	 * @return
	 */
	public int getScreenPositionId() {
		String prop = PROP.getProperty(ConfigIO.SCREEN_POSITION_ID);
		int id = 1;
		try {
			id = Integer.parseInt(prop);
		} catch (NumberFormatException e) {
			id = Util.getDefaultPositionId();
		}
		return id;
	}

	public boolean isMasterPwdEnabled() {
		String prop = PROP.getProperty(ConfigIO.USE_MASTER_PWD);
		boolean enabled = false;
		try {
			enabled = Boolean.valueOf(prop);
		} catch (NumberFormatException e) {
			enabled = false;
		}
		return enabled;
	}

	public String getServerUrl() {
		return PROP.getProperty(ConfigIO.SERVER_URL);
	}

	public String getStoragePath() {
		return PROP.getProperty(ConfigIO.STORAGE_PATH);
	}

	/**
	 * Creates a default config file if it does not exist and loads its
	 * properties.
	 */
	private void initConfig() {
		if (!isConfigFileAvailable()) {
			// create config file
			createEmptyConfigFile();
		}
		// load properties from file
		readConfig();
	}

	/**
	 * Loads properties from config file.
	 */
	private void readConfig() {
		if (PROP == null) {
			createPropertiesFile();
		}
		String configFile = getConfigPath();
		final File file = new File(configFile);
		if (!file.canRead()) {
			throw new Error("File is not readable: " + file.getAbsolutePath());
		}

		try (FileInputStream inputStream = new FileInputStream(file)) {
			LOGGER.debug("Loading properties from " + file.getAbsolutePath());
			PROP.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new Error("Error loading properties", e);
		}
	}

	/**
	 * Creates an empty config file at the OS-specific config path.
	 */
	private void createEmptyConfigFile() {
		createPropertiesFile();

		String configFile = getConfigPath();
		final File file = new File(configFile);

		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			LOGGER.error("Could not create config file at " + file.getAbsolutePath());
		}
	}

	private void createPropertiesFile() {
		PROP = new Properties();
		PROP.setProperty(ConfigIO.USER_NAME, "");
		PROP.setProperty(ConfigIO.SERVER_URL, "");
		PROP.setProperty(ConfigIO.STORAGE_PATH, "");
		PROP.setProperty(ConfigIO.SCREEN_ID, "");
		PROP.setProperty(ConfigIO.SCREEN_POSITION_ID, String.valueOf(Pos.BOTTOM_RIGHT.getId()));
		PROP.setProperty(ConfigIO.USE_MASTER_PWD, "false");
		PROP.setProperty(ConfigIO.ACCESS_PWD, "");
	}

	/**
	 * Returns true if config file exists, otherwise returns false.
	 * 
	 * @return
	 */
	private boolean isConfigFileAvailable() {
		String configFile = getConfigPath();
		final File file = new File(configFile);
		return file.exists();
	}

	/**
	 * Writes current config to disk.
	 */
	private void writeConfig() {
		if (PROP == null) {
			throw new Error("Properties not initialized");
		}

		try {
			PROP.store(new FileOutputStream(new File(getConfigPath())), null);
		} catch (FileNotFoundException e) {
			throw new Error("Error saving properties", e);
		} catch (IOException e) {
			throw new Error("Error saving properties", e);
		}
	}

	/**
	 * Returns the platform-specific path to the config file
	 * 
	 * @return
	 */
	private static String getConfigPath() {
		String path = "";
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN")) {
			path = System.getenv("APPDATA");
		} else if (OS.contains("MAC")) {
			path = System.getProperty("user.home") + "/Library/";
		} else if (OS.contains("NUX")) {
			path = System.getProperty("user.home");
		} else {
			path = System.getProperty("user.dir");
		}
		path = path + File.separatorChar + APP_NAME + File.separatorChar + DEFAULT_FILE_NAME;
		return path;
	}
}
package org.mn.dropzone.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class I18n {
	private static final String BUNDLE_NAME = "i18n/strings"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private I18n() {
	}

	public static String get(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

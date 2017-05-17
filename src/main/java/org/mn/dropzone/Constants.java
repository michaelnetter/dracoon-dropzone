/**
 * 
 */
package org.mn.dropzone;

import org.mn.dropzone.popover.DropzonePopOver;

import javafx.scene.image.ImageView;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class Constants {
	public static final ImageView ICON_ERROR = new ImageView(
			DropzonePopOver.class.getResource("/images/drop_error.png").toString());
	public static final ImageView ICON_OK = new ImageView(
			DropzonePopOver.class.getResource("/images/drop_ok.png").toString());

	public static final int EXPIRATION_PERIOD = 14; // days

	public static enum OSType {
		WINDOWS, MACOS, UNIX, OTHER;
	}

}

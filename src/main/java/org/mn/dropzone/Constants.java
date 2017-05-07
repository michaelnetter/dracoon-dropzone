/**
 * 
 */
package org.mn.dropzone;

import org.mn.dropzone.popover.DropzonePopOver;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class Constants {
	public static final ImageView ICON_ERROR = new ImageView(
			DropzonePopOver.class.getResource("/images/drop_error.png").toString());
	public static final ImageView ICON_OK = new ImageView(DropzonePopOver.class.getResource("/images/drop_ok.png").toString());

	public static final Color TEXT_COLOR_DEFAULT = Color.rgb(220, 220, 220);
	public static final Color TEXT_COLOR_HOVERED = Color.rgb(120, 120, 120);
	
	public static final int TEXT_SIZE_DEFAULT = 16;
	public static final int TEXT_SIZE_LARGE = 20;
	
	public static final int POPOVER_DURATION = 2500;

}

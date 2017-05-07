package org.mn.dropzone.eventlistener;

import java.util.EventObject;
import javafx.scene.input.DragEvent;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class DropzoneDragEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private DragEvent event;
	private boolean isPasswordProtected;
	private String password;

	public DropzoneDragEvent(DragEvent source, String password, boolean isPasswordProtected) {
		super(source);
		this.event = source;
		this.isPasswordProtected = isPasswordProtected;
		this.password = password;
	}

	public DragEvent getEvent() {
		return event;
	}

	public boolean isPasswordProtected() {
		return isPasswordProtected;
	}

	public String getPassword() {
		return password;
	}

}

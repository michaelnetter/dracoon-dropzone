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
	private boolean askForPassword, setExpiration;

	public DropzoneDragEvent(DragEvent source, boolean askForPassword, boolean setExpiration) {
		super(source);
		this.event = source;
		this.askForPassword = askForPassword;
		this.setExpiration = setExpiration;
	}

	public DragEvent getEvent() {
		return event;
	}

	public boolean isAskForPassword() {
		return askForPassword;
	}

	public boolean isSetExpiration() {
		return setExpiration;
	}
}

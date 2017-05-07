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
	private boolean askForPassword;

	public DropzoneDragEvent(DragEvent source, boolean askForPassword) {
		super(source);
		this.event = source;
		this.askForPassword = askForPassword;

	}

	public DragEvent getEvent() {
		return event;
	}

	public boolean isAskForPassword() {
		return askForPassword;
	}
}

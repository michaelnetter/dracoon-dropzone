package org.mn.dropzone.eventlistener;

import java.util.EventListener;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public interface DropzoneDragEventListener extends EventListener{
	void handleDragEvent(DropzoneDragEvent e);
}

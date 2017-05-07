package org.mn.dropzone.eventlistener;

import java.util.EventListener;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public interface UploadEventListener extends EventListener {
	void handleUploadEvent(UploadEvent e);
}

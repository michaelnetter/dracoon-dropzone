package org.mn.dropzone.eventlistener;

import java.util.EventListener;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public interface SharelinkEventListener extends EventListener {
	void handleSharelinkEvent(SharelinkEvent e);
}

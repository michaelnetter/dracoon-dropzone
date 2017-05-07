package org.mn.dropzone.eventlistener;

import java.util.EventObject;

import org.mn.dropzone.rest.RestClient.Status;
import org.mn.dropzone.rest.model.DownloadShare;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class SharelinkEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private Status status;
	private DownloadShare shareLink;

	public SharelinkEvent(Object source, Status status, DownloadShare shareLink) {
		super(source);
		this.status = status;
		this.shareLink = shareLink;
	}

	public Status getStatus() {
		return status;
	}

	public DownloadShare getSharelink() {
		return shareLink;
	}

}

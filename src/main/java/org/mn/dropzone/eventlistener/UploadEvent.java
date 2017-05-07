package org.mn.dropzone.eventlistener;

import java.util.EventObject;

import org.mn.dropzone.rest.RestClient.Status;

/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class UploadEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private Status status;
	private long nodeId;

	public UploadEvent(Object source, Status status, long nodeId) {
		super(source);
		this.status = status;
		this.nodeId = nodeId;
	}

	public Status getStatus() {
		return status;
	}

	public long getNodeId() {
		return nodeId;
	}

}

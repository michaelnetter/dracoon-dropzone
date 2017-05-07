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
	private boolean isPasswordProtected;
	private String password;

	public UploadEvent(Object source, Status status, long nodeId, boolean isPasswordProtected, String password) {
		super(source);
		this.status = status;
		this.nodeId = nodeId;
		this.isPasswordProtected = isPasswordProtected;
		this.password = password;
	}

	public Status getStatus() {
		return status;
	}

	public long getNodeId() {
		return nodeId;
	}

	public boolean isPasswordProtected() {
		return isPasswordProtected;
	}

	public String getPassword() {
		return password;
	}

}

package org.mn.dropzone.model;

/**
 * Model for screens for internal use
 * 
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class AuthModel {
	public enum Type {
		ACTIVE_DIRECTORY("ad"), E_MAIL("sql"), RADIUS("radius");
		
		String id;
		Type(String id){
			this.id = id;
		}
		
		public String getId(){
			return id;
		}
	}

	private Type type;
	private String name;

	public AuthModel(Type type,String name) {
		this.name = name;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return getType().getId();
	}
	

	public String toString() {
		return name;
	}

}

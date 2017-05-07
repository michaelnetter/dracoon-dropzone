package org.mn.dropzone.model;


/**
 * Dropzone for SDS
 * 
 * @author Michael Netter
 *
 */
public class ScreenPosition {
	/**
	 * All possible arrow locations.
	 */
	public static enum Pos {
		TOP_LEFT(1), TOP_RIGHT(2), BOTTOM_LEFT(3), BOTTOM_RIGHT(4);
		private int id;

		private Pos(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	private Pos pos;
	private String name;

	public ScreenPosition(Pos pos, String i18nName) {
		this.pos = pos;
		this.name = i18nName;
	}

	public Pos getPos() {
		return pos;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}

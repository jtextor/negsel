package visualization;
/*
 * Created on 27.08.2009 by Johannes Textor
 * This Code is licensed under the BSD license:
 * http://www.opensource.org/licenses/bsd-license.php
 */

public class LevelGraphLabel {
	private int level;
	private String label;
	private String extra_label;
	
	/**
	 * 
	 */
	public LevelGraphLabel() {
		super();
	}

	/**
	 * @param level
	 * @param label
	 */
	public LevelGraphLabel(int level, String label, String extra_label) {
		super();
		this.level = level;
		this.label = label;
		this.extra_label = extra_label;
	}

	public String getExtra_label() {
		return extra_label;
	}

	public void setExtra_label(String extra_label) {
		this.extra_label = extra_label;
	}

	public String toString(){
		return label+","+level+","+extra_label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((extra_label == null) ? 0 : extra_label.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LevelGraphLabel other = (LevelGraphLabel) obj;
		if (extra_label == null) {
			if (other.extra_label != null)
				return false;
		} else if (!extra_label.equals(other.extra_label))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (level != other.level)
			return false;
		return true;
	}


}

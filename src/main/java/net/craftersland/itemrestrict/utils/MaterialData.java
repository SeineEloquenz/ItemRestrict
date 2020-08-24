package net.craftersland.itemrestrict.utils;

import org.bukkit.Material;

public class MaterialData {
	
	public final Material type;
	public final short data;
	public final boolean allDataValues;
	public final String description;
	public final String reason;
	
	public MaterialData(Material type, short data, String description, String reason) {
		this.type = type;
		this.data = data;
		this.allDataValues = false;
		this.description = description;
		this.reason = reason;
	}
	
	public MaterialData(Material type, String description, String reason) {
		this.type = type;
		this.data = 0;
		this.allDataValues = true;
		this.description = description;
		this.reason = reason;
	}
	
	private MaterialData(Material type, short data, boolean allDataValues, String description, String reason) {
		this.type = type;
		this.data = data;
		this.allDataValues = allDataValues;
		this.description = description;
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		String returnValue = this.type + ":" + (this.allDataValues?"*":String.valueOf(this.data));
		if(this.description != null) returnValue += ":" + this.description + ":" + this.reason;
		
		return returnValue;
	}
	
	public static MaterialData fromString(String string) {
		
		if(string == null || string.isEmpty()) return null;
		
		String [] parts = string.split(":");
		if(parts.length < 2) return null;
		
		try {
			
			Material type = Material.getMaterial(parts[0]);
			
			short data;
			boolean allDataValues;
			if(parts[1].equals("*"))
			{
				allDataValues = true;
				data = 0;
			}
			else {
				allDataValues = false;
				data = Short.parseShort(parts[1]);
			}
			
			return new MaterialData(type, data, allDataValues, parts.length >= 3 ? parts[2] : "", parts.length >= 4 ? parts[3] : "(No reason provided.)");
		}
		catch(NumberFormatException exception) {
			return null;
		}
	}

}

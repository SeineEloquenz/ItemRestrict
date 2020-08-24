package net.craftersland.itemrestrict.utils;

import java.util.ArrayList;
import java.util.List;

public class MaterialCollection {
	
	List<MaterialData> materials = new ArrayList<MaterialData>();
	
	public void Add(MaterialData material)
	{
		int i;
		for(i = 0; i < this.materials.size() && this.materials.get(i).type.compareTo(material.type) <= 0; i++);
		this.materials.add(i, material);
	}
	
	//returns a MaterialInfo complete with the friendly material name from the config file
	public MaterialData Contains(MaterialData material)
	{
		for(int i = 0; i < this.materials.size(); i++)
		{
			MaterialData thisMaterial = this.materials.get(i);
			if(material.type == thisMaterial.type && (thisMaterial.allDataValues || material.data == thisMaterial.data))
			{
				return thisMaterial;
			}
		}
			
		return null;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < this.materials.size(); i++)
		{
			stringBuilder.append(this.materials.get(i).toString()).append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	public int size()
	{
		return this.materials.size();
	}

	public void clear() 
	{
		this.materials.clear();
	}

}

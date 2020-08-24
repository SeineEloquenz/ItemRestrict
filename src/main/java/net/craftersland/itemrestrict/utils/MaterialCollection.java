package net.craftersland.itemrestrict.utils;

import java.util.ArrayList;
import java.util.List;

public class MaterialCollection {

    final List<MaterialData> materials = new ArrayList<>();

    public void Add(MaterialData material) {
        int i;
        for (i = 0; i < this.materials.size() && this.materials.get(i).type.compareTo(material.type) <= 0; i++) ;
        this.materials.add(i, material);
    }

    //returns a MaterialInfo complete with the friendly material name from the config file
    public MaterialData Contains(MaterialData material) {
        for (MaterialData thisMaterial : this.materials) {
            if (material.type == thisMaterial.type && (thisMaterial.allDataValues || material.data == thisMaterial.data)) {
                return thisMaterial;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MaterialData material : this.materials) {
            stringBuilder.append(material.toString()).append(" ");
        }

        return stringBuilder.toString();
    }

    public int size() {
        return this.materials.size();
    }

    public void clear() {
        this.materials.clear();
    }

}

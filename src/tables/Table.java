package tables;

import java.util.ArrayList;

public class Table {
    private String name;
    private ArrayList<Attribute> attributes = new ArrayList<>();

    public Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAttribute(Attribute a) {
        attributes.add(a);
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasPrimary() {
        boolean hasPrimary = false;

        for (Attribute a : this.attributes) {
            if (a.isPrimaryKey()) {
                hasPrimary = true;
            }
        }

        return hasPrimary;
    }

    public boolean hasForeign() {
        boolean hasForeign = false;

        for (Attribute a : this.attributes) {
            if (a.isPrimaryKey()) {
                hasForeign = true;
            }
        }

        return hasForeign;
    }
}

package globals;

import tables.Attribute;
import tables.Table;

public class ForeignKeyReference {
    private Table referencedTable;
    private Attribute referencedAttribute;

    public Attribute getReferencedAttribute() {
        return referencedAttribute;
    }

    public Table getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(Table referencedTable) {
        this.referencedTable = referencedTable;
    }

    public void setReferencedAttribute(Attribute referencedAttribute) {
        this.referencedAttribute = referencedAttribute;
    }
}

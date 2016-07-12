package tables;

public class Attribute {
    // Standart Attributes
    private String name;
    private Datatype datatyp;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;

    // Foreign Key Values
    private Table refTable;
    private Attribute refAttribute;

    public Attribute(String name, Datatype datatyp, boolean isPrimaryKey, boolean isForeignKey) {
        this.name = name;
        this.datatyp = datatyp;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
    }

    public String getName() {
        return name;
    }

    public Datatype getDatatyp() {
        return datatyp;
    }

    public Boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public Boolean isForeignKey() {
        return isForeignKey;
    }

    public Attribute getRefAttribute() {
        return refAttribute;
    }

    public void setRefAttribute(Attribute refAttribute) {
        this.refAttribute = refAttribute;
    }

    public Table getRefTable() {
        return refTable;
    }

    public void setRefTable(Table refTable) {
        this.refTable = refTable;
    }

    public String getDatatypSqlite() {
        switch (datatyp) {
            case TEXT:
                return "TEXT";
            case INTEGER:
                return "INTEGER";
            case DECIMAL:
                return "NUMERIC";
            default:
                return "";
        }
    }

    public String getDatatypMySQL() {
        switch (datatyp) {
            case TEXT:
                return "TEXT";
            case INTEGER:
                return "INT";
            case DECIMAL:
                return "DOUBLE";
            default:
                return "";
        }
    }
}

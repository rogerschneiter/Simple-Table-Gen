package tables;

import globals.CopyPasteBox;
import globals.LogTags;
import globals.Logger;

import java.util.ArrayList;

import static tables.Snippets.*;

public class Exports {
    public static void toHTML(Table t) {
        String htmlContent = "";
        Logger.log(LogTags.INFO, "Exports.toHTML(): Generating HTML-Code...");

        htmlContent += HTMLMESSAGE;
        htmlContent += HTMLBASE;
        htmlContent += "\n\t\t<h1>" + t.getName() + "</h1>\n";
        htmlContent += "\t\t<table>\n\t\t\t<tr>\n";

        for (Attribute a : t.getAttributes()) {
            htmlContent += "\t\t\t\t<th>";
            htmlContent += a.getName();
            htmlContent += "</th>\n";
        }

        htmlContent += "\t\t\t</tr>\n" + emptyHTMLRow(t.getAttributes().size()) + "\n\t\t</table>\n\t</body>\n</html>";
        new CopyPasteBox("HTML-Export", htmlContent);
    }

    public static void toHTML(ArrayList<Table> allTables) {
        String htmlContent = "";
        Logger.log(LogTags.INFO, "Exports.toHTML(): Generating HTML-Code...");

        htmlContent += HTMLMESSAGE;
        htmlContent += HTMLBASE;

        for (Table t : allTables) {
            htmlContent += "\n\t\t<h1>" + t.getName() + "</h1>\n";
            htmlContent += "\n\t\t<table>\n\t\t\t<tr>\n";

            for (Attribute a : t.getAttributes()) {
                htmlContent += "\t\t\t\t<th>";
                htmlContent += a.getName();
                htmlContent += "</th>\n";
            }

            htmlContent += "\t\t\t</tr>\n" + emptyHTMLRow(t.getAttributes().size()) + "</table>";
        }

        htmlContent += "\n\t</body>\n</html>";
        new CopyPasteBox("HTML-Export", htmlContent);
    }

    public static void toSQLite(Table t) {
        String sqliteContent = "";
        Logger.log(LogTags.INFO, "Exports.toSQLite(): Generating SQLite-Script...");

        sqliteContent += SQLITEMESSAGE;
        sqliteContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

        if (t.hasPrimary()) {
            for (Attribute a : t.getAttributes()) {
                if (a.isPrimaryKey()) {
                    sqliteContent += "\t" + a.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT,\n";
                }
            }
        }

        for (Attribute a : t.getAttributes()) {
            if (!a.isPrimaryKey()) {
                if (a.isNotNull()) {
                    sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + " NOT NULL,\n";
                } else {
                    sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + ",\n";
                }
            }
        }

        sqliteContent = sqliteContent.substring(0, sqliteContent.length() - 2);

        sqliteContent += "\n);";

        new CopyPasteBox("SQLite-Export", sqliteContent);
    }

    public static void toSQLite(ArrayList<Table> allTables) {
        String sqliteContent = "";
        Logger.log(LogTags.INFO, "Exports.toSQLite(): Generating SQLite-Script...");

        sqliteContent += SQLITEMESSAGE;

        for (Table t : allTables) {

            if (!t.hasForeign()) {

                sqliteContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

                if (t.hasPrimary()) {
                    for (Attribute a : t.getAttributes()) {
                        if (a.isPrimaryKey()) {
                            sqliteContent += "\t" + a.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT,\n";
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (!a.isPrimaryKey()) {
                        if (a.isNotNull()) {
                            sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + " NOT NULL,\n";
                        } else {
                            sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + ",\n";
                        }
                    }
                }

                sqliteContent = sqliteContent.substring(0, sqliteContent.length() - 2);

                sqliteContent += "\n);\n\n";
            }

        }

        for (Table t : allTables) {
            if (t.hasForeign()) {
                sqliteContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

                if (t.hasPrimary()) {
                    for (Attribute a : t.getAttributes()) {
                        if (a.isPrimaryKey()) {
                            sqliteContent += "\t" + a.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT,\n";
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (!a.isPrimaryKey()) {
                        if (a.isForeignKey()) {
                            if (a.isNotNull()) {
                                sqliteContent += "\t" + a.getName() + " INTEGER NOT NULL,\n";
                            } else {
                                sqliteContent += "\t" + a.getName() + " INTEGER,\n";
                            }
                        } else {
                            if (a.isNotNull()) {
                                sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + " NOT NULL,\n";
                            } else {
                                sqliteContent += "\t" + a.getName() + " " + a.getDatatypSqlite() + ",\n";
                            }
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (a.isForeignKey()) {
                        sqliteContent += "\t" + "FOREIGN KEY(" + a.getName() + ") REFERENCES "
                                + a.getRefTable().getName() + "(" + a.getRefAttribute().getName() + "),\n";
                    }
                }

                sqliteContent = sqliteContent.substring(0, sqliteContent.length() - 2);

                sqliteContent += "\n);\n\n";
            }

        }

        new CopyPasteBox("SQLite-Export", sqliteContent);
    }

    public static void toMySQL(Table t) {
        String mysqlContent = "";
        Logger.log(LogTags.INFO, "Exports.toMySQL(): Generating MySQL-Script...");

        mysqlContent += MYSQLMESSAGE;
        mysqlContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

        if (t.hasPrimary()) {
            for (Attribute a : t.getAttributes()) {
                if (a.isPrimaryKey()) {
                    mysqlContent += "\t" + a.getName() + " INT AUTO_INCREMENT,\n";
                }
            }
        }

        for (Attribute a : t.getAttributes()) {
            if (!a.isPrimaryKey()) {
                if (a.isNotNull()) {
                    if (a.getSize() != 0) {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + "(" + a.getSize() + ") NOT NULL,\n";
                    } else {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + " NOT NULL,\n";
                    }
                } else {
                    if (a.getSize() != 0) {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + "(" + a.getSize() + "),\n";
                    } else {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + ",\n";
                    }
                }
            }
        }

        if (t.hasPrimary()) {
            for (Attribute a : t.getAttributes()) {
                if (a.isPrimaryKey()) {
                    mysqlContent += "\t" + "PRIMARY KEY(" + a.getName() + "),\n";
                }
            }
        }

        mysqlContent = mysqlContent.substring(0, mysqlContent.length() - 2);

        mysqlContent += "\n);";


        new CopyPasteBox("MySQL-Export", mysqlContent);
    }

    public static void toMySQL(ArrayList<Table> allTables) {
        String mysqlContent = "";
        Logger.log(LogTags.INFO, "Exports.toMySQL(): Generating MySQL-Script...");

        mysqlContent += MYSQLMESSAGE;

        for (Table t : allTables) {

            if (!t.hasForeign()) {

                mysqlContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

                if (t.hasPrimary()) {
                    for (Attribute a : t.getAttributes()) {
                        if (a.isPrimaryKey()) {
                            mysqlContent += "\t" + a.getName() + " INT AUTO_INCREMENT,\n";
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (a.isNotNull()) {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + " NOT NULL,\n";
                    } else {
                        mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + ",\n";
                    }
                }

                mysqlContent = mysqlContent.substring(0, mysqlContent.length() - 2);

                mysqlContent += "\n);\n\n";
            }

        }

        for (Table t : allTables) {

            if (t.hasForeign()) {
                mysqlContent += "CREATE TABLE IF NOT EXISTS " + t.getName() + " ( \n";

                if (t.hasPrimary()) {
                    for (Attribute a : t.getAttributes()) {
                        if (a.isPrimaryKey()) {
                            mysqlContent += "\t" + a.getName() + " INT AUTO_INCREMENT,\n";
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (!a.isPrimaryKey()) {
                        if (a.isForeignKey()) {
                            if (a.isNotNull()) {
                                mysqlContent += "\t" + a.getName() + " INT NOT NULL,\n";
                            } else {
                                mysqlContent += "\t" + a.getName() + " INT,\n";
                            }
                        } else {
                            if (a.isNotNull()) {
                                if (a.getSize() != 0) {
                                    mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + "(" + a.getSize() + ") NOT NULL,\n";
                                } else {
                                    mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + " NOT NULL,\n";
                                }
                            } else {
                                if (a.getSize() != 0) {
                                    mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + "(" + a.getSize() + "),\n";
                                } else {
                                    mysqlContent += "\t" + a.getName() + " " + a.getDatatypMySQL() + ",\n";
                                }
                            }
                        }
                    }
                }

                for (Attribute a : t.getAttributes()) {
                    if (a.isForeignKey()) {
                        mysqlContent += "\t" + "FOREIGN KEY(" + a.getName() + ") REFERENCES "
                                + a.getRefTable().getName() + "(" + a.getRefAttribute().getName() + "),\n";
                    }
                }

                mysqlContent = mysqlContent.substring(0, mysqlContent.length() - 2);

                mysqlContent += "\n);\n\n";
            }

        }

        new CopyPasteBox("MySQL-Export", mysqlContent);
    }
}

package tables;

public class Snippets {

    // Script texts
    public static String HTMLMESSAGE = "<!--Auto-Generated HTML-Code by Simple Table Generator V 0.1-->\n";
    public static String SQLITEMESSAGE = "--Auto-Generated SQLite-Script by Simple Table Generator V 0.1\n";
    public static String MYSQLMESSAGE = "--Auto-Generated MySQL-Script by Simple Table Generator V 0.1\n";

    // Standard HTML structure
    public static String HTMLBASE = " <!DOCTYPE html>\n" +
            "<html lang=\"de\">\n" +
            "\t<head>\n" +
            "\t\t<style>table, tr, th, td {\n" +
            "\t\t\tborder: 1px solid black; }\n" +
            "\t\t</style>\n" +
            "\t\t<meta charset=\"utf-8\">\n" +
            "\t\t<title>Tablename</title>\n" +
            "\t\t<meta charset=\"utf-8\">\n" +
            "\t</head>\n" +
            "\t<body>\n";

    // Empty table row in HTML
    public static String emptyHTMLRow(int columns) {
        String emptyRow = "";

        emptyRow += "\t\t\t<tr>\n";

        for (int i = 0; i < columns; i++) {
            emptyRow += "\t\t\t\t<td>";
            emptyRow += "CONTENT";
            emptyRow += "</td>\n";
        }

        emptyRow += "\t\t\t</tr>";

        return emptyRow;
    }
}

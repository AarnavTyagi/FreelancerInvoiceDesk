package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/** Writes a report to a CSV file that can be opened in Excel. */
public class CsvExporter {

    private CsvExporter() { }

    public static void export(File file, String[] headers, List<String[]> rows) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(toLine(headers));
            out.newLine();
            for (String[] row : rows) {
                out.write(toLine(row));
                out.newLine();
            }
        }
    }

    // Wraps every cell in quotes so names containing commas do not break the columns
    private static String toLine(String[] cells) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append('"').append(cells[i].replace("\"", "\"\"")).append('"');
        }
        return line.toString();
    }
}

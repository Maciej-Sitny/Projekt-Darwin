package agh.ics.oop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    private FileWriter fileWriter;
    private String filename;

    public CSVWriter(String filename, List<String> headers) throws IOException {
        this.filename = filename;
        fileWriter = new FileWriter(filename, true);
        if (new java.io.File(filename).length() == 0) {
            writeLine(headers);
        }
    }

    public void writeLine(List<String> values) throws IOException {
        String line = String.join(",", values);
        fileWriter.write(line + "\n");
    }

    public void close() throws IOException {
        fileWriter.close();
    }

    public void open() throws IOException {
        fileWriter = new FileWriter(filename, true);
    }

    public void clearFile() throws IOException {
        fileWriter = new FileWriter(filename, false);
        fileWriter.close();
        open();
    }
}

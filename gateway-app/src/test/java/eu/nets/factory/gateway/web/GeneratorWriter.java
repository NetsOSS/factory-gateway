package eu.nets.factory.gateway.web;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class GeneratorWriter implements Closeable {
    private final boolean silent;

    private final File file;

    private final List<String> lines = new ArrayList<>(10_000);

    private String indent = "";

    public GeneratorWriter(boolean silent, File file) {
        this.silent = silent;
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create directory: " + dir.getAbsolutePath());
        }
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            lines.forEach(writer::println);
        }
    }

    public GeneratorWriter push() {
        indent = indent + "  ";
        return this;
    }

    public GeneratorWriter pop() {
        indent = indent.substring(0, indent.length() - 2);
        return this;
    }

    public GeneratorWriter println(String s) {
        lines.add(indent + s);
        if (silent) {
            return this;
        }
        out.print(indent);
        out.println(s);
        return this;
    }
}

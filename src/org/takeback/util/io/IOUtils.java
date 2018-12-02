// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.io;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class IOUtils
{
    private static final int BUFFER_SIZE = 8192;
    
    public static long write(final InputStream is, final OutputStream os) throws IOException {
        return write(is, os, 8192);
    }
    
    public static long write(final InputStream is, final OutputStream os, final int bufferSize) throws IOException {
        long total = 0L;
        final byte[] buff = new byte[bufferSize];
        while (is.available() > 0) {
            final int read = is.read(buff, 0, buff.length);
            if (read > 0) {
                os.write(buff, 0, read);
                total += read;
            }
        }
        return total;
    }
    
    public static String read(final Reader reader) throws IOException {
        final StringWriter writer = new StringWriter();
        try {
            write(reader, writer);
            return writer.getBuffer().toString();
        }
        finally {
            writer.close();
        }
    }
    
    public static long write(final Writer writer, final String string) throws IOException {
        final Reader reader = new StringReader(string);
        try {
            return write(reader, writer);
        }
        finally {
            reader.close();
        }
    }
    
    public static long write(final Reader reader, final Writer writer) throws IOException {
        return write(reader, writer, 8192);
    }
    
    public static long write(final Reader reader, final Writer writer, final int bufferSize) throws IOException {
        long total = 0L;
        final char[] buf = new char[8192];
        int read;
        while ((read = reader.read(buf)) != -1) {
            writer.write(buf, 0, read);
            total += read;
        }
        return total;
    }
    
    public static String[] readLines(final File file) throws IOException {
        if (file == null || !file.exists() || !file.canRead()) {
            return new String[0];
        }
        return readLines(new FileInputStream(file));
    }
    
    public static String[] readLines(final InputStream is) throws IOException {
        final List<String> lines = new ArrayList<String>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[0]);
        }
        finally {
            reader.close();
        }
    }
    
    public static void writeLines(final OutputStream os, final String[] lines) throws IOException {
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));
        try {
            for (final String line : lines) {
                writer.println(line);
            }
            writer.flush();
        }
        finally {
            writer.close();
        }
    }
    
    public static void writeLines(final File file, final String[] lines) throws IOException {
        if (file == null) {
            throw new IOException("File is null.");
        }
        writeLines(new FileOutputStream(file), lines);
    }
    
    public static void appendLines(final File file, final String[] lines) throws IOException {
        if (file == null) {
            throw new IOException("File is null.");
        }
        writeLines(new FileOutputStream(file, true), lines);
    }
}

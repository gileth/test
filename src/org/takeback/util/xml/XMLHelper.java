// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.xml;

import java.io.FileOutputStream;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import java.io.OutputStream;
import org.dom4j.io.SAXReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.dom4j.DocumentException;
import java.io.File;
import org.dom4j.DocumentHelper;
import org.dom4j.Document;

public class XMLHelper
{
    private static final String CHARSET = "UTF-8";
    
    public static Document createDocument() {
        return DocumentHelper.createDocument();
    }
    
    public static Document getDocument(final String fileName) throws DocumentException, IOException {
        return getDocument(new File(fileName));
    }
    
    public static Document getDocument(final File file) throws DocumentException, IOException {
        return getDocument(new FileInputStream(file));
    }
    
    public static Document getDocument(final InputStream ins) throws DocumentException, IOException {
        final SAXReader oReader = new SAXReader();
        try {
            return oReader.read(ins);
        }
        finally {
            ins.close();
        }
    }
    
    public static void putDocument(final OutputStream outs, final Document doc) throws IOException {
        final OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        try {
            format.setEncoding("UTF-8");
            writer = new XMLWriter(outs, format);
            writer.setEscapeText(false);
            writer.write(doc);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public static void putDocument(final OutputStream outs, final String doc) throws IOException {
        final OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        try {
            format.setEncoding("UTF-8");
            writer = new XMLWriter(outs, format);
            writer.setEscapeText(false);
            writer.write(doc);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public static void putDocument(final File file, final Document doc) throws IOException {
        final OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        try {
            format.setEncoding("UTF-8");
            writer = new XMLWriter((OutputStream)new FileOutputStream(file), format);
            writer.setEscapeText(true);
            writer.write(doc);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

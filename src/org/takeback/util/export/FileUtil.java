// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;

public class FileUtil
{
    public static String currentWorkDir;
    
    public static String leftPad(final String str, final int length, final char ch) {
        if (str.length() >= length) {
            return str;
        }
        final char[] chs = new char[length];
        Arrays.fill(chs, ch);
        final char[] src = str.toCharArray();
        System.arraycopy(src, 0, chs, length - src.length, src.length);
        return new String(chs);
    }
    
    public static boolean delete(final String fileName) {
        boolean result = false;
        final File f = new File(fileName);
        result = (!f.exists() || f.delete());
        return result;
    }
    
    public static ArrayList<File> getAllFiles(final String dirPath) {
        final File dir = new File(dirPath);
        final ArrayList<File> files = new ArrayList<File>();
        if (dir.isDirectory()) {
            final File[] fileArr = dir.listFiles();
            for (int i = 0; i < fileArr.length; ++i) {
                final File f = fileArr[i];
                if (f.isFile()) {
                    files.add(f);
                }
                else {
                    files.addAll(getAllFiles(f.getPath()));
                }
            }
        }
        return files;
    }
    
    public static ArrayList<File> getDirFiles(final String dirPath) {
        final File path = new File(dirPath);
        final File[] fileArr = path.listFiles();
        final ArrayList<File> files = new ArrayList<File>();
        for (final File f : fileArr) {
            if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }
    
    public static ArrayList<File> getDirFiles(final String dirPath, final String suffix) {
        final File path = new File(dirPath);
        final File[] fileArr = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                final String lowerName = name.toLowerCase();
                final String lowerSuffix = suffix.toLowerCase();
                return lowerName.endsWith(lowerSuffix);
            }
        });
        final ArrayList<File> files = new ArrayList<File>();
        for (final File f : fileArr) {
            if (f.isFile()) {
                files.add(f);
            }
        }
        return files;
    }
    
    public static String read(final String fileName) throws IOException {
        final File f = new File(fileName);
        final FileInputStream fs = new FileInputStream(f);
        String result = null;
        final byte[] b = new byte[fs.available()];
        fs.read(b);
        fs.close();
        result = new String(b);
        return result;
    }
    
    public static boolean write(final String fileName, final String fileContent) throws IOException {
        return write(fileName, fileContent, true, true);
    }
    
    public static boolean write(final String fileName, final String fileContent, final boolean autoCreateDir, final boolean autoOverwrite) throws IOException {
        return write(fileName, fileContent.getBytes(), autoCreateDir, autoOverwrite);
    }
    
    public static boolean write(final String fileName, final byte[] contentBytes, final boolean autoCreateDir, final boolean autoOverwrite) throws IOException {
        boolean result = false;
        if (autoCreateDir) {
            createDirs(fileName);
        }
        if (autoOverwrite) {
            delete(fileName);
        }
        final File f = new File(fileName);
        final FileOutputStream fs = new FileOutputStream(f);
        fs.write(contentBytes);
        fs.flush();
        fs.close();
        result = true;
        return result;
    }
    
    public static boolean append(final String fileName, final String fileContent) throws IOException {
        boolean result = false;
        final File f = new File(fileName);
        if (f.exists()) {
            final RandomAccessFile rFile = new RandomAccessFile(f, "rw");
            final byte[] b = fileContent.getBytes();
            final long originLen = f.length();
            rFile.setLength(originLen + b.length);
            rFile.seek(originLen);
            rFile.write(b);
            rFile.close();
        }
        result = true;
        return result;
    }
    
    public List<String> splitBySize(final String fileName, final int byteSize) throws IOException {
        final List<String> parts = new ArrayList<String>();
        final File file = new File(fileName);
        final int count = (int)Math.ceil(file.length() / byteSize);
        final int countLen = (count + "").length();
        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(count, count * 3, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(count * 2));
        for (int i = 0; i < count; ++i) {
            final String partFileName = file.getPath() + "." + leftPad(i + 1 + "", countLen, '0') + ".part";
            threadPool.execute(new SplitRunnable(byteSize, i * byteSize, partFileName, file));
            parts.add(partFileName);
        }
        return parts;
    }
    
    public void mergePartFiles(final String dirPath, final String partFileSuffix, final int partFileSize, final String mergeFileName) throws IOException {
        final ArrayList<File> partFiles = getDirFiles(dirPath, partFileSuffix);
        Collections.sort(partFiles, new FileComparator());
        final RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFileName, "rw");
        randomAccessFile.setLength(partFileSize * (partFiles.size() - 1) + partFiles.get(partFiles.size() - 1).length());
        randomAccessFile.close();
        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(partFiles.size(), partFiles.size() * 3, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(partFiles.size() * 2));
        for (int i = 0; i < partFiles.size(); ++i) {
            threadPool.execute(new MergeRunnable(i * partFileSize, mergeFileName, partFiles.get(i)));
        }
    }
    
    public static void createDirs(final String filePath) {
        final File file = new File(filePath);
        final File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
    
    static {
        FileUtil.currentWorkDir = System.getProperty("user.dir") + "\\";
    }
    
    private class FileComparator implements Comparator<File>
    {
        @Override
        public int compare(final File o1, final File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
    
    private class SplitRunnable implements Runnable
    {
        int byteSize;
        String partFileName;
        File originFile;
        int startPos;
        
        public SplitRunnable(final int byteSize, final int startPos, final String partFileName, final File originFile) {
            this.startPos = startPos;
            this.byteSize = byteSize;
            this.partFileName = partFileName;
            this.originFile = originFile;
        }
        
        @Override
        public void run() {
            try {
                final RandomAccessFile rFile = new RandomAccessFile(this.originFile, "r");
                final byte[] b = new byte[this.byteSize];
                rFile.seek(this.startPos);
                final int s = rFile.read(b);
                final OutputStream os = new FileOutputStream(this.partFileName);
                os.write(b, 0, s);
                os.flush();
                os.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class MergeRunnable implements Runnable
    {
        long startPos;
        String mergeFileName;
        File partFile;
        
        public MergeRunnable(final long startPos, final String mergeFileName, final File partFile) {
            this.startPos = startPos;
            this.mergeFileName = mergeFileName;
            this.partFile = partFile;
        }
        
        @Override
        public void run() {
            try {
                final RandomAccessFile rFile = new RandomAccessFile(this.mergeFileName, "rw");
                rFile.seek(this.startPos);
                final FileInputStream fs = new FileInputStream(this.partFile);
                final byte[] b = new byte[fs.available()];
                fs.read(b);
                fs.close();
                rFile.write(b);
                rFile.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

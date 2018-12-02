// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.File;

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

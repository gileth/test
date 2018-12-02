// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.File;

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

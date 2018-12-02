// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.io.File;
import java.io.FilenameFilter;

static final class FileUtil$1 implements FilenameFilter {
    final /* synthetic */ String val$suffix;
    
    @Override
    public boolean accept(final File dir, final String name) {
        final String lowerName = name.toLowerCase();
        final String lowerSuffix = this.val$suffix.toLowerCase();
        return lowerName.endsWith(lowerSuffix);
    }
}
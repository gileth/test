// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.export;

import java.io.File;
import java.util.Comparator;

private class FileComparator implements Comparator<File>
{
    @Override
    public int compare(final File o1, final File o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}

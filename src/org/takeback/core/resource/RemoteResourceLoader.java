// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.resource;

import java.io.IOException;
import org.springframework.core.io.Resource;

public interface RemoteResourceLoader
{
    Resource load(final String p0, final boolean p1) throws IOException;
    
    Resource load(final String p0) throws IOException;
}

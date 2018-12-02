// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.core.resource;

import org.springframework.core.io.DefaultResourceLoader;
import org.apache.commons.lang3.StringUtils;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.File;
import org.takeback.util.io.IOUtils;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.takeback.util.ApplicationContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.context.ResourceLoaderAware;

public class ResourceCenter implements ResourceLoaderAware
{
    private static ResourceLoader loader;
    private static RemoteResourceLoader remoteLoader;
    
    public static Resource load(final String path) throws IOException {
        Resource r = ResourceCenter.loader.getResource("classpath:" + path);
        if (r.exists()) {
            return r;
        }
        r = ResourceCenter.loader.getResource(path);
        if (r.exists()) {
            return r;
        }
        if (ResourceCenter.remoteLoader != null) {
            return ResourceCenter.remoteLoader.load(path, !ApplicationContextHolder.isDevMode());
        }
        throw new FileNotFoundException("file not found:" + path);
    }
    
    public static Resource load(final String pathPrefix, final String path) throws IOException {
        final Resource r = ResourceCenter.loader.getResource(pathPrefix + path);
        if (r.exists()) {
            return r;
        }
        throw new FileNotFoundException("file not found:" + path);
    }
    
    public static void write(final Resource r, final OutputStream output) throws IOException {
        final String protocol = r.getURL().getProtocol();
        final boolean isFileSystem = protocol.startsWith("file");
        if (ApplicationContextHolder.isDevMode() && isFileSystem) {
            final File f = r.getFile();
            final InputStream input = new FileInputStream(f);
            try {
                IOUtils.write(input, output);
            }
            finally {
                input.close();
            }
        }
        else {
            final InputStream input2 = r.getInputStream();
            try {
                IOUtils.write(input2, output);
            }
            finally {
                input2.close();
            }
        }
    }
    
    public void setResourceLoader(final ResourceLoader appContextLoader) {
        ResourceCenter.loader = appContextLoader;
    }
    
    public void setRemoteResourceLoader(final RemoteResourceLoader loader) {
        ResourceCenter.remoteLoader = loader;
    }
    
    public static String getAbstractClassPath() throws URISyntaxException {
        return new File(ResourceCenter.loader.getClassLoader().getResource("").toURI()).getAbsolutePath();
    }
    
    public static String getAbstractClassPath(final String path) throws URISyntaxException {
        return StringUtils.join((Object[])new String[] { getAbstractClassPath(), "/", path });
    }
    
    static {
        ResourceCenter.loader = (ResourceLoader)new DefaultResourceLoader();
    }
}

package org.shutterspiny.plugin.ShutterPvP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarUtils {
	
	public static void addClassPath(File file) throws IOException {
		URL url = getJarUrl(file);
        URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL",
                    new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { url });
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url
                    + " to system classloader");
        }
    }
	
	private final static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) > 0) {
                out.write(buff, 0, n);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }
	
	public static boolean extractResource(JarFile jar, String path, File f) throws IOException {
		InputStream in = getResource(jar, path);
		if(in == null) return false;
		OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
		copyInputStream(in, out);
		return true;
	}
	
	public static URL getJarUrl(File file) throws IOException {
        return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
    }
	
	private static InputStream getResource(JarFile jar, String path) throws IOException {
		Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (!je.getName().contains(path)) continue;
            return new BufferedInputStream(jar.getInputStream(je));
        }
        return null;
	}
	
	public static JarFile getRunningJar() throws IOException {
        String path = new File(JarUtils.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();
        path = URLDecoder.decode(path, "UTF-8");
        return new JarFile(path);
    }
	
}

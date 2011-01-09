/*
 * Syntelos-X
 * Copyright (C) 2009 John Pritchard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package lxl.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A web desktop style {@link ClassLoader}.
 * 
 * @see http://jnlp-loader.googlecode.com/
 * @author jdp
 */
public class ClassLoader
    extends java.lang.ClassLoader
{

    public final static java.lang.ClassLoader Current(){
        return Thread.currentThread().getContextClassLoader();
    }

    public volatile static boolean Debug = true;

    public volatile static boolean Test = true;

    protected final static void LoadTestDebug(){
        {
            String test = System.getProperty("lxl.net.ClassLoader.Test");
            Test = (null != test && "true".equalsIgnoreCase(test));
        }
        {
            String debug = System.getProperty("lxl.net.ClassLoader.Debug");
            Debug = (null != debug && "true".equalsIgnoreCase(debug));
        }
    }


    public final static File GetTempDir(java.lang.ClassLoader loader){
        if (loader instanceof ClassLoader)
            return ((ClassLoader)loader).temp;
        else
            return null;
    }
    public final static File GetCacheDir(java.lang.ClassLoader loader){
        if (loader instanceof ClassLoader)
            return ((ClassLoader)loader).cache;
        else
            return null;
    }
    public final static File GetTempDir(){
        return GetTempDir(Current());
    }
    public final static File GetTempFile(String path){
        if (null == path)
            return null;
        else {
            File file = new File(GetTempDir(),path);
            return file;
        }
    }
    public static File GetCacheDir(){
        return GetCacheDir(Current());
    }
    public static File GetCacheFile(String path){
        if (null == path)
            return null;
        else {
            File file = new File(GetCacheDir(),path);
            return file;
        }
    }

    /**
     */
    protected static void LogFileWrite(File file)
        throws IOException
    {
        Object test = Current();
        if (test instanceof ClassLoader){
            ClassLoader current = (ClassLoader)test;
            current.logFileWrite(file);
        }
        else if (ClassLoader.Test)
            System.err.println(String.format("%s: wrote file '%s'.",Thread.currentThread().getName(),file.getPath()));
    }


    protected volatile File temp, cache;

    protected volatile URL base;

    protected final String[] argv;

    protected final File alternative;

    protected final boolean clean, cleanOnly;


    /**
     * Class default behavior
     */
    private ClassLoader()
        throws IOException
    {
        this(null);
    }
    /**
     * @param argv ClassLoader function arguments.
     */
    public ClassLoader(String[] argv)
        throws IOException
    {
        this(null, argv);
    }
    /**
     * @param dir Provide a static directory name or default to jnlp href name.
     * @param argv ClassLoader function arguments.
     */
    public ClassLoader(String dir, String[] argv)
        throws IOException
    {
        super();
            
        Thread.currentThread().setContextClassLoader(this);

        if (null != argv && 0 != argv.length){
            int argx = 0;
            if ("clean".equals(argv[argx])){
                argx += 1;
                this.clean = true;
                if (argx < argv.length){
                    if ("only".equals(argv[argx])){
                        argx += 1;
                        this.cleanOnly = true;
                    }
                    else
                        this.cleanOnly = false;
                }
                else
                    this.cleanOnly = false;
            }
            else {
                this.clean = false;
                this.cleanOnly = false;
            }

            if (argx < argv.length){
                File alt = new File(argv[argx]);
                if (alt.exists() && alt.isDirectory()){
                    argx += 1;
                    this.alternative = alt;
                    this.argv = ShiftTo(argv,argx);
                }
                else {
                    this.argv = ShiftTo(argv,argx);
                    this.alternative = null;
                }
            }
            else {
                this.argv = ShiftTo(argv,argx);
                this.alternative = null;
            }
        }
        else {
            this.argv = null;
            this.clean = false;
            this.cleanOnly = false;
            this.alternative = null;
        }
        if (null != dir)
            this.temp = new File(dir);
        else
            this.temp = new File("lxl.net");

        this.cache = new File(this.temp,"cache");
    }


    

    public URL getCodebase(){
        return this.base;
    }
    protected String findLibrary(String basename){
        throw new UnsupportedOperationException();
    }
    protected Class<?> findClass(String biname) throws ClassNotFoundException {
        throw new ClassNotFoundException(biname);
    }
    protected URL findResource(String filepath){
        throw new UnsupportedOperationException();
    }

    public final File getTempDir(){
        File dir = this.temp;
        if (null == dir)
            throw new IllegalStateException();
        else
            return dir;
    }
    public final File getTempFile(String path){
        if (null == path)
            return null;
        else {
            File file = new File(this.temp,path);
            return file;
        }
    }
    public final File getCacheDir(){
        return this.cache;
    }
    public final File getCacheFile(String path){
        if (null == path)
            return null;
        else {
            File file = new File(this.cache,path);
            return file;
        }
    }
    public final lxl.net.Find findTemp(){
        return new Find(true);
    }
    public final lxl.net.Find findCache(){
        return new Find(false);
    }
    public final Clean cleanTemp(){
        return new Clean(this.findTemp());
    }
    public final Clean cleanCache(){
        return new Clean(this.findCache());
    }
    /**
     * @return Get arg will not return null.
     */
    public boolean hasMainArg(int idx){
        if (-1 < idx){
            String[] argv = this.argv;
            return (null != argv && idx < argv.length);
        }
        else
            return false;
    }
    /**
     * @return Main argument (not consumed) indexed from zero, or null
     * for index out of bounds.
     */
    public String getMainArg(int idx){
        if (-1 < idx){
            String[] argv = this.argv;
            if (null != argv && idx < argv.length)
                return argv[idx];
        }
        return null;
    }
    protected void logFileWrite(File file) throws IOException {

        if (Test)
            System.err.println(String.format("%s: wrote file '%s'.",Thread.currentThread().getName(),file.getPath()));
    }

    public static URL SourceUrl(URL source){
        java.lang.ClassLoader current = Current();
        if (current instanceof ClassLoader){
            ClassLoader ccurrent = (ClassLoader)current;
            if (null != ccurrent.alternative){
                String name = ContentLoader.SourcePath(source);
                try {
                    File file = new File(ccurrent.alternative,name);
                    return file.toURL();
                }
                catch (MalformedURLException exc){
                    throw new RuntimeException(name,exc);
                }
            }
        }
        return source;
    }
    public static URL SourceUrl(String source){
        try {
            return SourceUrl(new URL(source));
        }
        catch (MalformedURLException exc){

            java.lang.ClassLoader current = Current();
            if (current instanceof ClassLoader){
                ClassLoader ccurrent = (ClassLoader)current;
                if (null != ccurrent.alternative){
                    try {
                        File file = new File(ccurrent.alternative,source);
                        return file.toURL();
                    }
                    catch (MalformedURLException exc2){
                        throw new RuntimeException(source,exc2);
                    }
                }
                else {
                    URL base = ccurrent.getCodebase();
                    if (null != base){
                        try {
                            return new URL(base,source);
                        }
                        catch (MalformedURLException exc2){
                            throw new RuntimeException(source,exc2);
                        }
                    }
                }
            }
            throw new RuntimeException(source,exc);
        }
    }
    public static URL SourceUrl(String base, String path){
        return SourceUrl(Fcat(base,path));
    }
    public static byte[] ContentOf(File file){
        if (file.isFile() && file.canRead()){
            long llength = file.length();
            if (0L < llength && llength < Integer.MAX_VALUE){
                int length = (int)llength;
                InputStream in = null;
                try {
                    in = new FileInputStream(file);
                    byte[] content = new byte[length];
                    int offset = 0, read;
                    while (0 < (read = in.read(content,offset,length)) || 0 < length){
                        offset += read;
                        length -= read;
                    }
                    return content;
                }
                catch (Throwable thrown){
                    thrown.printStackTrace();
                }
                finally {
                    if (null != in){
                        try {
                            in.close();
                        }
                        catch (IOException ignore){
                        }
                    }
                }
            }
        }
        return null;
    }
    /**
     * Concatenate with '/' file path separator.
     */
    public final static URL NewURL(URL base, String path)
	throws MalformedURLException
    {
	return new URL(Fcat(base.toExternalForm(),path));
    }
    /**
     * Concatenate with '/' file path separator.
     */
    public final static String Fcat(String a, String b){
        if (null != a && 0 != a.length()){
            if (null != b && 0 != b.length()){
                if ('/' == a.charAt(a.length()-1))
                    return (a+b);
                else if ('/' == (b.charAt(0)))
                    return (a+b);
                else
                    return (a+'/'+b);
            }
            else
                return a;
        }
        else
            return b;
    }
    /**
     * Concatenate with '.' dot separator.
     */
    public final static String Pcat(String a, String b, String c){
        return Pcat(a,Pcat(b,c));
    }
    public final static String Pcat(String a, String b){
        if (null != a && 0 != a.length()){
            if (null != b && 0 != b.length()){
                if ('.' == a.charAt(a.length()-1))
                    return (a+b);
                else if ('.' == (b.charAt(0)))
                    return (a+b);
                else
                    return (a+'.'+b);
            }
            else
                return a;
        }
        else
            return b;
    }
    /**
     * Truncate argv array to include the argument start.
     * @param argv Array of string
     * @param start Inclusive starting argv offset (position zero in
     * the returned result)
     * @return Shifted array, or null for empty.
     */
    public final static String[] ShiftTo(String[] argv, int start){
        if (null == argv || 0 >= start)
            return argv;
        else {
            int argc = argv.length;
            if (start < argc){
                int nlen = (argc - start);
                String[] copier = new String[nlen];
                System.arraycopy(argv,start,copier,0,nlen);
                return copier;
            }
            else
                return null;
        }
    }
}

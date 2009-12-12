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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.jnlp.JNLPRandomAccessFile;

/**
 * Disk cache content download and unpack.
 * 
 * @see AbstractContent
 */
public abstract class ContentLoader
    extends Object
{

    public final static URL SourceUrl(URL source){
        return ClassLoader.SourceUrl(source);
    }
    public final static URL SourceUrl(String base, String path){
        return ClassLoader.SourceUrl(base,path);
    }
    public final static URL SourceUrl(String url){
        return ClassLoader.SourceUrl(url);
    }


    public final URL source;

    public final String sourcePath;

    public final boolean lazy;

    private volatile long requested;

    protected volatile File targetFile;


    protected ContentLoader(URL source, boolean lazy){
        super();
        this.source = ContentLoader.SourceUrl(source);
        this.lazy = lazy;
        this.sourcePath = ContentLoader.SourcePath(source);
    }
    protected ContentLoader(String codebase, String path, boolean lazy){
        super();
        this.source = ContentLoader.SourceUrl(codebase,path);
        this.lazy = lazy;
        this.sourcePath = ContentLoader.SourcePath(this.source);
    }
    protected ContentLoader(String url, boolean lazy){
        super();
        this.source = ContentLoader.SourceUrl(url);
        this.lazy = lazy;
        this.sourcePath = ContentLoader.SourcePath(this.source);
    }


    public final URL getSource(){
        return this.source;
    }
    public final String getSourcePath(){
        return this.sourcePath;
    }
    public final boolean isNotLoaded(){
        return this.hasNotCopy();
    }
    public final boolean isLoaded(){
        return this.hasCopy();
    }
    /**
     * Lazily loaded
     */
    public final boolean isLazy(){
        return this.lazy;
    }
    public final File init(){
        File targetFile = this.targetFile;
        if (null == targetFile){

            targetFile = ClassLoader.GetTempFile(this.sourcePath);

            this.targetFile = targetFile;
        }
        return targetFile;
    }
    public final File target(){
        File targetFile = this.targetFile;
        if (null == targetFile){

            targetFile = ClassLoader.GetTempFile(this.sourcePath);

            this.targetFile = targetFile;
        }
        return targetFile;
    }
    public void delete(){
        this.target().delete();
    }
    public final boolean download() throws IOException {
        return this.download(ClassLoader.Current());
    }
    /**
     * @return Read once available
     * @see Clean
     */
    public boolean download(java.lang.ClassLoader loader) throws IOException {
        if (this.hasNotCopy()||this.overwrite())
            return this.overwritein(loader);
        else
            return true;
    }
    public boolean overwritein(java.lang.ClassLoader loader) throws IOException {
        URLConnection connection = this.source.openConnection();
        connection.setRequestProperty("User-Agent","lxl.net.ContentLoader/1.0");
        connection.setRequestProperty("Accept","text/xml, application/*, image/*");
        connection.connect();
        long networkLast = connection.getLastModified();
        int networkLength = connection.getContentLength();
        File target = this.target();
        if (target.isFile()){
            long request = (networkLast / 1000);
            long file = (target.lastModified() / 1000);
            if (request <= file){
                connection.getInputStream().close();
                this.downloaded(loader);
                return true;
            }
        }
        InputStream in = connection.getInputStream();
        try {
            FileOutputStream out = new FileOutputStream(target);
            try {
                byte[] iob = new byte[0x200];
                int count = networkLength;
                int read;
                while (0 < (read = in.read(iob,0,0x200))){
                    out.write(iob,0,read);
                    count -= read;
                }
                target.setLastModified(networkLast);
                this.requested = System.currentTimeMillis();
                this.downloaded(loader);
                return true;
            }
            catch (IOException exc){
                exc.printStackTrace();
                target.delete();
                return false;
            }
            finally {
                out.close();
            }
        }
        catch (IOException exc){
            exc.printStackTrace();
            target.delete();
            return false;
        }
        finally {
            in.close();
        }
    }
    public final InputStream getInputStream() throws IOException {
        File target = this.target();
        return new FileInputStream(target);
    }
    public final OutputStream getOutputStream(boolean overwrite) throws IOException {
        File target = this.target();
        boolean append = (!overwrite);
        return new FileOutputStream(target,append);
    }
    public final FileChannel getChannelRead() throws IOException {
        File target = this.target();
        FileInputStream fin = new FileInputStream(target);
        return fin.getChannel();
    }
    public final FileChannel getChannelReadWrite(boolean overwrite) throws IOException {
        File target = this.target();
        boolean append = (!overwrite);
        FileOutputStream fout = new FileOutputStream(target,append);
        return fout.getChannel();
    }
    public final long getLength() throws IOException {
        File target = this.target();
        return target.length();
    }
    public final boolean canRead() throws IOException {
        File target = this.target();
        return target.canRead();
    }
    public final boolean canWrite() throws IOException {
        File target = this.target();
        return target.canWrite();
    }
    public final ByteBuffer copyTo() throws IOException {
        if (this.hasCopy()){
            File target = this.target();
            long llen = target.length();
            if (Integer.MAX_VALUE < llen)
                throw new IllegalStateException("Buffer overflow.");
            else {
                int len = (int)llen;

                FileChannel readonly = this.getChannelRead();
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(len);

                    int read, remaining = len;
                    while (-1 != (read = readonly.read(buffer))){
                        remaining -= read;
                        if (0 == remaining)
                            break;
                    }
                    buffer.flip();
                    return buffer;
                }
                finally {
                    readonly.close();
                }
            }
        }
        else
            throw new IllegalStateException("Requires download.");
    }
    public final CharBuffer copyTo(Charset cs) throws IOException {

        ByteBuffer bytes = this.copyTo();
        CharsetDecoder coder = cs.newDecoder();
        CharBuffer chars = coder.decode(bytes);
        return chars;
    }
    public final JNLPRandomAccessFile getRandomAccessFile(String mode) throws IOException {
        /* **************************************
         *                                      *
         * [TODO] implement for mapped file     *
         *                                      *
         ****************************************/
        throw new UnsupportedOperationException();
    }
    public final long getMaxLength() throws IOException {
        /* **************************************
         *                                      *
         * [TODO] implement for RAC             *
         ****************************************/
        throw new UnsupportedOperationException();
    }
    public final long setMaxLength(long maxlength) throws IOException {
        /* **************************************
         *                                      *
         * [TODO] implement for RAC             *
         ****************************************/
        throw new UnsupportedOperationException();
    }
    protected final boolean hasCopy(){
        File target = this.target();
        return (target.exists() && target.isFile() && 0L != target.length());
    }
    protected final boolean hasNotCopy(){
        File target = this.target();
        return ((!target.exists())||(!target.isFile())||(0L == target.length()));
    }
    protected boolean overwrite(){
        return false;
    }
    protected void downloaded(java.lang.ClassLoader loader) throws IOException {
    }
    protected void unpackToCache(java.lang.ClassLoader loader, boolean exe) throws IOException {
        File target = this.target();
        long targetLast = target.lastModified();
        ZipFile zip = new ZipFile(target);
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            ZipEntry zent;
            while (entries.hasMoreElements()){
                zent = entries.nextElement();
                if (zent.isDirectory()){
                    File dir = new File(ClassLoader.GetCacheDir(loader),zent.getName());
                    if ((!dir.exists())&&(!dir.mkdirs()))
                        throw new IllegalStateException("Failed to create directory '"+dir.getPath()+"'.");
                }
                else {
                    File file = new File(ClassLoader.GetCacheDir(loader),zent.getName());
                    if (file.exists()){
                        long fileLast = file.lastModified();
                        if (targetLast <= fileLast)
                            continue;
                    }
                    else {
                        File dir = file.getParentFile();
                        if ((!dir.exists())&(!dir.mkdirs()))
                            throw new IllegalStateException("Failed to create directory '"+dir.getPath()+"'.");
                    }
                    InputStream src = zip.getInputStream(zent);
                    try {
                        OutputStream dst = new FileOutputStream(file);
                        try {
                            Copy(src,dst);
                        }
                        catch (IOException exc){
                            file.delete();
                        }
                        finally {
                            dst.close();
                        }
                    }
                    catch (IOException exc){
                        file.delete();
                    }
                    finally {
                        src.close();
                    }
                    file.setLastModified(targetLast);
                    if (exe)
                        SetExecutable(file);

                    ClassLoader.LogFileWrite(file);
                }
            }
        }
        finally {
            zip.close();
        }
    }

    protected static boolean SetExecutable(File file){
        if (!Os.IsWindows){
            String[] cmd = {"chmod","700",file.getAbsolutePath()};
            String[] env = {};
            try {
                Process proc = RT.exec(cmd,RTEnv,ClassLoader.GetCacheDir());
                try {
                    return (0 == proc.waitFor());
                }
                catch (InterruptedException exc){
                    throw new RuntimeException("chmod "+file,exc);
                }
                finally {
                    proc.destroy();
                }
            }
            catch (java.io.IOException exc){
                if (ClassLoader.Test){

                    System.err.println(String.format("%s: unable to chmod file '%s'.",Thread.currentThread().getName(),file.getPath()));

                    if (ClassLoader.Debug)
                        exc.printStackTrace();
                }
            }
        }
        return true;
    }


    private final static Runtime RT = Runtime.getRuntime();
    private final static String[] RTEnv = {};

    public static String SourcePath(URL url){
        String path = url.getPath();
        int idx = ScanPath(path);
        if (-1 == idx)
            return path;
        else {
            switch (path.charAt(idx)){
            case '!':
            case '/':
                return path.substring(idx+1);
            default:
                throw new IllegalStateException(String.valueOf(idx)+'@'+path);
            }
        }
    }
    private static int ScanPath(String p){
        int last = -1;
        char[] cary = p.toCharArray();
        for (int cc = 0, zz = cary.length; cc < zz; cc++){
            switch (cary[cc]){
            case '!':
                return cc;
            case '/':
                last = cc;
            default:
                break;
            }
        }
        return last;
    }

    public static void Copy(InputStream src, OutputStream dst) throws IOException {
        byte[] iob = new byte[0x200];
        int read;
        while (0 < (read = src.read(iob,0,0x200))){
            dst.write(iob,0,read);
        }
        dst.flush();
    }
}

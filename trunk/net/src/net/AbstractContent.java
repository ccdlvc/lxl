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

import java.net.URL;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import javax.jnlp.JNLPRandomAccessFile;

/**
 * Base class for {@link ContentHandlers} content classes.
 * 
 * @see ContentLoader
 */
public class AbstractContent
    extends ContentLoader
    implements ContentHandlers.Content
{

    public final String name, fext;


    public AbstractContent(URL source){
        this(source,true);
    }
    protected AbstractContent(URL source, boolean lazy){
        super(source,lazy);
        this.fext = ContentHandlers.Fext(source);
        this.name = ContentHandlers.Name(source);
    }
    public AbstractContent(String codebase, String path){
        this(codebase,path,true);
    }
    protected AbstractContent(String codebase, String path, boolean lazy){
        super(codebase,path,lazy);
        this.fext = ContentHandlers.Fext(path);
        this.name = ContentHandlers.Name(path);
    }
    public AbstractContent(String url){
        this(url,true);
    }
    protected AbstractContent(String url, boolean lazy){
        super(url,lazy);
        this.fext = ContentHandlers.Fext(url);
        this.name = ContentHandlers.Name(url);
    }


    public final URL getSource(){
        return this.source;
    }
    public final String getSourcePath(){
        return this.sourcePath;
    }
    public final boolean isNotLoaded(){
        return (!this.hasCopy());
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
    /**
     * Filename extension
     */
    public final String getType(){
        return this.fext;
    }
    /*
     * 
     */
    public final String getName() throws IOException {
        return this.name;
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

                    readonly.read(buffer);

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
    public final JNLPRandomAccessFile getRandomAccessFile(String mode) throws IOException {
        /* **************************************
         *                                      *
         * [TODO] implement for                 *
         *         mapped file                  *
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
}

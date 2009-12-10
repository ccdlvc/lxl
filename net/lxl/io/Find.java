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
package lxl.io;


/**
 * List all files, excluding those under a specific directory.  For
 * example, <code>"CVS"</code> or <code>".svn"</code>
 * 
 * @author jdp
 * @since 1.5
 */
public class Find 
    extends java.lang.Object
    implements java.util.Iterator<java.io.File>,
               java.lang.Iterable<java.io.File>
{
    public final static java.io.FileFilter NulFilter = null;

    /**
     * 
     */
    public final static class File 
        extends java.lang.Object
    {
        public final static File[] List(Find parent, java.io.File[] list){
            if (null != list){
                int count = list.length;
                if (0 < count){
                    File[] re = new File[count];
                    for (int cc = 0; cc < count; cc++){
                        re[cc] = new File(parent,list[cc]);
                    }
                    return re;
                }
            }
            return null;
        }


        private final java.io.File file;
        private final Find directory;

        public File(Find parent, java.io.File fd){
            super();
            if (fd.isFile()){
                this.file = fd;
                this.directory = null;
            }
            else if (fd.isDirectory()){
                this.file = null;
                this.directory = new Find(parent,fd);
            }
            else
                throw new java.lang.IllegalArgumentException(fd.getPath());
        }

        public boolean isFile(){
            return (null != this.file);
        }
        public java.io.File getFile(){
            java.io.File file = this.file;
            if (null != file)
                return file;
            else
                throw new java.lang.IllegalStateException();
        }
        public boolean isDirectory(){
            return (null != this.directory);
        }
        public boolean hasNext(){
            Find directory = this.directory;
            if (null != directory)
                return directory.hasNext();
            else
                throw new java.lang.IllegalStateException();
        }
        public java.io.File next(){
            Find directory = this.directory;
            if (null != directory)
                return directory.next();
            else
                throw new java.lang.IllegalStateException();
        }
    }


    private final java.io.File dir;
    private final java.io.FileFilter filter;
    private final File[] list;
    private final int length;
    private int index = 0;

    /**
     * Find files under the directory.
     * @name dir Directory
     * @exception java.lang.IllegalArgumentException Directory not found
     */
    public Find(String dir)
        throws java.lang.IllegalArgumentException
    {
        this(new java.io.File(dir));
    }
    public Find(java.io.File dir)
        throws java.lang.IllegalArgumentException
    {
        this(NulFilter,dir);
    }
    public Find(java.io.FileFilter filter, java.io.File dir)
        throws java.lang.IllegalArgumentException
    {
        super();
        this.dir = dir;
        this.filter = filter;
        //
        if (null != dir && this.dir.isDirectory()){
            if (null != this.filter)
                this.list = File.List(this,this.dir.listFiles(this.filter));
            else
                this.list = File.List(this,this.dir.listFiles());
            
            if (null == list)
                this.length = 0;
            else
                this.length = this.list.length;
        }
        else 
            throw new java.lang.IllegalArgumentException("Directory not found '"+dir+"'.");
    }
    private Find(Find parent, java.io.File dir){
        super();
        if (null == parent)
            throw new IllegalArgumentException();
        else
            this.filter = parent.filter;

        if (null != dir){
            if (dir.isDirectory()){
                this.dir = dir;
                if (null != this.filter)
                    this.list = File.List(this,dir.listFiles(this.filter));
                else
                    this.list = File.List(this,dir.listFiles());

                if (null == list)
                    this.length = 0;
                else
                    this.length = this.list.length;
            }
            else
                throw new java.lang.IllegalArgumentException("Directory not found '"+dir+"'.");
        }
        else
            throw new java.lang.IllegalArgumentException();
    }


    public final File peek(int ahead){
        int index = (this.index + ahead);
        if (-1 < index && index < this.length)
            return this.list[index];
        else
            return null;
    }
    public final boolean hasNext(){
        if (this.index < this.length){
            for (int cc = 0; ; cc++){
                File peek = this.peek(cc);
                if (null == peek)
                    return false;
                else if (peek.isFile())
                    return true;
                else if (peek.hasNext())
                    return true;
            }
        }
        return false;
    }
    public final java.io.File getRoot(){
        return this.dir;
    }
    public final java.io.File next(){
        for (int cc = 0; ; cc++){
            File peek = this.peek(cc);
            if (null == peek)
                throw new java.util.NoSuchElementException();
            else if (peek.isFile()){
                this.index += (cc+1);
                return peek.getFile();
            }
            else if (peek.hasNext()){
                this.index += (cc);
                return peek.next();
            }
        }
    }
    /**
     * @exception java.lang.UnsupportedOperationException Always,
     * this method is not supported here.
     */
    public final void remove()
        throws java.lang.UnsupportedOperationException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public final java.util.Iterator<java.io.File> iterator(){
        return this;
    }

    public final static void usage(java.io.PrintStream out){
        out.println("Usage");
        out.println();
        out.println("  lxl.io.Find  <dir> ");
        out.println();
        out.println("Description");
        out.println();
        out.println("  Test 'find files' on the argument directory.");
        out.println();
    }
    public static void main(String[] argv){

        Find list = new Find(argv[0]);

        while (list.hasNext()){
            java.io.File next = list.next();
            System.out.println(next.getPath());
        }
        System.exit(0);
    }

}

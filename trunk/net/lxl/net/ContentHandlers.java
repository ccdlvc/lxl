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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;

/**
 * Disk cache web data loaders (java.nio).
 * 
 * @see AbstractContent
 * @author jdp
 */
public final class ContentHandlers
    extends java.util.HashMap<String,Constructor<ContentHandlers.Content>>
{
    public final static String Name = "ContentHandlers";


    /**
     * A content file 
     * 
     * @see AbstractContent
     */
    public interface Content
        extends javax.jnlp.FileContents
    {

        public URL getSource();

        public String getSourcePath();

        public boolean isLazy();

        public String getType();
    }



    public ContentHandlers(){
        super();
    }


    public Content getContentFor(URL content){
        String fext = Fext(content);
        Constructor<ContentHandlers.Content> ctor = this.get(fext);
        if (null != ctor){
            try {
                Object[] args = new Object[]{
                    content,
                    Boolean.FALSE
                };
                return ctor.newInstance(args);
            }
            catch (Exception exc){
                if (ClassLoader.Test){
                    System.err.println(String.format("%s: failed to instantiate with ctor '%s' for fext '%s' in error.", Thread.currentThread().getName(),ctor.getName(),fext));
                    if (ClassLoader.Debug)
                        exc.printStackTrace();
                }
            }
        }
        return null;
    }
    public boolean setClassOf(String fext, Class<ContentHandlers.Content> handler){
        Constructor<ContentHandlers.Content> ctor = Ctor(handler);
        if (null != ctor){
            this.put(fext,ctor);
            return true;
        }
        else
            return false;
    }

    public final static String Fext(URL content){
        return Fext(content.getPath());
    }
    public final static String Fext(String path){
        if (null == path || 0 == path.length() || "/".equals(path))
            return null;
        else {
            int idx = path.lastIndexOf('/');
            if (-1 != idx)
                path = path.substring(idx+1);

            idx = path.lastIndexOf('.');
            if (-1 != idx)
                path = path.substring(idx+1);

            return path;
        }
    }
    public final static String Name(URL content){
        return Name(content.getPath());
    }
    public final static String Name(String path){
        if (null == path || 0 == path.length() || "/".equals(path))
            return "";
        else {
            int idx = path.lastIndexOf('/');
            if (-1 != idx)
                path = path.substring(idx+1);

            idx = path.lastIndexOf('.');
            if (-1 != idx)
                path = path.substring(0,idx);

            return path;
        }
    }

    private final static Class<?>[] CtorParams = {
        URL.class,
        boolean.class
    };
    final static Constructor<ContentHandlers.Content> Ctor(Class<ContentHandlers.Content> clas){
        try {
            return clas.getConstructor(CtorParams);
        }
        catch (Exception ignore){
            return null;
        }
    }

}

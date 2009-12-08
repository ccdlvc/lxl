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

import java.net.URL;

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


    /**
     * @return Filename extension
     */
    public final String getType(){
        return this.fext;
    }
    /** 
     * @return File name
     */
    public final String getName(){
        return this.name;
    }
}

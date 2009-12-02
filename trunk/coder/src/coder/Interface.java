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
package lxl.coder;

import java.lang.reflect.TypeVariable;
import java.io.IOException;
import lxl.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 
 * @see Class
 * @author jdp
 */
public final class Interface
    extends Object
{
    public final static Pattern Statement = Pattern.compile("^\\s*implements [\\w\\._]+[;\\s]*");


    private Comment comment;

    public final String typeName;

    public final java.lang.Class typeClass;


    public Interface(Reader reader, Package pkg, List<Import> imports)
        throws IOException, Syntax
    {
        super();
        this.comment = reader.comment();
        String line = reader.getNext(Statement);
        if (null != line){
            StringTokenizer strtok = new StringTokenizer(line," \t\r\n;");
            if (2 == strtok.countTokens()){
                strtok.nextToken();

                this.typeName = strtok.nextToken();

                this.typeClass = Import.Find(pkg,imports,this.typeName);

                if (null == this.typeClass)

                    throw new Syntax("Unrecognized class not found '"+this.typeName+"' in '"+line+"'.");

                else if (!this.typeClass.isInterface())

                    throw new Syntax("Class '"+this.typeClass.getName()+"' is not an interface in '"+line+"'.");
                else
                    return;
            }
            else
                throw new Syntax("Malformed statement '"+line+"'.");
        }
        else
            throw new Jump(this.comment);
    }


    public Object getType(){
        return this.typeName;
    }
    public boolean hasTypeClass(){
        return (null != this.typeClass);
    }
    public java.lang.Class getTypeClass(){
        return this.typeClass;
    }
    public String toString(){
        return this.typeName;
    }
    public boolean hasComment(){
        return (null != this.comment);
    }
    public Comment getComment(){
        return this.comment;
    }
}

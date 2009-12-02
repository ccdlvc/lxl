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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 
 * @see Class
 * @author jdp
 */
public final class Parent
    extends Object
{
    public final static Pattern Statement = Pattern.compile("^\\s*parent [\\w\\.]+[;\\s]*");


    private Comment comment;

    public final String name;

    public Parent(Reader reader)
        throws IOException, Syntax
    {
        super();
        this.comment = reader.comment();
        String line = reader.getNext(Statement);
        if (null != line){
            StringTokenizer strtok = new StringTokenizer(line," \t\r\n;");
            if (2 == strtok.countTokens()){
                strtok.nextToken();
                this.name = strtok.nextToken();
                return;
            }
            else
                throw new Syntax("Malformed ODL parent statement '"+line+"'.");
        }
        else 
            throw new Jump(this.comment);
    }

    public String getName(){
        return this.name;
    }
    public boolean hasComment(){
        return (null != this.comment);
    }
    public Comment getComment(){
        return this.comment;
    }
}

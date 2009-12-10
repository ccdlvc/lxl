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
import lxl.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 
 * @see Class
 * @author jdp
 */
public final class Import
    extends Object
{
    public final static Pattern Statement = Pattern.compile("^import [\\w\\.\\*]+[;\\s]*");


    public final static java.lang.Class Find(Package pkg, List<Import> imports, String type){
        type = Clean(type);
        try {
            return java.lang.Class.forName(type);
        }
        catch (java.lang.ClassNotFoundException exc){
        }
        for (Import importDescriptor : imports){
            java.lang.Class clas = importDescriptor.classFor(type);
            if (null != clas)
                return clas;
        }
        try {
            String classname = pkg.name+'.'+type;
            return java.lang.Class.forName(classname);
        }
        catch (java.lang.ClassNotFoundException exc){
        }
        try {
            String classname = "java.lang."+type;
            return java.lang.Class.forName(classname);
        }
        catch (java.lang.ClassNotFoundException exc){
        }
        return null;
    }


    private Comment comment;

    public final String packageName, packageSpec, className;

    public final boolean classNameInner;


    public Import(Reader reader)
        throws IOException, Syntax
    {
        super();
        this.comment = reader.comment();
        String line = reader.getNext(Statement);
        if (null != line){
            StringTokenizer strtok = new StringTokenizer(line," \t\r\n;");
            if (2 == strtok.countTokens()){
                strtok.nextToken();

                String expr = strtok.nextToken();
                if (expr.endsWith(".*")){
                    this.packageSpec = expr;
                    this.packageName = expr.substring(0,expr.length()-2);
                    this.className = null;
                    this.classNameInner = false;
                }
                else {
                    this.packageName = null;
                    this.packageSpec = null;
                    this.className = expr;
                    this.classNameInner = (0 < expr.indexOf('$'));
                }
                return;
            }
            else
                throw new Syntax("Malformed statement '"+line+"'.");
        }
        else 
            throw new Jump(this.comment);
    }


    public boolean hasClassName(){
        return (null != this.className);
    }
    public String getClassName(){
        return this.className;
    }
    public boolean hasPackageSpec(){
        return (null != this.packageSpec);
    }
    public String getPackageSpec(){
        return this.packageSpec;
    }
    public boolean isPackage(){
        return (null != this.packageName);
    }
    public boolean isClass(){
        return (null != this.className);
    }
    public boolean hasComment(){
        return (null != this.comment);
    }
    public Comment getComment(){
        return this.comment;
    }

    public java.lang.Class classFor(String typeName){
        if (this.isClass()){
            if (this.classNameInner){
                if (this.className.endsWith('$'+typeName))
                    try {
                        return java.lang.Class.forName(this.className);
                    }
                    catch (java.lang.ClassNotFoundException exc){
                        return null;
                    }
                else
                    return null;
            }
            else if (this.className.endsWith('.'+typeName))
                try {
                    return java.lang.Class.forName(this.className);
                }
                catch (java.lang.ClassNotFoundException exc){
                    return null;
                }
            else
                return null;
        }
        else {
            String composure = this.packageName+'.'+typeName;
            try {
                return java.lang.Class.forName(composure);
            }
            catch (java.lang.ClassNotFoundException exc){
                return null;
            }
        }
    }
    final static String Clean(String name){
        int idx = name.indexOf('<');
        if (-1 != idx)
            return name.substring(0,idx);
        else
            return name;
    }
}

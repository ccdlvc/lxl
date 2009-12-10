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
package lxl.jela;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * A JPL source program from the "jela" engine. 
 * 
 * @author J. Pritchard
 */
public abstract class JelaProgram
    extends java.io.FilterWriter
{
    protected final static String[] Imports = {
        "import lxl.*;",
        "import lxl.data.*;",
        "import lxl.service.*;",
        "import lxl.*;"
    };
    protected final static String[] Lines(String jelaExpression){
        if (null != jelaExpression){
            StringTokenizer strtok = new StringTokenizer(jelaExpression,"\r\n");
            int count = strtok.countTokens();
            if (0 != count){
                int cc = 0;
                String[] re = new String[count];
                while (strtok.hasMoreTokens()){
                    re[cc++] = strtok.nextToken();
                }
                return re;
            }
        }
        return null;
    }


    private LineNumbered lined;

    private int indent;


    protected JelaProgram(java.io.Writer writer){
        super(writer);
    }


    public abstract String getPackageName();

    public abstract String getClassName();

    public abstract String getFullClassName();

    public abstract String getSuperClassName();

    public abstract String[] getImports();


    public void writeHead()
        throws IOException
    {
        String packageName = this.getPackageName();
        String className = this.getClassName();
        this.iprintln("package "+packageName+";");
        this.iprintln("");
        String[] imports = this.getImports();
        if (null != imports){
            for (String iexp : imports){
                this.iprintln("import "+iexp+";");
            }
        }
        this.iprintln("public class "+className);
        String superClassName = this.getSuperClassName();
        if (null != superClassName){
            this.iopen();
            this.iprintln("extends "+superClassName);
            this.iclose();
        }
        this.iprintln("{");
        this.iprintln("");
        this.writeConstructor();
    }
    public void writeTail()
        throws IOException
    {
        do {
        }
        while (this.iclose());

        this.iprintln("}");
    }
    public void writeConstructor()
        throws IOException
    {
        this.writeConstructor(NoParams);
    }
    public void writeConstructor(JelaFunction.Parameter... parameters)
        throws IOException
    {

        this.writeMethodOpen(Ctor,parameters);
        
        this.writeMethodBody(true,"super();");

        this.writeMethodClose();
    }
    public void writeMethod(JelaFunction jelaFunction)
        throws IOException
    {
        Class returnType = jelaFunction.getFunctionReturn();
        JelaFunction.Parameter[] parameters = jelaFunction.getFunctionParameters();
        this.writeMethodOpen(returnType,parameters);
        String jelaExpression = jelaFunction.getFunctionBody();
        String[] lines = Lines(jelaExpression);
        this.writeMethodBody((null == returnType),lines);
    }
    protected void writeMethodOpen(Class returnType, JelaFunction.Parameter... parameters)
        throws IOException
    {

        String className = this.getClassName();
        this.iopen();

        if (null == returnType)
            this.iprint("public "+className+"(");
        else
            this.iprint("public "+returnType.getName()+' '+className+"(");

        if (null != parameters){
            boolean sep = false;
            for (JelaFunction.Parameter param: parameters){
                String type = param.getParameterType().getName();
                String name = param.getParameterName();
                if (sep)
                    super.append(", ");
                else
                    sep = true;
                super.append(type);
                super.append(' ');
                super.append(name);
            }
        }
        super.append("){\n");
        this.iopen();
    }
    protected void writeMethodBody(boolean ctor, String body)
        throws IOException
    {
        this.writeMethodBody(ctor,Lines(body));
    }
    protected void writeMethodBody(boolean ctor, String[] lines)
        throws IOException
    {
        boolean re = true;
        for (String stmt : lines){
            if (-1 != stmt.indexOf("return"))
                re = false;
            if (stmt.endsWith("{")){
                this.iprintln(stmt);
                this.iopen();
            }
            else if (stmt.endsWith("}")){
                this.iclose();
                this.iprintln(stmt);
            }
            else
                this.iprintln(stmt);
        }
        if ((!ctor) && re){
            this.iprintln();
            this.iprintln("return null;");
        }
    }
    protected void writeMethodClose()
        throws IOException
    {
        this.iclose();
        this.iprintln("}");
    }

    protected void iopen(){
        this.indent += 1;
    }
    protected boolean iclose(){
        if (0 < this.indent){
            this.indent -= 1;
            return true;
        }
        else
            return false;
    }
    protected void iprint(String string)
        throws IOException
    {
        super.append(I[this.indent % I.length]);
        super.append(string);
    }
    protected void iprintln(String string)
        throws IOException
    {
        this.iprint(string);
        super.append('\n');
    }
    protected void iprintln()
        throws IOException
    {
        super.append('\n');
    }
    protected void println()
        throws IOException
    {
        super.append('\n');
    }

    private final static String[] I = {
        "",
        "    ",
        "        ",
        "            ",
        "                ",
        "                    ",
        "                        ",
    };
    private final static JelaFunction.Parameter[] NoParams = {};
    private final static Class Ctor = null;

}

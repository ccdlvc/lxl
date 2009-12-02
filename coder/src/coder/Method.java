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
 * Methods are employed in a template by name, typically by a template
 * defining a method with particular method signature dependencies.
 * 
 * <h3>Syntax</h3>
 * 
 * <pre>
 * "method" [type] name "(" arguments ")" ["throws" exceptions] "{"
 *    body
 * "}"
 * </pre>
 * 
 * The components "type" and "exceptions" are optional.
 * 
 * Type defaults to void.  
 * 
 * The arguments and exceptions components are in comma delimited JPL
 * syntax.
 * 
 * The arguments and body components may be empty.  Note that an empty
 * body is only valid for type void.
 * 
 * The body is plain JPL syntax.
 * 
 * 
 * @see Class
 * @author jdp
 */
public final class Method
    extends Object
    implements MethodDescriptor.Arguments,
               MethodDescriptor.Exceptions
{
    public final static Pattern Open = Pattern.compile("\\s*method\\s+[\\w\\s]+\\s*",Pattern.MULTILINE);
    public final static Pattern Arguments = Pattern.compile("\\([\\w\\s,\\._]*\\)",Pattern.MULTILINE);
    public final static Pattern Exceptions = Pattern.compile("\\s*throws\\s*[\\w\\s,\\._]*\\s*",Pattern.MULTILINE);
    public final static Pattern Block = Pattern.compile(".*[{}]",Pattern.MULTILINE);

    private Comment comment;

    private Object type;

    private String name, arguments, exceptions;

    private String body;



    public Method(Reader reader)
        throws IOException, Syntax
    {
        super();
        this.comment = reader.comment();
        String open = reader.getNext(Method.Open);
        if (null != open){
            {
                StringTokenizer strtok = new StringTokenizer(open," \r\n\t");
                strtok.nextToken();
                switch (strtok.countTokens()){
                case 1:
                    this.type = "void";
                    this.name = strtok.nextToken();
                    break;
                case 2:
                    this.type = strtok.nextToken();
                    this.name = strtok.nextToken();
                    break;
                default:
                    throw new Syntax("Method format error in '"+open+"'.");
                }
            }
            String arguments = reader.getNext(Method.Arguments);
            if (null != arguments){
                {
                    StringBuilder rewrite = new StringBuilder();
                    StringTokenizer normalize = new StringTokenizer(arguments," \r\n\t,()");
                    normalize.nextToken();
                    int args = 0;
                    while (normalize.hasMoreTokens()){
                        if (0 != rewrite.length() && 0 == (args & 1))
                            rewrite.append(", ");
                        rewrite.append(normalize.nextToken());
                        args += 1;
                    }
                    this.arguments = rewrite.toString();
                }

                String exceptions = reader.getNext(Method.Exceptions);
                if (null != exceptions){

                    StringBuilder rewrite = new StringBuilder();
                    StringTokenizer normalize = new StringTokenizer(exceptions," \r\n\t,");
                    normalize.nextToken();
                    while (normalize.hasMoreTokens()){
                        if (0 != rewrite.length())
                            rewrite.append(", ");
                        rewrite.append(normalize.nextToken());
                    }
                    this.exceptions = rewrite.toString();
                }
                StringBuilder body = new StringBuilder();
                int stack = 0;
                String block;
                scan:
                while (null != (block = reader.getNext(Block))){
                    switch (block.charAt(block.length()-1)){
                    case '{':
                        stack += 1;
                        body.append(block);
                        continue scan;

                    case '}':
                        stack -= 1;
                        body.append(block);
                        if (0 == stack)
                            break scan;
                        else
                            continue scan;
                    default:
                        throw new Syntax("Method definition body format error in '"+open+"'.");
                    }
                }
                if (0 != body.length())
                    this.body = body.toString();
            }
            else
                throw new Syntax("Missing arguments following '"+open+"'.");
        }
        else 
            throw new Jump(this.comment);
    }


    public String getName(){
        return this.name;
    }
    public Object getType(){
        return this.type;
    }
    public boolean hasBody(){
        return (null != this.body);
    }
    public String getBody(){
        return this.body;
    }
    public boolean hasArguments(){
        return (null != this.arguments);
    }
    public String getArguments(){
        return this.arguments;
    }
    public boolean hasExceptions(){
        return (null != this.exceptions);
    }
    public String getExceptions(){
        return this.exceptions;
    }
    public boolean hasComment(){
        return (null != this.comment);
    }
    public Comment getComment(){
        return this.comment;
    }

}

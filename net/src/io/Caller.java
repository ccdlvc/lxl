/*
 * Copyright (C) 1998, 2009  John Pritchard
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
package net.io;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Functions to work on the current thread's method call strack.
 * 
 * @since 1.2
 */
public final class Caller 
    extends Object
{
    /**
     * @return Call trace in stack trace method frames format.
     */
    public final static String[] Trace(){
        StringWriter buffer = new StringWriter();
        PrintWriter err = new PrintWriter(buffer);
        new Exception().printStackTrace(err);

        StringTokenizer lines = new StringTokenizer(buffer.toString(),"\r\n");
        int count = lines.countTokens();
        int pop = 3;
        String[] trace = new String[count-pop];
        for (int cc = 0; cc < count; cc++){
            String line = lines.nextToken();
            if (pop <= cc)
                trace[cc-pop] = line.trim();
        }
        return trace;
    }
    public final static boolean IsIn(java.lang.Object frame){
        return IsIn(frame.getClass());
    }
    public final static boolean IsNotIn(java.lang.Object frame){
        return IsNotIn(frame.getClass());
    }
    public final static boolean IsNotIn(java.lang.Class classframe){
        return (!IsIn(classframe));
    }
    public final static boolean IsIn(java.lang.Class classframe){
        java.lang.String classframename = classframe.getName();

        String[] trace = Trace();
        String line;
        for (int cc = 0, count = trace.length; cc < count; cc++){
            line = trace[cc];

            java.lang.String linename = ClassNameFor(line);
            if (classframename.equals(linename))
                return true;
            else {
                try {
                    java.lang.Class lineclass = Class.forName(linename);
                    if (classframe.isAssignableFrom(lineclass))
                        return true;
                }
                catch (ClassNotFoundException exc){
                }
            }
        }
        return false;
    }
    public final static boolean IsNotIn(java.lang.ClassLoader loader){
        return (!IsIn(loader));
    }
    public final static boolean IsIn(java.lang.ClassLoader loader){

        String[] trace = Trace();
        String line;
        for (int cc = 0, count = trace.length; cc < count; cc++){
            line = trace[cc];

            java.lang.Class lineclass = ClassFor(line);
            if (null != lineclass){
                if (loader == lineclass.getClassLoader())
                    return true;
            }
        }
        return false;
    }
    public final static boolean IsIn(java.lang.reflect.Method mframe){
        java.lang.String framename = mframe.getDeclaringClass().getName()+'.'+mframe.getName();

        String[] trace = Trace();
        String line;
        for (int cc = 0, count = trace.length; cc < count; cc++){
            line = trace[cc];

            java.lang.String linename = FrameNameFor(line);
            if (framename.equals(linename))
                return true;
        }
        return false;
    }

    public static java.lang.Class ClassFor(java.lang.String line){
        java.lang.String classname = ClassNameFor(line);
        if (null != classname){
            try {
                return java.lang.Class.forName(classname);
            }
            catch (java.lang.ClassNotFoundException exc){
            }
        }
        return null;
    }
    public static java.lang.String ClassNameFor(char[] line){
        if (null != line)
            return ClassNameFor(new java.lang.String(line));
        else
            return null;
    }
    public static java.lang.String ClassNameFor(java.lang.String line){
        line = FrameNameFor(line);
        if (null != line){
            int idx = line.lastIndexOf('.');
            if (0 < idx)
                return line.substring(0,idx);
        }
        return null;
    }
    public static java.lang.String FrameNameFor(char[] line){
        if (null != line)
            return FrameNameFor(new java.lang.String(line));
        else
            return null;
    }
    public static java.lang.String FrameNameFor(java.lang.String line){
        int idx = line.lastIndexOf('(');
        if (0 < idx){
            line = line.substring(0,idx);
        }
        idx = line.indexOf("at ");
        if (0 < idx)
            line = line.substring(idx+"at ".length()).trim();
        else
            line = line.trim();
        return line;
    }
}

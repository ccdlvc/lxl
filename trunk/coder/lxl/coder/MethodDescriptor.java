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

/**
 * A defined method must have both type and name.
 * 
 * @author jdp
 */
public interface MethodDescriptor {

    /**
     * Optional comma- space JPL syntax method arguments declaration
     * content expression.
     * 
     * For example,
     * <pre>String arg0, Integer arg1</pre>.
     */
    public interface Arguments 
        extends MethodDescriptor
    {

        public boolean hasArguments();

        public String getArguments();
    }

    /**
     * Optional comma- space JPL syntax exceptions declaration content
     * expression.
     * 
     * For example,
     * <pre>IllegalArgumentException, IllegalStateException</pre>.
     */
    public interface Exceptions 
        extends MethodDescriptor
    {

        public boolean hasExceptions();

        public String getExceptions();
    }



    /**
     * @return Return type.  An object for toString.  Defaults to void
     * when null.
     * @see HasName
     */
    public Object getType();

    /**
     * When name is class-name, it's a constructor.  The return type
     * of the constructor is ignored.
     */
    public String getName();

    public boolean hasBody();

    public String getBody();

}

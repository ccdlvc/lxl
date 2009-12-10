/*
 * Gap Data
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
 * A JPL class or package import expression follows the JPL "import"
 * keyword and does not include the JPL semicolon statement terminal.
 * 
 * The package expression includes a trailing dot- star.  The class
 * expression is a fully qualified class name.
 * 
 * 
 * @author jdp
 */
public interface ImportDescriptor {

    /**
     * @return Not a package spec
     */
    public boolean hasClassName();

    /**
     * @return A fully qualified class name
     */
    public String getClassName();

    /**
     * @return Not a class name
     */
    public boolean hasPackageSpec();

    /**
     * @return Includes trailing ".*"
     */
    public String getPackageSpec();

}

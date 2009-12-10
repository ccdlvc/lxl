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

import lxl.List;

/**
 * 
 * 
 * @author jdp
 */
public interface ClassDescriptor
{

    /**
     * The class relation position as parent or child.  None is
     * generally equivalent to parent in that there's no relation to
     * another class as parent.
     */
    public interface Relation 
        extends ClassDescriptor
    {
        /**
         * The child relation employs the parent key in its identity,
         * while the child group relation employs the parent key in
         * its key.
         */
        public enum Type {
            None, Parent, Child, ChildGroup;
        }

        public boolean hasRelation();

        public Type getRelation();
    }

}

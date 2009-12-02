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
 * An incorrect state of the model.
 * 
 * @author jdp
 */
public class ODStateException 
    extends java.lang.IllegalStateException
{

    public final Object member;


    public ODStateException(Object member, String m){
        super(m);
        this.member = member;
    }
    public ODStateException(Object member, String m, Exception exc){
        super(m,exc);
        this.member = member;
    }
    public ODStateException(String m, ODStateException exc){
        super(m,exc);
        this.member = exc.member;
    }


    /**
     * @return Offending member
     */
    public Object getMember(){
        return this.member;
    }
}

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
package lxl.io;

/**
 * The ODL primitive types from java.
 * 
 * <h3>Special note for Key type</h3>
 * 
 * Instances of the Key type should be complete -- depending on the
 * context -- before being stored.  Stored incomplete keys have a name
 * component and are retrieved for a unique value.
 * 
 * 
 * @author jdp
 */
public enum Primitive {
    String(java.lang.String.class),
    Boolean(java.lang.Boolean.class),
    Byte(java.lang.Byte.class),
    Short(java.lang.Short.class),
    Integer(java.lang.Integer.class), 
    Long(java.lang.Long.class),
    Float(java.lang.Float.class),
    Double(java.lang.Double.class),
    Date(java.util.Date.class);


    public final Class type;

    private Primitive(Class impl){
        this.type = impl;
    }



    public final static boolean Is(String name){
        return (null != Primitive.For(name));
    }
    public final static boolean Is(Class type){
        return (null != Primitive.For(type));
    }
    private final static lxl.Map<String,Primitive> Map = new lxl.Map<String,Primitive>(11);
    static {
        for (Primitive type : Primitive.values()){
            Map.put(type.type.getName(),type);
        }
    }
    public final static Primitive For(Class type){
        if (null != type)
            return Map.get(type.getName());
        else
            return null;
    }
    public final static Primitive For(String name){
        Primitive type = Map.get(name);
        if (null != type)
            return type;
        else {
            try {
                return Primitive.valueOf(name);
            }
            catch (IllegalArgumentException exc){
                return null;
            }
        }
    }
}

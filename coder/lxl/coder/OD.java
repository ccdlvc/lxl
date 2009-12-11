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

import lxl.hapax.TemplateName;
import lxl.hapax.TemplateRenderer;
import lxl.hapax.TemplateDataDictionary;
import lxl.hapax.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lxl.List;
import java.util.StringTokenizer;


/**
 * Object data model for generating JPL source from description.
 * 
 * @author jdp
 */
public final class OD
    extends java.lang.Object
{

    public final static void GenerateBeanSource(TemplateRenderer template,
                                                TemplateDataDictionary top,
                                                PrintWriter out)
        throws ODStateException, IOException, TemplateException
    {
        if (null != template && null != top && null != out)

            template.render(top,out); 
        else
            throw new IllegalArgumentException();
    }

    /**
     * Upcase the first character and return 
     * @see lxl.beans.Reflector#Camel
     */
    public final static String Camel(String string){
        if (null != string){
            int strlen = string.length();
            if (0 != strlen){
                if (1 != strlen)
                    return (string.substring(0,1).toUpperCase()+string.substring(1));
                else
                    return string.toUpperCase();
            }
            else
                throw new IllegalArgumentException();
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * Downcase the first character and return 
     */
    public final static String Decamel(String string){
        if (1 < string.length())
            return (string.substring(0,1).toLowerCase()+string.substring(1));
        else
            return string.toLowerCase();
    }
    public final static String ClassPath(Class cd){
        if (cd.hasPath())
            return cd.getPath();
        else
            throw new ODStateException(cd,"OD Model requires 'path' field of class.");
    }
    public final static String[] ClassImplements(Class cd){
        if (cd.hasInterfaces()){
            List<Object> ili = cd.getInterfaces();
            int count = ili.size();
            if (0 != count){
                String[] re = new String[count];
                for (int cc = 0; cc < count; cc++)
                    re[cc] = ili.get(cc).toString();
                return re;
            }
        }
        return new String[0];
    }
    public final static String ListChildClassName(Field field){
        String typeName = OD.ToString(field.getType());
        String[] parameters = FieldTypeParameters(typeName);
        if (null != parameters && 1 == parameters.length)
            return parameters[0];
        else
            return null;
    }
    /**
     * Parse map type components.  A map type field declaration
     * requires its key component declared from the child class in
     * 'FieldType:fieldName' format, as in
     * 'Map.Short&lt;String:name,TableClass&gt;' (for field type
     * 'String' name 'name' in child 'TableClass').
     */
    public final static class MapChild
        extends Object
    {
        public final static String ClassName(Field field){
            String typeName = OD.ToString(field.getType());
            String[] parameters = FieldTypeParameters(typeName);
            if (null != parameters && 2 == parameters.length)
                return parameters[1];
            else
                return null;
        }
        /**
         * Normalize Type string.
         */
        public final static String Type(String type){
            int idx0 = type.indexOf(':');
            if (-1 != idx0){
                int idx1 = type.indexOf(',',idx0);
                if (-1 != idx1){
                    return (type.substring(0,idx0)+type.substring(idx1));
                }
            }
            return type;
        }
        /**
         * Normalize Type parameter string.
         */
        public final static String TypeKey(String type){
            int idx = type.indexOf(':');
            if (-1 != idx)
                return (type.substring(0,idx));
            else
                return type;
        }

        public final String fieldTypeDeclaration, fieldTypeName;
        public final String mapType;
        public final String keyType;
        public final String childKeyFieldType, childKeyFieldName, childValueClassName;

        public MapChild(Field field)
            throws ODStateException
        {
            super();
            String fieldTypeDeclaration = OD.ToString(field.getType());
            this.fieldTypeDeclaration = fieldTypeDeclaration;
            this.mapType = fieldTypeDeclaration;
            String[] parameters = FieldTypeParameters(fieldTypeDeclaration);
            if (null != parameters && 2 == parameters.length){
                this.childValueClassName = parameters[1];
                String childKeyField = parameters[0];
                int idx = childKeyField.indexOf(':');
                if (-1 != idx){
                    this.childKeyFieldType = childKeyField.substring(0,idx);
                    try {
                        this.keyType = this.childKeyFieldType;
                        this.childKeyFieldName = childKeyField.substring(idx+1);
                        /*
                         */
                        this.fieldTypeName = this.mapType+'<'+this.childKeyFieldType+','+this.childValueClassName+'>';
                    }
                    catch (IllegalArgumentException notPrimitive){
                        throw new ODStateException(field,"Map type parameter key '"+this.childKeyFieldType+"' not primitive.",notPrimitive);
                    }
                }
                else
                    throw new ODStateException(field,"Map type requires key component 'type:fieldName' as in 'Map.Short<String:name,TableClass>'.");
            }
            else
                throw new ODStateException(field,"Map type parameters not found.");
        }
    }
    public final static String[] FieldTypeParameters(String typeName){
        int start = typeName.indexOf('<');
        if (-1 != start){
            String parameters = typeName.substring((start+1),(typeName.length()-1)).trim();
            StringTokenizer strtok = new StringTokenizer(parameters,", ");
            int count = strtok.countTokens();
            String[] list = new String[count];
            for (int cc = 0; cc < count; cc++){
                String token = strtok.nextToken();
                if ('<' != token.charAt(0))
                    list[cc] = token;
                else
                    throw new IllegalStateException(typeName);
            }
            return list;
        }
        return new String[0];
    }
    public final static boolean IsTypeClassList(java.lang.Class fieldType){
        if (null != fieldType)
            return (lxl.List.class.isAssignableFrom(fieldType));
        else
            return false;
    }
    public final static boolean IsTypeClassMap(java.lang.Class fieldType){
        if (null != fieldType)
            return (lxl.Dictionary.class.isAssignableFrom(fieldType));
        else
            return false;
    }
    public final static boolean IsTypeClassString(java.lang.Class fieldType){
        if (null != fieldType)
            return (java.lang.String.class.equals(fieldType));
        else
            return false;
    }
    public final static boolean IsTypeClassDate(java.lang.Class fieldType){
        if (null != fieldType)
            return (java.util.Date.class.isAssignableFrom(fieldType));
        else
            return false;
    }
    public final static boolean IsTypeClassCollection(java.lang.Class fieldType){
        if (null != fieldType)
            return (java.util.Collection.class.isAssignableFrom(fieldType));
        else
            return true;
    }
    public final static boolean IsNotTypeClassCollection(java.lang.Class fieldType){
        if (null != fieldType)
            return (!(java.util.Collection.class.isAssignableFrom(fieldType)));
        else
            return true;
    }
    public final static String CleanTypeName(String name){
        int idx = name.indexOf('<');
        if (-1 != idx)
            return name.substring(0,idx);
        else
            return name;
    }
    public final static String CleanCleanTypeName(String name){
        int idx = name.indexOf('.');
        if (-1 != idx)
            return name.substring(0,idx);
        else
            return name;
    }
    public final static java.lang.Class ClassFor(String cleanTypeName, Import imp){
        if (imp.hasPackageSpec()){
            String packageSpec = imp.getPackageSpec();
            if (packageSpec.endsWith(".*")){
                packageSpec = packageSpec.substring(0,packageSpec.length()-1);
                String packageClassName = packageSpec+cleanTypeName;
                try {
                    return java.lang.Class.forName(packageClassName);
                }
                catch (ClassNotFoundException exc){
                    return null;
                }
            }
            else
                throw new ODStateException(imp,"Import descriptor package spec missing dot-star suffix '"+packageSpec+"'.");
        }
        else if (imp.hasClassName()){
            String packageClassName = imp.getClassName();
            if (packageClassName.endsWith("."+cleanTypeName)){
                try {
                    return java.lang.Class.forName(packageClassName);
                }
                catch (ClassNotFoundException exc){
                    return null;
                }
            }
            else
                return null;
        }
        else
            return null;
    }
    public final static String ToString(Object object){
        if (null == object)
            return null;
        else if (object instanceof String)
            return (String)object;
        else 
            return object.toString();
    }
    public final static String PackageName(Package pkg)
        throws ODStateException
    {
        String packageName = pkg.getName();
        if (null == packageName || 0 == packageName.length())
            throw new ODStateException(pkg,"The object data model requires a package name.");
        else
            return packageName;
    }
    public final static String ClassName(Class cd)
        throws ODStateException
    {
        String className = cd.getName();
        if (null == className || 0 == className.length())
            throw new ODStateException(cd,"The object data model requires a class name.");
        else
            return Camel(className);
    }
}

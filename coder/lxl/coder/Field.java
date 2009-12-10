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
 * Normal field types are either {@link gap.Primitive primitives} or
 * {@link gap.data.Collection collections}.  Transient fields and
 * relation declarations may be {@link gap.data.BigTable data bean}
 * types.
 * 
 * @see Class
 * @author jdp
 */
public final class Field
    extends Object
    implements FieldDescriptor.Persistence,
               FieldDescriptor.Uniqueness,
               FieldDescriptor.Relation
{
    public final static Pattern Statement = Pattern.compile("\\s*[\\w\\.\\*\\-<:>,\\s]+\\s*[\\w]+\\s*[;\\s]*");

    public enum Qualifier {
        Child("*child"),
        Unique("*unique"),
        HashUnique("*hash-unique"),
        Transient("*transient"),
        DefaultSortBy("*sortby");


        public final String syntax;

        private Qualifier(String syntax){
            this.syntax = syntax;
        }


        private final static lxl.Map<String,Qualifier> Syntax = new lxl.Map<String,Qualifier>();
        static {
            for (Qualifier qualifier: Qualifier.values()){
                Syntax.put(qualifier.syntax,qualifier);
            }
        }
        public static Qualifier For(String syntax){
            if (null == syntax || 0 == syntax.length())
                return null;
            else {
                Qualifier qualifier = Syntax.get(syntax);
                if (null != qualifier)
                    return qualifier;
                else {
                    try {
                        return valueOf(syntax);
                    }
                    catch (IllegalArgumentException exc){
                        return null;
                    }
                }
            }
        }
        public static Qualifier[] Add(Qualifier[] list, Qualifier item){
            if (null == item)
                return list;
            else if (null == list)
                return new Qualifier[]{item};
            else {
                int len = list.length;
                Qualifier[] copier = new Qualifier[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }
    }

    private Comment comment;

    public final String typeName, name;

    public final Persistence.Type persistence;

    public final Uniqueness.Type uniqueness;

    public final Relation.Type relational;

    public final boolean isDefaultSortBy;


    public Field(Reader reader)
        throws IOException, Syntax
    {
        super();
        this.comment = reader.comment();
        String line = reader.getNext(Statement);
        if (null != line){
            boolean isDefaultSortBy = false;
            String typeName = null, name = null;
            Persistence.Type persistence = null;
            Uniqueness.Type uniqueness = null;
            Relation.Type relational = null;
            StringTokenizer strtok = new StringTokenizer(line," \t\r\n;");
            while (strtok.hasMoreTokens()){
                String token = strtok.nextToken();
                Qualifier qualifier = Qualifier.For(token);
                if (null != qualifier){
                    switch (qualifier){
                    case Child:
                        if (null != uniqueness)
                            throw new Syntax("Malformed  field statement '"+line+"'.");
                        else {
                            uniqueness = Uniqueness.Type.Undefined;
                            persistence = Persistence.Type.Transient;
                            relational = Relation.Type.Child;
                        }
                        break;
                    case Unique:
                        if (null != uniqueness)
                            throw new Syntax("Malformed  field statement '"+line+"'.");
                        else {
                            uniqueness = Uniqueness.Type.Unique;
                            persistence = Persistence.Type.Persistent;
                            relational = Relation.Type.None;
                        }
                        break;
                    case HashUnique:
                        if (null != uniqueness)
                            throw new Syntax("Malformed  field statement '"+line+"'.");
                        else {
                            uniqueness = Uniqueness.Type.HashUnique;
                            persistence = Persistence.Type.Persistent;
                            relational = Relation.Type.None;
                        }
                        break;
                    case Transient:
                        if (null != uniqueness)
                            throw new Syntax("Malformed  field statement '"+line+"'.");
                        else {
                            uniqueness = Uniqueness.Type.Undefined;
                            persistence = Persistence.Type.Transient;
                            relational = Relation.Type.None;
                        }
                        break;
                    case DefaultSortBy:
                        isDefaultSortBy = true;
                        break;
                    }
                }
                else if (null == typeName)
                    typeName = token;
                else if (null == name)
                    name = token;
                else
                    throw new Syntax("Malformed  field statement '"+line+"'.");
            }
            if (null == typeName)
                throw new Syntax("Malformed  field statement '"+line+"'.");
            else if (null == name)
                name = Class.Decamel(typeName);

            this.typeName = typeName;
            this.name = name;
            this.uniqueness = uniqueness;
            this.persistence = persistence;
            this.relational = relational;
            this.isDefaultSortBy = isDefaultSortBy;
        }
        else
            throw new Jump(this.comment);
    }


    public String getName(){
        return this.name;
    }
    public Object getType(){
        return this.typeName;
    }
    public boolean hasPersistence(){
        return (null != this.persistence);
    }
    public Persistence.Type getPersistence(){
        return this.persistence;
    }
    public boolean hasUniqueness(){
        return (null != this.uniqueness);
    }
    public Uniqueness.Type getUniqueness(){
        return this.uniqueness;
    }
    public boolean hasRelation(){
        return (null != this.relational);
    }
    public Relation.Type getRelation(){
        return this.relational;
    }
    public boolean hasComment(){
        return (null != this.comment);
    }
    public Comment getComment(){
        return this.comment;
    }
    public boolean isDefaultSortBy(){
        return this.isDefaultSortBy;
    }
}

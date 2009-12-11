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
package lxl.hapax;

import lxl.List;
import lxl.beans.Reflector;
import lxl.io.Primitive;

/**
 * 
 */
public class BeanData
    extends AbstractData
    implements lxl.Dictionary<String,Object>
{

    protected Reflector bean;


    public BeanData(Object bean){
        super();
        this.bean = new Reflector(bean);
    }

    protected boolean _hasVariable(TemplateName name){
        return (null != this.bean.getType(name.getName()));
    }
    protected String _getVariable(TemplateName name){
        Class type = this.bean.getType(name.getName());
        if (null != type){
            Object value = this.bean.get(name.getName());
            if (null == value)
                return null;
            else if (value instanceof String)
                return (String)value;
            else
                return value.toString();
        }
        else
            return null;
    }
    protected boolean _hasSection(TemplateName name){
        return (null != this.bean.getType(name.getName()));
    }
    protected List<TemplateDataDictionary> _getSection(TemplateName name){

        Class type = this.bean.getType(name.getName());
        if (null != type){
            Object value = this.bean.get(name.getName());

            if (null == value)
                return null;
            else if (Primitive.Is(value.getClass()))
                return null;
            else {
                BeanData bean = new BeanData(value);
                return this.addBean(name,bean);
            }
        }
        return null;
    }
    public Object get(Object key) {
        return this.bean.get(key);
    }
    public Object put(String key, Object value){
        throw new UnsupportedOperationException();
    }
    public Object remove(Object key){
        throw new UnsupportedOperationException();
    }
    public boolean containsKey(Object key){
        return this.bean.containsKey(key);
    }
    public boolean isEmpty(){
        return this.bean.isEmpty();
    }
    public java.util.Iterator<String> iteratorKeys(){
        return this.bean.iteratorKeys();
    }
    public java.util.Iterator<Object> iteratorValues(){
        return this.bean.iteratorValues();
    }
    public Iterable<String> keys(){
        return this.bean.keys();
    }
    public lxl.Dictionary<String,Object> cloneDictionary(){
        return this.bean.cloneDictionary();
    }
}

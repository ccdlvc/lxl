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

/**
 * 
 */
public class BeanData
    extends AbstractData
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
    protected void _setVariable(TemplateName name, String value){
        throw new UnsupportedOperationException();
    }
    /* ***************************************************************
     * TODO
     * 
     * If the field type is list, transcribe to a list of primitives or beans.
     * 
     * Otherwise transcribe int a list of one primitive or bean 
     * 
     *****************************************************************/
    protected boolean _hasSection(TemplateName name){
        return false;
    }
    protected List<TemplateDataDictionary> _getSection(TemplateName name){
        throw new UnsupportedOperationException();
    }
}

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

/**
 * @author jdp
 */
public class AbstractData
    extends Object
    implements TemplateDataDictionary
{
    protected TemplateDataDictionary parent;
    protected lxl.Map<String,String> variables;
    protected lxl.Map<String,List<TemplateDataDictionary>> sections;


    public AbstractData(){
        super();
    }
    public AbstractData(TemplateDataDictionary parent){
        super();
        this.parent = parent;
    }

    public void renderComplete(){
        this.parent = null;
        lxl.Map<String,String> variables = this.variables;
        if (null != variables)
            variables.clear();
        lxl.Map<String,List<TemplateDataDictionary>> sections = this.sections;
        if (null != sections){
            for (List<TemplateDataDictionary> list: sections.values()){
                for (TemplateDataDictionary item: list){
                    item.renderComplete();
                }
            }
            sections.clear();
        }
    }
    public TemplateDataDictionary clone(){
        try {
            return (TemplateDataDictionary)super.clone();
        }
        catch (java.lang.CloneNotSupportedException exc){
            throw new java.lang.Error(exc);
        }
    }
    public TemplateDataDictionary clone(TemplateDataDictionary parent){
        try {
            AbstractData clone = (AbstractData)super.clone();
            clone.parent = parent;
            return clone;
        }
        catch (java.lang.CloneNotSupportedException exc){
            throw new java.lang.Error(exc);
        }
    }
    public TemplateDataDictionary getParent(){
        return this.parent;
    }
    public final boolean hasVariable(TemplateName name){

        lxl.Map<String,String> variables = this.variables;
        if (null != variables && variables.containsKey(name.getName())){
            return true;
        }
        TemplateDataDictionary parent = this.parent;
        if (null != parent){
            return parent.hasVariable(name);
        }
        return false;
    }
    protected boolean _hasVariable(TemplateName name){
        return false;
    }
    public final String getVariable(TemplateName name){

        lxl.Map<String,String> variables = this.variables;
        if (null != variables){
            String value = variables.get(name.getName());
            if (null != value)
                return value;
        }
        if (this._hasVariable(name))
            return this._getVariable(name);

        TemplateDataDictionary parent = this.parent;
        if (null != parent){
            return parent.getVariable(name);
        }
        return null;
    }
    protected String _getVariable(TemplateName name){
        throw new UnsupportedOperationException();
    }
    public final void setVariable(TemplateName name, String value){

        if (this._hasVariable(name))
            this._setVariable(name,value);
        else {
            lxl.Map<String,String> variables = this.variables;
            if (null == variables){
                variables = new lxl.Map<String,String>();
                this.variables = variables;
            }
            variables.put(name.getName(),value);
        }
    }
    protected void _setVariable(TemplateName name, String value){
        throw new UnsupportedOperationException();
    }
    public List<TemplateDataDictionary> getSection(TemplateName name){

        lxl.Map<String,List<TemplateDataDictionary>> sections = this.sections;
        List<TemplateDataDictionary> section = null;
        if (null != sections){
            section = sections.get(name.getComponent(0));
        }
        if (null == section){
            /*
             * Reflected
             */
            if (this._hasSection(name))
                section = this._getSection(name);

            if (null == section){
                /*
                 * Inherit
                 */
                TemplateDataDictionary parent = this.parent;
                if (null != parent){
                    section = parent.getSection(name);
                    if (null != section){
                        
                        section = SectionClone(this,section);
                        
                        sections.put(name.getComponent(0),section);
                    }
                }
                /*
                 * Synthetic
                 */
                if (null == section){
                    if (this.hasVariable(name))
                        section = this.showSection(name);
                    else
                        return null;
                }
            }
        }
        /*
         * Section name resolution
         */
        if (name.is(0))
            return section;
        else {
            TemplateDataDictionary sectionData = name.dereference(0,section);
            return sectionData.getSection(new TemplateName(name));
        }
    }
    protected boolean _hasSection(TemplateName name){
        return false;
    }
    protected List<TemplateDataDictionary> _getSection(TemplateName name){
        throw new UnsupportedOperationException();
    }

    public List<TemplateDataDictionary> showSection(TemplateName name){

        lxl.Map<String,List<TemplateDataDictionary>> sections = this.sections;
        if (null == sections){
            sections = new lxl.Map<String,List<TemplateDataDictionary>>();
            this.sections = sections;

            TemplateDataDictionary newSection = new AbstractData(this);
            List<TemplateDataDictionary> newSectionList = Add(null,newSection);
            sections.put(name.getComponent(0),newSectionList);
            if (name.is(0))
                return newSectionList;
            else
                return newSection.showSection(new TemplateName(name));
        }
        else {
            List<TemplateDataDictionary> section = sections.get(name.getComponent(0));
            if (null != section){
                if (name.is(0))
                    return section;
                else {
                    TemplateDataDictionary sectionData = name.dereference(0,section);
                    return sectionData.showSection(new TemplateName(name));
                }
            }
            else {
                TemplateDataDictionary newSection = new AbstractData(this);
                section = Add(section,newSection);
                this.sections.put(name.getComponent(0),section);
                if (name.is(0))
                    return section;
                else
                    return newSection.showSection(new TemplateName(name));
            }
        }
    }
    public TemplateDataDictionary addSection(TemplateName name){

        return this.addSection(name, new AbstractData(this));
    }
    public TemplateDataDictionary addSection(TemplateName name, TemplateDataDictionary newSection){

        lxl.Map<String,List<TemplateDataDictionary>> sections = this.sections;
        if (null == sections){
            sections = new lxl.Map<String,List<TemplateDataDictionary>>();
            this.sections = sections;
            List<TemplateDataDictionary> section = Add(null,newSection);
            sections.put(name.getComponent(0),section);
        }
        else {
            List<TemplateDataDictionary> section = sections.get(name.getComponent(0));
            if (null == section){
                section = Add(section,newSection);
                sections.put(name.getComponent(0),section);
            }
            else
                section = Add(section,newSection);
        }
        if (name.is(0))
            return newSection;
        else
            return newSection.addSection(new TemplateName(name));
    }

    public TemplateDataDictionary addBean(String name, Object bean){
        return this.addBean(new TemplateName(name),bean);
    }
    public TemplateDataDictionary addBean(TemplateName tn, Object bean){
        BeanData bd = new BeanData(bean);
        this.addSection(tn,bd);
        return bd;
    }
    public List<TemplateDataDictionary> addBeanList(String name, List copy){
        TemplateName tn = new TemplateName(name);
        List<TemplateDataDictionary> list = this.showSection(tn);
        for (int cc = 0, count = copy.size(); cc < count; cc++){
            Object bean = copy.get(cc);
            list.add(new BeanData(bean));
        }
        return list;
    }

    public final static List<TemplateDataDictionary> Add(List<TemplateDataDictionary> list, TemplateDataDictionary data){
        if (null == list){
            List<TemplateDataDictionary> newSectionList = new lxl.ArrayList<TemplateDataDictionary>();
            newSectionList.add(data);
            return newSectionList;
        }
        else {
            list.add(data);
            return list;
        }
    }
    public final static List<TemplateDataDictionary> SectionClone(TemplateDataDictionary parent, List<TemplateDataDictionary> section){

        List<TemplateDataDictionary> sectionClone = section.cloneList();

        for (int sectionIndex = 0, sectionCount = sectionClone.size(); sectionIndex < sectionCount; sectionIndex++){
            TemplateDataDictionary sectionItem = sectionClone.get(sectionIndex);
            TemplateDataDictionary sectionItemClone = sectionItem.clone(parent);
            sectionClone.update(sectionIndex,sectionItemClone);
        }

        return sectionClone;
    }

}

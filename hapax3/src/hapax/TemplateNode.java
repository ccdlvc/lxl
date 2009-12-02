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

/**
 * 
 */
public final class TemplateNode
    extends Object
{

    private volatile TemplateNodeType nodeType;
    private volatile Integer lineNumber;
    private volatile String nodeContent;
    private volatile Integer offset;
    private volatile Integer offsetCloseRelative;


    public TemplateNode() {
        super();
    }
    public TemplateNode(TemplateNodeType nodeType, Integer lineNumber, String nodeContent) {
        super();
        this.setNodeType(nodeType);
        this.setLineNumber(lineNumber);
        this.setNodeContent(nodeContent);
    }


    public void destroy(){
        this.nodeType = null;
        this.lineNumber = null;
        this.nodeContent = null;
        this.offset = null;
        this.offsetCloseRelative = null;
    }
    public boolean hasNodeType(){
        return (null != this.getNodeType());
    }
    public boolean hasNotNodeType(){
        return (null == this.getNodeType());
    }
    public boolean dropNodeType(){
        if (null != this.nodeType){
            this.nodeType = null;
            return true;
        }
        else
            return false;
    }
    public TemplateNodeType getNodeType(){
        return this.nodeType;
    }
    public void setNodeType(TemplateNodeType nodeType){
        this.nodeType = nodeType;
    }
    public boolean hasLineNumber(){
        return (null != this.getLineNumber());
    }
    public boolean hasNotLineNumber(){
        return (null == this.getLineNumber());
    }
    public boolean dropLineNumber(){
        if (null != this.lineNumber){
            this.lineNumber = null;
            return true;
        }
        else
            return false;
    }
    public Integer getLineNumber(){
        return this.lineNumber;
    }
    public void setLineNumber(Integer lineNumber){
        this.lineNumber = lineNumber;
    }
    public boolean hasNodeContent(){
        return (null != this.getNodeContent());
    }
    public boolean hasNotNodeContent(){
        return (null == this.getNodeContent());
    }
    public boolean dropNodeContent(){
        if (null != this.nodeContent){
            this.nodeContent = null;
            return true;
        }
        else
            return false;
    }
    public String getNodeContent(){
        return this.nodeContent;
    }
    public void setNodeContent(String nodeContent){
        this.nodeContent = nodeContent;
    }
    public boolean hasOffset(){
        return (null != this.getOffset());
    }
    public boolean hasNotOffset(){
        return (null == this.getOffset());
    }
    public boolean dropOffset(){
        if (null != this.offset){
            this.offset = null;
            return true;
        }
        else
            return false;
    }
    public Integer getOffset(){
        return this.offset;
    }
    public void setOffset(Integer offset){
        this.offset = offset;
    }
    public boolean hasOffsetCloseRelative(){
        return (null != this.getOffsetCloseRelative());
    }
    public boolean hasNotOffsetCloseRelative(){
        return (null == this.getOffsetCloseRelative());
    }
    public boolean dropOffsetCloseRelative(){
        if (null != this.offsetCloseRelative){
            this.offsetCloseRelative = null;
            return true;
        }
        else
            return false;
    }
    public Integer getOffsetCloseRelative(){
        return this.offsetCloseRelative;
    }
    public void setOffsetCloseRelative(Integer offsetCloseRelative){
        this.offsetCloseRelative = offsetCloseRelative;
    }

}

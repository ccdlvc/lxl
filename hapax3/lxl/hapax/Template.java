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

import java.net.URL;

import java.nio.CharBuffer;

import java.nio.charset.Charset;

/**
 * 
 */
public class Template
    extends lxl.net.AbstractContent
    implements TemplateLoader
{
    public final static Charset UTF8 = Charset.forName("UTF-8");


    private volatile lxl.List<TemplateNode> target;


    private volatile TemplateRenderer renderer;


    public Template(java.net.URL source){
        super(source);
    }
    public Template(String codebase, String path){
        super(codebase,path);
    }
    public Template(String url){
        super(url);
    }


    public TemplateRenderer getTemplate(TemplateName name)
        throws TemplateException
    {
        try {
            URL src = new URL(this.source,name.toString());
            Template template = new Template(src);
            template.init();
            try {
                template.download();
                return (new TemplateRenderer(this,template));
            }
            catch (java.io.IOException exc){
                throw new TemplateException(src.toString(),exc);
            }
        }
        catch (java.net.MalformedURLException exc){

            try {
                URL src = new URL(name.toString());
                Template template = new Template(src);
                template.init();
                try {
                    template.download();
                    return (new TemplateRenderer(this,template));
                }
                catch (java.io.IOException exc2){
                    throw new TemplateException(src.toString(),exc2);
                }
            }
            catch (java.net.MalformedURLException exc2){

                throw new TemplateException(this.source.toString()+"/+++/"+name.toString(),
                                            exc);
            }
        }
    }
    public TemplateRenderer getTemplateRenderer()
        throws TemplateException
    {
        TemplateRenderer renderer = this.renderer;
        if (null == renderer){
            renderer = (new TemplateRenderer(this,this));
            this.renderer = renderer;
        }
        return renderer;
    }
    public TemplateRenderer createTemplateRenderer()
        throws TemplateException
    {
        return (new TemplateRenderer(this,this));
    }
    public boolean hasName(){
        return true;
    }
    public boolean hasNotName(){
        return false;
    }
    public boolean hasLastModified(){
        return (1L < this.target().lastModified());
    }
    public boolean hasNotLastModified(){
        return (1L > this.target().lastModified());
    }
    public Long getLastModified(){
        return this.target().lastModified();
    }
    public boolean hasTemplateSourceHapax(){
        try {
            return (0 < this.getLength());
        }
        catch (java.io.IOException exc){
            return false;
        }
    }
    public boolean hasNotTemplateSourceHapax(){
        try {
            return (1 > this.getLength());
        }
        catch (java.io.IOException exc){
            return true;
        }
    }
    public CharBuffer getTemplateSourceHapax(){
        try {
            return this.copyTo(UTF8);
        }
        catch (java.io.IOException exc){
            return null;
        }
    }
    public lxl.List<TemplateNode> getTemplateTargetHapax(){
        return this.target;
    }
    public void setTemplateTargetHapax(lxl.List<TemplateNode> list){
        this.target = list;
    }
}

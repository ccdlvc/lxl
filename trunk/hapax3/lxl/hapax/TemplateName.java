/*
 * Hapax3
 * Copyright (c) 2007 Doug Coker
 * Copyright (c) 2009 John Pritchard
 * 
 * The MIT License
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lxl.hapax;

import lxl.Dictionary;
import lxl.List;

import java.util.StringTokenizer;

/**
 * A hierarchical section or variable name parsed into a list of
 * components delimited by solidus '/' (slash).
 * 
 * The user implements path interpretation, as demonstrated in {@link
 * AbstractData}.
 * 
 * A path component accepts an optional index suffix enclosed in
 * square brackets for names identifying lists.  A negative list index
 * is added to list size.  In this way, negative one refers to the
 * last element of the list.  The component dereference method is
 * provided to handle lists.
 * 
 * @see AbstractData
 * @author jdp
 */
public final class TemplateName 
    extends Object
    implements Iterable<TemplateName.Component>
{

    /**
     * A name component has name, and an optional qualifier for use
     * when the name selects a collection.  Generally, an index can
     * dereferece a list, and a name can dereference a map.
     * 
     * <pre><i>name</i> '['</code> <i>index</i> <code>']'</pre>.
     * 
     * A negative list index is added to list size.
     * 
     * @author jdp
     */
    public final static class Component 
        extends Object
        implements Comparable<Component>
    {

        public final String source, term, name;

        public final int index;


        public Component(String source){
            super();
            StringTokenizer strtok = new StringTokenizer(source,"][");
            switch (strtok.countTokens()){
            case 1:
                this.term = strtok.nextToken();
                this.index = 0;
                this.source = source;
                this.name = null;
                break;
            case 2:
                this.term = strtok.nextToken();
                String term = strtok.nextToken(), name;
                int index;
                try {
                    index = Integer.parseInt(term);
                    source = term+'['+index+']';
                    name = null;
                }
                catch (NumberFormatException exc){
                    index = -1;
                    name = term;
                    source = term+'['+name+']';
                }
                this.index = index;
                this.source = source;
                this.name = name;
                break;
            default:
                throw new IllegalArgumentException(source);
            }
        }


        public TemplateDataDictionary dereference(List<TemplateDataDictionary> list){
            int idx = this.index;
            if (-1 < idx)
                return list.get(idx);
            else {
                idx += list.size();
                if (-1 < idx)
                    return list.get(idx);
                else
                    throw new ArrayIndexOutOfBoundsException("In '"+this.source+"' at list size '"+list.size()+"'.");
            }
        }
        public Object dereference(Object object){
            /*
             * Called on result of dereferencing 'term' at root, and
             * then on children in a name component list.
             * 
             * This procedure is fuzzy for this reason, and the
             * general policy of maximized acceptance.
             * 
             * The requirement is not to give a wrong answer for a
             * correct question.
             */
            if (object instanceof Dictionary){

                if (null != this.name){
                    Object test = ((Dictionary)object).get(this.name);
                    if (null != test)
                        return test;
                }
                {
                    Object test = ((Dictionary)object).get(this.term);
                    if (null != test)
                        return test;
                }
            }
            else if (object instanceof List){

                return ((List)object).get(this.index);
            }
            return object;
        }
        public int hashCode(){
            return this.source.hashCode();
        }
        public String toString(){
            return this.source;
        }
        public boolean equals(Object that){
            if (this == that)
                return true;
            else if (null == that)
                return false;
            else
                return this.source.equals(that.toString());
        }
        public int compareTo(Component that){
            if (this == that)
                return 0;
            else if (null == that)
                return 1;
            else if (0 != this.index || 0 != that.index){
                int comp = this.term.compareTo(that.term);
                if (0 != comp)
                    return comp;
                else if (this.index != that.index){
                    if (this.index < that.index)
                        return -1;
                    else
                        return 1;
                }
                else
                    return 0;
            }
            else
                return this.term.compareTo(that.term);
        }
    }
    /**
     * TemplateName path iterator.
     * 
     * @author jdp
     */
    public final class Iterator
        extends Object
        implements java.util.Iterator<Component>
    {
        private final Component[] list;
        private final int count;
        private int index;


        public Iterator(Component[] list){
            super();
            this.list = list;
            this.count = list.length;
        }

        public boolean hasNext(){
            return (this.index < this.count);
        }
        public Component next(){
            int index = this.index;
            if (index < this.count){
                Component next = this.list[index];
                this.index++;
                return next;
            }
            else
                throw new java.util.NoSuchElementException(String.valueOf(index));
        }
        public void remove(){
            throw new java.lang.UnsupportedOperationException();
        }
    }


    public final TemplateName from;

    public final String source;

    public final Component path[];

    public final int count;


    public TemplateName(String base, String name){
        this(Cat(base,name));
    }
    public TemplateName(String source){
        super();
        this.from = null;
        if (null != source){
            StringBuilder strbuf = new StringBuilder();
            StringTokenizer strtok = new StringTokenizer(source,"/");
            int count = strtok.countTokens();
            Component[] path = new Component[count];
            for (int cc = 0; cc < count; cc++){
                Component el = new Component(strtok.nextToken());
                path[cc] = el;
                if (0 != cc)
                    strbuf.append('/');
                strbuf.append(el.source);
            }
            this.source = strbuf.toString();
            this.path = path;
            this.count = count;
        }
        else
            throw new IllegalArgumentException();
    }
    public TemplateName(TemplateName prefix, String suffix){
        this(Cat(prefix,suffix));
    }
    public TemplateName(TemplateName shift){
        super();
        if (null != shift){
            this.from = shift;
            this.source = shift.source;
            int count = (shift.count-1);
            if (0 < count){
                this.count = count;
                Component[] path = new Component[count];
                System.arraycopy(shift.path,1,path,0,count);
                this.path = path;
            }
            else {
                this.count = 0;
                this.path = new Component[0];
            }
        }
        else
            throw new IllegalArgumentException();
    }


    public String getSource(){
        return this.source;
    }
    public boolean isIdentity(){
        return (1 == this.count);
    }
    public int size(){
        return this.count;
    }
    public boolean has(int idx){
        return (-1 < idx && idx < this.count);
    }
    public boolean hasNot(int idx){
        return (idx >= this.count);
    }
    public boolean is(int idx){
        return (idx == (this.count-1));
    }
    public Component get(int idx){
        if (-1 < idx && idx < this.count)
            return this.path[idx];
        else
            return null;
    }
    public String getComponent(int idx){
        if (-1 < idx && idx < this.count)
            return this.path[idx].term;
        else
            return null;
    }
    public TemplateDataDictionary dereference(int componentIndex, List<TemplateDataDictionary> list){
        Component c = this.get(componentIndex);
        if (null != c)
            return c.dereference(list);
        else
            throw new ArrayIndexOutOfBoundsException("In '"+this.source+"' at index '"+componentIndex+"'.");
    }
    public TemplateDataDictionary dereference(List<TemplateDataDictionary> list){
        return this.dereference(0,list);
    }
    /**
     * @return Primitive value, data dictionary, or list of data dictionary.
     */
    public Object dereference(Object value){
        if (null != value){
            for (Component c: this.path){
                value = c.dereference(value);
                if (null == value)
                    return null;
            }
        }
        return value;
    }
    public Object dereferenceVariable(Object value){
        value = this.dereference(value);
        if (value instanceof List)
            return null;
        else
            return value;
    }
    public List<TemplateDataDictionary> dereferenceSection(Object value){
        value = this.dereference(value);
        if (value instanceof List)
            return (List<TemplateDataDictionary>)value;
        else
            return null;
    }
    /**
     * @return Head
     */
    public Component first(){
        return this.get(0);
    }
    /**
     * @return Not first
     */
    public Component last(){
        int idx = (this.count-1);
        if (0 != idx)
            return this.get(idx);
        else
            return null;
    }
    /**
     * @return May be first
     */
    public Component tail(){
        return this.get(this.count-1);
    }
    /**
     * A negative index is added to list size.
     */
    public int getIndex(int idx){
        if (-1 < idx && idx < this.count)
            return this.path[idx].index;
        else
            return -1;
    }
    public boolean hasNotBase(){
        return (2 > this.count);
    }
    public boolean hasBase(){
        return (1 < this.count);
    }
    public String getBase(){
        StringBuilder strbuf = new StringBuilder();
        Component el, path[] = this.path;
        for (int cc = 0, count = path.length, term = (count-1); cc < term; cc++){
            el = path[cc];
            if (0 != strbuf.length())
                strbuf.append('/');
            strbuf.append(el.source);
        }
        return strbuf.toString();
    }
    public boolean hasNotName(){
        return (1 > this.count);
    }
    public boolean hasName(){
        return (0 < this.count);
    }
    public String getName(){
        if (0 < this.count){
            Component[] components = this.path;
            return components[components.length-1].term;
        }
        else
            return "";
    }
    public int hashCode(){
        return this.source.hashCode();
    }
    public String toString(){
        return this.source;
    }
    public boolean equals(Object that){
        if (this == that)
            return true;
        else if (null == that)
            return false;
        else
            return this.source.equals(that.toString());
    }
    public java.util.Iterator<Component> iterator(){
        return new Iterator(this.path);
    }
    public final static String Cat(String base, String name){
        if (null == base || 0 == base.length())
            return name;
        else if (null == name || 0 == name.length())
            return base;
        else if ('/' == base.charAt(base.length()-1) || '/' == name.charAt(0))
            return base+name;
        else
            return base+'/'+name;
    }
    public final static String Cat(TemplateName prefix, String suffix){
        if (null == prefix)
            return suffix;
        else {
            Component tail = prefix.tail();
            if (0 != tail.index){
                StringBuilder strbuf = new StringBuilder();
                if (prefix.hasBase()){
                    strbuf.append(prefix.getBase());
                    strbuf.append('/');
                }
                strbuf.append(tail.term);
                strbuf.append(suffix);
                strbuf.append('[');
                strbuf.append(tail.index);
                strbuf.append(']');
                return strbuf.toString();
            }
            else
                return (prefix+suffix);
        }
    }
}

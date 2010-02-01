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
package lxl;

/**
 * A list of unique elements.  Elements of this list are members of
 * the class Comparable.
 *
 * @author jdp
 */
public class Set<T extends java.lang.Comparable>
    extends ArrayList<T>
{

    protected Index<T> index ;


    /**
     * Default {@link Index} size
     */
    public Set(){
        super();
        this.index = new Index<T>();
    }
    /**
     * @param tablesize {@link Index} size
     */
    public Set(int tablesize){
        super();
        this.index = new Index<T>(tablesize);
    }


    @Override
    public Set clone(){
        Set clone = (Set)super.clone();
        if (null != this.index)
            clone.index = this.index.clone();
        return clone;
    }
    public int indexOf(T item) {
        return this.index.get(item);
    }
    public void insert(T item, int idx) {
        super.insert(item,idx);
        this.index.increment(idx);
    }
    public int add(T item) {
        int idx = this.indexOf(item);
        if (-1 == idx){
            idx = super.add(item);
            this.index.put(item,idx);
        }
        else {
            this.set(idx,item);
        }
        return idx;
    }
    public T set(int idx, T value){
        T previous = super.set(idx,value);
        if (previous != value){
            Index<T> index = this.index;
            index.drop(previous);
            index.put(value,idx);
        }
        return previous;
    }
    protected T removeIn (int idx) {
        T dropped = super.removeIn(idx);
        {
            Index<T> index = this.index;
            index.drop(dropped);
            index.decrement(idx);
        }
        return dropped;
    }
    /**
     * Compatible with java collections API.
     */
    public T remove (Object item) {
        T tee = (T)item;
        Index<T> index = this.index;
        int idx = index.get(tee);
        T dropped;
        if (-1 != idx){
            dropped = super.remove(idx);
            index.drop(dropped);
            index.decrement(idx);
        }
        else
            dropped = null;

        return dropped;
    }
}

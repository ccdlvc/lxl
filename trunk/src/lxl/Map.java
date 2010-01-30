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
 * Example hash array from list and dictionary.  Key must implement
 * Comparable.
 * 
 * @author jdp
 */
public class Map<K extends java.lang.Comparable,V>
    extends ArrayList<V>
    implements Dictionary<K,V>
{

    protected volatile Index<K> index ;


    /**
     * Lazy constructor
     */
    public Map(){
        super();
    }
    /**
     * Optimistic constructor
     */
    public Map(int tablesize){
        super();
        this.index = new Index<K>(tablesize);
    }


    @Override
    public Map clone(){
        Map clone = (Map)super.clone();
        if (null != this.index)
            clone.index = this.index.clone();
        return clone;
    }
    public Dictionary<K,V> cloneDictionary(){
        return this.clone();
    }
    /**
     * Lazy construction or reconstruction
     */
    public final void tableSize(int tablesize){
        if (1 > tablesize)
            this.index = null;
        else if (null == this.index)
            this.index = new Index<K>(tablesize);
        else if (tablesize != this.index.size)
            this.index = new Index<K>(this.index,tablesize);
    }
    /**
     * Lazy default construction
     */
    protected final Index<K> index(){
        if (null == this.index){
            int z = this.getLength();
            if (1 > z)
                z = 7;
            this.index = new Index<K>(z);
        }
        return this.index;
    }
    public V put(K key, V value){
        int idx = super.add(value);
        this.index().put(key,idx);
        return value;
    }
    public int put2(K key, V value){
        int idx = super.add(value);
        this.index().put(key,idx);
        return idx;
    }
    public V get(Object key){
        K ck = (K)key;
        int idx = this.index().get(ck);
        return super.get(idx);
    }
    public V remove(Object key){
        K ck = (K)key;
        int idx = this.index().get(ck);
        if (-1 != idx){
            V value = super.removeIn(idx);
            if (null != this.index){
                this.index = this.index.drop( (K)key);
            }
            return value;
        }
        else
            return null;
    }
    public void clear(){
        if (null != this.index)
            this.index.clear();
        super.clear();
    }
    public boolean containsKey(Object key){
        K ck = (K)key;
        Index idx = this.index();
        return (-1 != idx.get(ck));
    }
    public boolean isEmpty(){
        return (0 == this.getLength());
    }
    public java.util.Iterator<K> iteratorKeys(){
        return (java.util.Iterator<K>)this.index().iterator();
    }
    public java.util.Iterator<V> iteratorValues(){
        return super.iterator();
    }
    public Iterable<K> keys(){
        return this.index().keys();
    }


    public final static void main(String[] test){
        final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Map<Character,Integer> map = new Map<Character,Integer>(20);
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            map.put(key,cc);
        }
        map.index.distribution(false,System.out);
        int failure = 0;
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            Integer idx = map.get(key);
            if (null == idx || -1 == idx){
                failure++;
                System.out.println("Test failed for key '"+key+"'.");
            }
        }
        if (0 == failure)
            System.out.println("Test passed.");
        System.exit(failure);
    }
}

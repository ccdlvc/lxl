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

    protected Index<K> index ;


    /**
     * Default {@link Index} size.
     */
    public Map(){
        super();
        this.index = new Index<K>();
    }
    /**
     * @param tablesize {@link Index} size
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
    public V put(K key, V value){
        Index<K> index = this.index;
        int idx = index.get(key);
        if (-1 == idx){
            idx = super.add(value);
            index.put(key,idx);
        }
        else
            super.set(idx,value);

        return value;
    }
    public int put2(K key, V value){
        Index<K> index = this.index;
        int idx = index.get(key);
        if (-1 == idx){
            idx = super.add(value);
            index.put(key,idx);
        }
        else
            super.set(idx,value);

        return idx;
    }
    public V get(Object key){
        K ck = (K)key;
        int idx = this.index.get(ck);
        return super.get(idx);
    }
    public V remove(Object key){
        K ck = (K)key;
        Index<K> index = this.index;
        int idx = index.get(ck);
        if (-1 != idx){
            V value = super.removeIn(idx);
            index.drop(ck,idx);
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
        Index idx = this.index;
        return (-1 != idx.get(ck));
    }
    public boolean isEmpty(){
        return (0 == this.getLength());
    }
    public java.util.Iterator<K> iteratorKeys(){
        return this.index.iterator();
    }
    public java.util.Iterator<V> iteratorValues(){
        return super.iterator();
    }
    public Iterable<K> keys(){
        return this.index.keys();
    }
    public int add(V item) {
        throw new UnsupportedOperationException();
    }
    public void insert(V item, int index) {
        throw new UnsupportedOperationException();
    }
    public int indexOf(K key) {
        return this.index.get(key);
    }
    public Sequence<V> remove(int index, int count) {
        throw new UnsupportedOperationException();
    }
    public int set(V value){
        throw new UnsupportedOperationException();
    }
    public V replace(V prev, V next){
        throw new UnsupportedOperationException();
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

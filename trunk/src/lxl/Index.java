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

import java.io.PrintStream;

/**
 * Fixed size lookup table.  
 * 
 * The size of the collision table is defined by the constructor.  It
 * is not dynamic and therefore has a simple performance behavior
 * without (user) relatively nondeterministic resizing and rehashing.
 * 
 * Map from Comparable K to int. 
 * <pre>
 *   public int get(K key);
 *
 *   public void put(K key, int index);
 * </pre>
 * 
 * It has been the experience of the author that many if not most
 * applications of an {@link Index} can benefit from this fixed size
 * approach to this data structure.
 * 
 * The user is responsible for the integer values stored within an
 * instance of this class.  The increment and decrement methods are
 * provided for reflecting changes to that data.
 * 
 * @see Map
 * @see Set
 * @author jdp
 */
public class Index<K extends java.lang.Comparable>
    extends Object
    implements java.io.Serializable,
               java.lang.Cloneable,
               java.lang.Iterable
{
    private final static long serialVersionUID = 1;
    /**
     * Collision table entry holds a list element key and index.
     */
    public final static class Entry<K extends java.lang.Comparable>
        extends Object
        implements java.io.Serializable,
                   java.lang.Comparable
    {
        private final static long serialVersionUID = 1;

        protected final K key;
        protected int index;


        public Entry(K key, int index){
            super();
            this.key = key;
            this.index = index;
        }

        public int compareTo(Object key){
            if (key == this.key)
                return 0;
            else if (null == key)
                return 1;
            else {
                int comp = this.key.compareTo(key);
                switch (comp){
                case -1:
                case 0:
                case 1:
                    return comp;
                default:
                    if (0 < comp)
                        return 1;
                    else
                        return -1;
                }
            }
        }
    }
    /**
     * Collision table iterator
     */
    public static class Iterator<K extends java.lang.Comparable>
        extends Object
        implements java.util.Iterator<K>,
                   java.lang.Iterable<K>
    {

        private final Entry<K>[] list;

        private final int length;

        private int index;


        public Iterator(Index<K> index){
            super();
            Entry<K>[][] table = index.table;
            int tableLen = table.length;
            Entry<K> row[];
            /*
             * Copy
             */
            int length = index.count;
            Entry<K>[] list = (Entry<K>[])(new Entry[length]);

            for (int cc = 0, cz = tableLen, lc = 0, rr, rz; cc < cz; cc++){
                row = table[cc];
                if (null != row){
                    for (rr = 0, rz = row.length; rr < rz; rr++, lc++){
                        list[lc] = row[rr];
                    }
                }
            }
            this.list = list;
            this.length = length;
        }

        public boolean hasNext(){
            return (this.index < this.length);
        }
        public K next(){
            if (this.index < this.length)
                return this.list[this.index++].key;
            else
                throw new java.util.NoSuchElementException();
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator iterator(){
            return this;
        }
    }


    /**
     * Collision table size (not a count of table entries)
     * @see #count
     * @see #size()
     */
    public final int size;
    /**
     * Cloneable
     */
    private volatile Entry<K>[][] table;
    /**
     * Number of table entries
     * @see #size()
     */
    private volatile int count;


    public Index(){
        super();
        int size = Primes.Get(5);
        this.table = (Entry<K>[][])(new Entry[size][]);
        this.size = size;
    }
    public Index(int size){
        super();
        size = Primes.Ceil(size);
        this.table = (Entry<K>[][])(new Entry[size][]);
        this.size = size;
    }
    public Index(Index copy, int resize){
        super();
        resize = Primes.Ceil(resize);
        this.table = (Entry<K>[][])(new Entry[resize][]);
        this.size = resize;

        Entry<K> list[], table[][] = copy.table;
        for (int cc = 0, cz = this.size, lc, lz; cc < cz; cc++){
            list = table[cc];
            if (null != list){
                for (lc = 0, lz = list.length; lc < lz; lc++){
                    Entry<K> e = list[lc];
                    this.put(e.key,e.index);
                }
            }
        }
    }


    public void clear(){
        Entry<K> table[][] = this.table;
        for (int cc = 0, len = this.size; cc < len; cc++){
            table[cc] = null;
        }
        this.count = 0;
    }
    public int size(){
        return this.count;
    }
    public Index<K> clone(){
        try {
            Index<K> clone = (Index<K>)super.clone();
            clone.table = this.table.clone();

            Entry<K> list[], table[][] = clone.table;
            for (int cc = 0, len = this.size; cc < len; cc++){
                list = table[cc];
                if (null != list)
                    table[cc] = list.clone();
            }
            return clone;
        }
        catch(CloneNotSupportedException exc){
            throw new InternalError(exc.toString());
        }
    }
    private Index<K> clone(int drop){
        try {
            Index<K> clone = (Index<K>)super.clone();
            clone.table = this.table.clone();

            Entry<K> list[], table[][] = clone.table;
            for (int cc = 0, len = this.size; cc < len; cc++){
                if (drop != cc){
                    list = table[cc];
                    if (null != list)
                        table[cc] = list.clone();
                }
            }
            return clone;
        }
        catch(CloneNotSupportedException exc){
            throw new InternalError(exc.toString());
        }
    }
    public K key(int index){

        Entry<K> ent, list[], table[][] = this.table;
        for (int cc = 0, cz = this.size, bb, bz; cc < cz; cc++){
            list = table[cc];
            if (null != list){
                for (bb = 0, bz = list.length; bb < bz; bb++){
                    ent = list[bb];
                    if (index == ent.index)
                        return ent.key;
                }
            }
        }
        return null;
    }
    /**
     * Drop and decrement
     */
    public Index<K> drop(K key, int idx){
        this.drop(key);
        this.decrement(idx);
        return this;
    }
    public Index<K> drop(K key){
        int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
        Entry<K>[] list = this.table[table];
        if (null == list)
            return this;
        else {
            Entry<K> entry;
            scan:
            for (int cc = 0, len = list.length; cc < len; cc++){
                entry = list[cc];
                switch(entry.compareTo(key)){
                case 0: {
                    Index<K> clone = this.clone(table);
                    if (1 == len)
                        clone.table[table] = null;
                    else {
                        int nlen = (len-1);
                        if (0 == cc){
                            Entry<K>[] copier = (Entry<K>[])(new Entry[nlen]);
                            System.arraycopy(list,1,copier,0,nlen);

                            clone.table[table] = copier;
                        }
                        else if (nlen == cc){
                            Entry<K>[] copier = (Entry<K>[])(new Entry[nlen]);
                            System.arraycopy(list,0,copier,0,nlen);

                            clone.table[table] = copier;
                        }
                        else {
                            Entry<K>[] copier = (Entry<K>[])(new Entry[nlen]);
                            System.arraycopy(list,0,copier,0,cc);
                            System.arraycopy(list,(cc+1),copier,cc,(nlen-cc));

                            clone.table[table] = copier;
                        }
                    }
                    this.count -= 1;
                    return clone;
                }
                case -1:
                    continue scan;
                default:
                    return this;
                }
            }
            return this;
        }
    }
    public int get(K key){
        int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
        Entry<K>[] list = this.table[table];
        if (null == list)
            return -1;
        else {
            Entry<K> entry;
            scan:
            for (int cc = 0, len = list.length; cc < len; cc++){
                entry = list[cc];
                switch(entry.compareTo(key)){
                case 0:
                    return entry.index;
                case -1:
                    continue scan;
                default:
                    return -1;
                }
            }
            return -1;
        }
    }
    public void put(K key, int index){
        if (-1 < index){
            int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
            Entry<K>[] list = this.table[table];
            if (null == list){
                list = (Entry<K>[])(new Entry[]{new Entry<K>(key,index)});
                this.table[table] = list;
                this.count += 1;
            }
            else {
                int len = list.length, term = (len-1);
                Entry<K> entry, copier[];
                for (int cc = 0; cc < len; cc++){
                    entry = list[cc];
                    switch (entry.compareTo(key)){
                    case -1:
                        break;
                    case 0:
                        list[cc] = new Entry<K>(key,index);/*(assume index or key instance change)
                                                            */
                        return;
                    case 1:
                        copier = (Entry<K>[])(new Entry[len+1]);
                        if (0 == cc){
                            copier = (Entry<K>[])(new Entry[len+1]);
                            System.arraycopy(list,0,copier,1,len);
                            copier[0] = new Entry<K>(key,index);
                            this.table[table] = copier;
                            this.count += 1;
                            return;
                        }
                        else {
                            copier = (Entry<K>[])(new Entry[len+1]);
                            System.arraycopy(list,0,copier,0,cc);
                            copier[cc] = new Entry<K>(key,index);
                            System.arraycopy(list,cc,copier,(cc+1),(len-cc));
                            this.table[table] = copier;
                            this.count += 1;
                            return;
                        }
                    default:
                        throw new IllegalStateException("Comparison state value out of range {-1,0,+1}");
                    }
                }
                copier = (Entry<K>[])(new Entry[len+1]);
                System.arraycopy(list,0,copier,0,len);
                copier[len] = new Entry<K>(key,index);
                this.table[table] = copier;
                this.count += 1;
            }
        }
        else
            throw new IllegalArgumentException();
    }
    public void distribution(boolean keys, PrintStream out){
        boolean line;
        for (Entry<K>[] list: this.table){
            line = true;
            for (Entry<K> entry: list){
                line = false;
                if (keys)
                    out.print("["+entry.key+","+entry.index+"]");
                else
                    out.print("["+entry.index+"]");
            }
            if (line)
                out.println("[]");
            else
                out.println();
        } 
    }
    /**
     * Reflect an insertion operation by incrementing indeces equal
     * to, or greater than the argument.
     */
    public void increment(int index){

        for (Entry<K>[] list: this.table){

            for (Entry<K> entry: list){
                if (index >= entry.index)
                    entry.index += 1;
            }
        } 
    }
    /**
     * Reflect a deletion operation by decrementing indeces equal to,
     * or less than the argument.
     */
    public void decrement(int index){

        for (Entry<K>[] list: this.table){

            for (Entry<K> entry: list){
                if (index <= entry.index)
                    entry.index -= 1;
            }
        } 
    }
    @Override
    public Iterator<K> iterator(){
        return new Iterator<K>(this);
    }
    public Iterable<K> keys(){
        return new Iterator<K>(this);
    }

    public final static void main(String[] test){
        final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Index index = new Index(20);
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            index.put(key,cc);
        }
        index.distribution(false,System.err);//(err)
        int failure = 0;
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            int idx = index.get(key);
            if (idx != cc){
                failure++;
                System.out.printf("Test failed (expected %d != result %d) for key: %c\n",cc,idx,key);//(out)
            }
        }
        if (0 == failure){
            System.out.println("Test passed.");//(out)
            System.exit(0);
        }
        else {
            System.out.printf("Test failures: %d\n",failure);//(out)
            System.exit(1);
        }
    }
}

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
 * Fixed size lookup table for {@link java.lang.Comparable} keys.
 * 
 * Adds a map to a list.
 * 
 * Map from Comparable to int.  
 * 
 * <pre>
 *   public int get(Comparable key);
 * 
 *   public void put(Comparable key, int list_index);
 * 
 * </pre>
 * 
 * @author jdp
 */
public class Index 
    extends Object
    implements java.io.Serializable,
               java.lang.Cloneable,
               java.lang.Iterable
{
    private final static long serialVersionUID = 1;
    /**
     * Collision table entry holds a list element key and index.
     */
    public final static class Entry 
        extends Object
        implements java.io.Serializable,
                   java.lang.Comparable
    {
        private final static long serialVersionUID = 1;

        protected final Comparable key;
        protected final int index;


        public Entry(Comparable key, int index){
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
    public static class Iterator
        extends Object
        implements java.util.Iterator,
                   Iterable
    {

        private final Entry[] list;

        private final int length;

        private int index;


        public Iterator(Index index){
            super();
            Entry[][] table = index.table;
            int tableLen = table.length;
            Entry row[];
            /*
             * Count
             */
            int length = 0;
            for (int cc = 0, cz = tableLen, lc, lz; cc < cz; cc++){
                row = table[cc];
                if (null != row){
                    length += row.length;
                }
            }
            /*
             * Copy
             */
            Entry[] list = new Entry[length];

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
        public Object next(){
            if (this.index < this.length)
                return this.list[this.index++];
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


    private volatile Entry[][] table;

    public final int size;


    public Index(int size){
        super();
        size = Primes.Ceil(size);
        this.table = new Entry[size][];
        this.size = size;
    }
    public Index(Index copy, int resize){
        super();
        resize = Primes.Ceil(resize);
        this.table = new Entry[resize][];
        this.size = resize;

        Entry list[], table[][] = copy.table;
        for (int cc = 0, cz = this.size, lc, lz; cc < cz; cc++){
            list = table[cc];
            if (null != list){
                for (lc = 0, lz = list.length; lc < lz; lc++){
                    Entry e = list[lc];
                    this.add(e.key,e.index);
                }
            }
        }
    }


    public void clear(){
        Entry table[][] = this.table;
        for (int cc = 0, len = this.size; cc < len; cc++){
            table[cc] = null;
        }
    }
    public Index clone(){
        try {
            Index clone = (Index)super.clone();
            clone.table = this.table.clone();

            Entry list[], table[][] = clone.table;
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
    private Index clone(int drop){
        try {
            Index clone = (Index)super.clone();
            clone.table = this.table.clone();

            Entry list[], table[][] = clone.table;
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
    public Index drop(Comparable key){
        int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
        Entry[] list = this.table[table];
        if (null == list)
            return this;
        else {
            Entry entry;
            scan:
            for (int cc = 0, len = list.length; cc < len; cc++){
                entry = list[cc];
                switch(entry.compareTo(key)){
                case 0: {
                    Index clone = this.clone(table);
                    if (1 == len)
                        clone.table[table] = null;
                    else {
                        int nlen = (len-1);
                        if (0 == cc){
                            Entry[] copier = new Entry[nlen];
                            System.arraycopy(list,1,copier,0,nlen);

                            clone.table[table] = copier;
                        }
                        else if (nlen == cc){
                            Entry[] copier = new Entry[nlen];
                            System.arraycopy(list,0,copier,0,nlen);

                            clone.table[table] = copier;
                        }
                        else {
                            Entry[] copier = new Entry[nlen];
                            System.arraycopy(list,0,copier,0,cc);
                            System.arraycopy(list,(cc+1),copier,cc,(nlen-cc));

                            clone.table[table] = copier;
                        }
                    }
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
    public int get(Comparable key){
        int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
        Entry[] list = this.table[table];
        if (null == list)
            return -1;
        else {
            Entry entry;
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
    public void add(Comparable key, int index){
        if (-1 < index){
            int table = ((null == key)?(0):(Math.abs(key.hashCode())%this.size));
            Entry[] list = this.table[table];
            if (null == list){
                list = new Entry[]{new Entry(key,index)};
                this.table[table] = list;
            }
            else {
                int len = list.length, term = (len-1), comp;
                Entry entry, copier[];
                for (int cc = 0; cc < len; cc++){
                    entry = list[cc];
                    comp = entry.compareTo(key);
                    if (0 == comp){
                        list[cc] = new Entry(key,index);/*(assume index or key instance change)*/
                        return;
                    }
                    else if (-1 != comp){
                        copier = new Entry[len+1];
                        if (0 == cc){
                            copier = new Entry[len+1];
                            System.arraycopy(list,0,copier,1,len);
                            copier[0] = new Entry(key,index);
                            this.table[table] = copier;
                            return;
                        }
                        else {
                            copier = new Entry[len+1];
                            System.arraycopy(list,0,copier,0,cc);
                            copier[cc] = new Entry(key,index);
                            System.arraycopy(list,cc,copier,(cc+1),(len-cc));
                            this.table[table] = copier;
                            return;
                        }
                    }
                }
                copier = new Entry[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = new Entry(key,index);
                this.table[table] = copier;
            }
        }
        else
            throw new IllegalArgumentException();
    }
    public void distribution(boolean keys, PrintStream out){
        boolean line;
        for (Entry[] list: this.table){
            line = true;
            for (Entry entry: list){
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
    @Override
    public Iterator iterator(){
        return new Iterator(this);
    }
    public Iterable keys(){
        return new Iterator(this);
    }

    public final static void main(String[] test){
        final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Index index = new Index(20);
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            index.add(key,cc);
        }
        index.distribution(false,System.out);
        int failure = 0;
        for (int cc = 0; cc < 62; cc++){
            Character key = new Character(alphabet.charAt(cc));
            int idx = index.get(key);
            if (-1 == idx){
                failure++;
                System.out.println("Test failed for key '"+key+"'.");
            }
        }
        if (0 == failure)
            System.out.println("Test passed.");
        System.exit(failure);
    }
}

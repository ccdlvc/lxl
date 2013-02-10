/*
 * Copyright (c) 2008 VMware, Inc.
 * Copyright (c) 2009 John Pritchard, WTKX Project Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lxl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;



/**
 * Load and store implementation of {@link List}.  This class swaps or
 * copies on all operations including iteration.  
 * 
 * <h3>MT Safety</h3>
 * 
 * The load and store is multi- thread consistent.  However, for
 * expected or desireable results external synchronization will be
 * necessary.
 * 
 *
 * @author gbrown
 * @author jdp
 */
public class ArrayList<T>
    extends Object
    implements List<T>,
               Stack<T>,
               Cloneable
{
    public final static <T> int IndexOf(T[] list, Comparator<T> c, T item) {
        if (null == list)
            return -1;
        else {
            if (null == c) {
                for (int cc = 0, count = list.length; cc < count; cc++){
                    T value = list[cc];
                    if (null != value){
                        if (value.equals(item))
                            return cc;
                    }
                    else if (null == item)
                        return cc;
                }
                return -1;
            }
            else {
                int index = Arrays.binarySearch(list, item, c);
                if (index < 0)
                    return -1;
                else
                    return index;
            }
        }
    }
    @SuppressWarnings("unchecked")
    public final static <T> T[] NewInstance(T[] list, int len){

        return (T[])(new Object[len]);
    }
    @SuppressWarnings("unchecked")
    public final static <T> T[] NewInstance(Sequence<T> list, int len){

        return (T[])(new Object[len]);
    }


    public static class Inverse<T>
        extends Object
        implements Iterable<T>, java.util.Iterator<T>
    {
        private final T[] list;
        private final int length;
        private int next;

        Inverse(T[] list){
            super();
            if (null == list){
                this.next = -1;
                this.list = null;
                this.length = 0;
            }
            else {
                int len = list.length;
                T[] copy = NewInstance(list,len);
                System.arraycopy(list,0,copy,0,len);
                this.list = copy;
                this.length = len;
                this.next = (len-1);
            }
        }

        public boolean hasNext(){
            return (-1 < this.next);
        }
        public T next(){
            if (-1 < this.next)
                return this.list[this.next--];
            else
                throw new NoSuchElementException(String.valueOf(this.next));
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<T> iterator(){
            return this;
        }
    }
    public static class Iterator<T>
        extends Object
        implements Iterable<T>, java.util.Iterator<T>
    {
        private final T[] list;
        private final int length;
        private int next;

        Iterator(T[] list){
            super();
            if (null == list){
                this.list = null;
                this.length = 0;
            }
            else {
                int len = list.length;
                T[] copy = NewInstance(list,len);
                System.arraycopy(list,0,copy,0,len);
                this.list = copy;
                this.length = len;
            }
        }

        public boolean hasNext(){
            return (this.next < this.length);
        }
        public T next(){
            if (this.next < this.length)
                return this.list[this.next++];
            else
                throw new NoSuchElementException(String.valueOf(this.next));
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<T> iterator(){
            return this;
        }
    }


    protected T[] list;

    private Comparator<T> comparator = null;


    public ArrayList() {
        super();
    }
    public ArrayList(ArrayList<T> list, T item) {
        super();
        if (null != list)
            this.comparator = list.comparator;
        if (null != item)
            this.add(item);
    }
    public ArrayList(T[] list) {
        super();
        if (null != list){
            int len = list.length;
            T[] copy = NewInstance(list,len);
            System.arraycopy(list,0,copy,0,len);
            this.list = copy;
        }
    }
    public ArrayList(Sequence<T> sequence) {
        super();
        if (null != sequence){
            int count = sequence.getLength();
            if (0 < count){
                T[] list = NewInstance(sequence,count);
                for (int cc = 0; cc < count; cc++) {
                    T item = sequence.get(cc);
                    list[cc] = item;
                }
                this.list = list;
            }
        }
    }
    public ArrayList(Sequence<T> sequence, int ofs, int count) {
        super();
        if (0 < count){
            T[] list = NewInstance(sequence,count);
            for (int cc = 0; cc < count; cc++) {
                T item = sequence.get(cc+ofs);
                list[cc] = item;
            }
            this.list = list;
        }
    }
    public ArrayList(Comparator<T> comparator) {
        super();

        this.comparator = comparator;
    }
    public ArrayList(Iterable<T> list){
        super();
        if (null != list){
            for (T item : list){
                this.add(item);
            }
        }
    }
    public ArrayList(ArrayList<T> copy){
        super();
        if (null != copy){
            for (T item : copy){
                this.add(item);
            }
        }
    }
    public ArrayList(int initialCapacity) {
        super();
        if (0 < initialCapacity)
            this.list = (T[])(new Object[initialCapacity]);
        else
            throw new IllegalArgumentException(String.valueOf(initialCapacity));
    }


    public List<T> ensure(int index){
        final int size = (index+1);
        if (0 < size){

            Object[] list = this.list;
            if (null == list){

                list = new Object[size];

                this.list = (T[])list;
            }
            else {
                final int list_len = list.length;
                if (size > list_len){

                    Object[] copier = new Object[size];
                    System.arraycopy(list,0,copier,0,list_len);

                    this.list = (T[])copier;
                }
            }
            return this;
        }
        else
            throw new IllegalArgumentException(String.valueOf(index));
    }
    public List<T> clone(){
        try {
            ArrayList<T> clone = (ArrayList<T>)super.clone();
            if (null != this.list)
                clone.list = this.list.clone();
            return clone;
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError("Cloneable");
        }
    }

    public int add(T item) {
        int index = -1;
        T[] list = this.list;
        int len = ((null != list)?(list.length):(0));
        if (comparator == null) {
            index = len;
            if (null == list){
                T[] copy = NewInstance(list,1);
                copy[index] = item;
                this.list = copy;
            }
            else {
                index = len;
                int nlen = (len+1);
                T[] copy = NewInstance(list,nlen);
                System.arraycopy(list,0,copy,0,len);
                copy[index] = item;
                this.list = copy;
            }
            return index;
        }
        else {
            index = Search.binarySearch(this, item, comparator);
            if (0 > index){
                index = -(index + 1);//(insertion point expression)
            }
            this.insert(item,index);
            return index;
        }
    }
    public List<T> addAll(List<T> collection){
        for (T item: collection)
            this.add(item);
        return this;
    }
    public void insert(T item, int index) {
        if (-1 < index){
            T[] list = this.list;
            int len = ((null != list)?(list.length):(0));
            int nlen = Math.max((len+1),(index+1));
            T[] copy = NewInstance(list,nlen);
            if (null != list){
                if (0 == index){
                    System.arraycopy(list,0,copy,1,len);
                }
                else if (len == index){
                    System.arraycopy(list,0,copy,0,len);
                }
                else {
                    System.arraycopy(list,0,copy,0,index);
                    System.arraycopy(list,index,copy,(index+1),(len-index));
                }
            }
            copy[index] = item;
            this.list = copy;
        }
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
    }
    public void push(T item){
        this.insert(item,0);
    }

    public T update(int index, T newValue) {

        T previousValue = this.set(index, newValue);

        return previousValue;
    }
    public Sequence<T> unique(){

        return this.list(1,Integer.MAX_VALUE);
    }
    public T remove (Object item) {
        T[] list = this.list;
        int index = IndexOf(list,this.comparator,(T)item);
        if (0 > index) {
            throw new NoSuchElementException();
        }
        else {
            int len = list.length;
            int term = (len-1);
            T[] copy = NewInstance(list,term);

            if (0 == index){
                System.arraycopy(list,1,copy,0,term);
            }
            else if (term == index){
                System.arraycopy(list,0,copy,0,term);
            }
            else {
                System.arraycopy(list,0,copy,0,index);
                System.arraycopy(list,(index+1),copy,index,(term-index));
            }
            this.list = copy;

            return (T)item;
        }
    }

    protected T removeIn (int index) {
        T[] list = this.list;
        if (0 > index) 
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
        else if (null != list){
            int len = list.length;
            if (index < len){
                T dropped = list[index];
                int term = (len-1);
                T[] copy = NewInstance(list,term);

                if (0 == index){
                    System.arraycopy(list,1,copy,0,term);
                }
                else if (term == index){
                    System.arraycopy(list,0,copy,0,term);
                }
                else {
                    System.arraycopy(list,0,copy,0,index);
                    System.arraycopy(list,(index+1),copy,index,(term-index));
                }
                this.list = copy;
                return dropped;
            }
            else
                throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
        }
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
    }


    public Sequence<T> remove(int index, int count) {

        index = Math.max(index,0);

        count = Math.min(this.getLength(),count);

        ArrayList<T> removed = new ArrayList<T>();

        for (int i = count - 1; i >= 0; i--) {
            removed.insert(this.removeIn(index + i), 0);
        }

        return removed;
    }

    public T removeFirst(){

        return this.removeIn(0);
    }

    public T pop(){
        if (this.isNotEmpty())
            return this.removeFirst();
        else
            return null;
    }

    public Sequence<T> list(int index, int count) {

        index = Math.max(index,0);

        count = Math.min(this.getLength(),count);

        ArrayList<T> re = new ArrayList<T>();

        for (int cc = (count - 1); -1 < cc; cc--) {
            T t = this.get(cc+index);
            if (null != t)
                re.insert(t, 0);
        }

        return re;
    }
    public void clear() {
        this.list = null;
    }
    public T first(){
        T[] list = this.list;
        if (null != list)
            return list[0];
        else
            return null;
    }
    public T last(){
        T[] list = this.list;
        if (null != list)
            return list[list.length-1];
        else
            return null;
    }
    public T get(int index) {
        T[] list = this.list;
        if (null == list)
            return null;
        else if (-1 < index && index < list.length)
            return list[index];
        else
            return null;
    }
    public T peek(){
        return this.get(0);
    }
    public int set(T value){
        int idx = this.indexOf(value);
        if (-1 < idx)
            return idx;
        else
            return this.add(value);
    }
    public T set(int index, T value){
        T[] list = this.list;
        int len = ((null != list)?(list.length):(0));
        if (-1 < index && index < len){
            T previousValue = list[index];
            list[index] = value;
            return previousValue;
        }
        else
            throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
    }
    public T poke(T item){
        return this.set(0,item);
    }
    public T replace(T prev, T next){
        int idx = this.indexOf(prev);
        if (-1 < idx){
            this.set(idx,next);
            return next;
        }
        else if (null == prev){
            this.add(next);
            return next;
        }
        else
            throw new IllegalArgumentException("Previous value not found.");
    }
    public boolean contains(T item){
        return (-1 != this.indexOf(item));
    }
    public int indexOf(T item) {
        return IndexOf(this.list,this.comparator,item);
    }
    public int getLength() {
        T[] list = this.list;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public int size() {
        T[] list = this.list;
        if (null == list)
            return 0;
        else
            return list.length;
    }
    public boolean isEmpty() {
        T[] list = this.list;
        if (null == list)
            return true;
        else
            return (0 == list.length);
    }
    public boolean isNotEmpty() {
        T[] list = this.list;
        if (null == list)
            return false;
        else
            return (0 != list.length);
    }
    public Object[] toArray() {
        return this.toArray(null);
    }
    public Object[] toArraySorted() {
        return this.toArraySorted(null);
    }
    /**
     * Safe array is copied defensively.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(Class component) {
        T[] list = this.list;
        if (null == list)
            return null;
        else if (null != component){
            int len = list.length;
            T[] copy = (T[])Array.newInstance(component,len);
            System.arraycopy(list,0,copy,0,len);
            return copy;
        }
        else {
            int len = list.length;
            T[] copy = NewInstance(list,len);
            System.arraycopy(list,0,copy,0,len);
            return copy;
        }
    }
    public T[] toArraySorted(Class component){
        T[] list = this.toArray(component);
        java.util.Arrays.sort(list);
        return list;
    }
    /**
     * Unsafe array is the internal list.
     */
    public T[] array(){
        return this.list;
    }
    public Comparator<T> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        Comparator<T> previousComparator = this.comparator;

        if (previousComparator != comparator) {
            if (comparator != null) {
                T[] list = this.list;
                if (null != list){
                    Arrays.sort(list,0,list.length,comparator);
                }
            }

            this.comparator = comparator;
        }
    }

    public java.util.Iterator<T> iterator() {

        return new Iterator(this.list);
    }
    public Iterable<T> values() {

        return new Iterator(this.list);
    }

    public Inverse<T> inverse() {

        return new Inverse(this.list);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int i = 0, n = getLength(); i < n; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(get(i));
        }

        sb.append("]");

        return sb.toString();
    }
    public final int hashCode(){
        int h = 0;
        for (T item : this){
            if (null != item){
                h += item.hashCode();
            }
        }
        return h;
    }
    public final boolean equals(Object that){
        if (this == that)
            return true;
        else if (that instanceof Collection)
            return this.equals( (Collection)that);
        else
            return false;
    }
    public final boolean equals(Collection that){
        final int len = this.size();
        if (len == that.size()){
            final Object[] thisL = this.toArraySorted();
            final Object[] thatL = that.toArraySorted();
            for (int ix = 0; ix < len; ix++){
                Object thisI = thisL[ix];
                Object thatI = thatL[ix];
                if (null != thisI){
                    if (null != thatI){
                        if (!thisI.equals(thatI))
                            return false;
                    }
                    else
                        return false;
                }
                else if (null != thatI)
                    return false;
            }
            return true;
        }
        else
            return false;
    }
    public final int compareTo(Collection that){
        if (this == that)
            return 0;
        else {
            int thisZ = this.size();
            int thatZ = that.size();
            if (thisZ == thatZ){
                int thisH = this.hashCode();
                int thatH = that.hashCode();
                if (thisH == thatH)
                    return 0;
                else if (thisH < thatH)
                    return -1;
                else
                    return 1;
            }
            else if (thisZ < thatZ)
                return 1;
            else
                return -1;
        }
    }

    public static void main(String[] argv){
        try {
            ArrayList list = new ArrayList();
            list.ensure(1);
            list.set(1,new Object());
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}

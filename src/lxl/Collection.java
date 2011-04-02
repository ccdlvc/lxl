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

import java.util.Comparator;

/**
 * Root interface in collection hierarchy. Defines operations common to all
 *
 * @author gbrown
 * @author jdp
 */
public interface Collection<T>
    extends Cloneable, Iterable<T>, Comparable<Collection<T>>
{

    /**
     * Removes all elements from the collection.
     */
    public void clear();

    public int size();

    public int add(T item);

    public boolean contains(T item);

    /**
     * Returns the collection's sort order.
     *
     * @return
     * The comparator used to order elements in the collection, or <tt>null</tt>
     * if the sort order is undefined.
     *
     * @see #setComparator(Comparator)
     */
    public Comparator<T> getComparator();

    /**
     * Sets the collection's sort order, re-ordering the collection's contents
     * and ensuring that new entries preserve the sort order.
     *
     * @param comparator
     * The comparator used to order elements in the collection, or null if the
     * collection is unsorted.
     */
    public void setComparator(Comparator<T> comparator);

    public Object[] toArray();
    public Object[] toArraySorted();

    public T[] toArray(Class component);
    public T[] toArraySorted(Class component);
}

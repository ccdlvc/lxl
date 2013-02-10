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
 * Collection interface representing an ordered sequence of items.
 *
 * @author gbrown
 * @author jdp
 */
public interface List<T>
    extends Sequence<T>, Collection<T>
{
    /**
     * Ensure that a following "set(index,value)" will succeed.
     * 
     * @param index List index
     */
    public List<T> ensure(int index);

    /**
     * Adds an item to the list. If the list is unsorted, the item is appended
     * to the end of the list. Otherwise, it is inserted at the appropriate
     * index.
     *
     * @see wtkx.co.ListListener#itemInserted(List, int)
     *
     * @return
     * The index at which the item was added.
     */
    public int add(T item);

    public List<T> addAll(List<T> collection);

    public T get(int index);

    public T set(int index, T value);

    /**
     * Inserts an item into the list.
     *
     * @param item
     * The item to be added to the list.
     *
     * @param index
     * The index at which the item should be inserted. Must be a value between
     * <tt>0</tt> and <tt>getLength()</tt>.
     *
     * @throws IllegalArgumentException
     * If the list is sorted and the insertion point of the item does not match
     * the given index.
     *
     * @see ListListener#itemInserted(List, int)
     */
    public void insert(T item, int index);

    /**
     * Updates (or defines) the item at the given index.
     *
     * @param index
     * The index of the item to update.
     *
     * @param item
     * The item that will replace any existing value at the given index.
     *
     * @throws IllegalArgumentException
     * If the list is sorted and the index of the updated item would be
     * different than its current index.
     *
     * @see ListListener#itemUpdated(List, int, Object)
     */
    public T update(int index, T item);

    /**
     * @see ListListener#itemsRemoved(List, int, Sequence)
     */
    public Sequence<T> remove(int index, int count);

    /**
     * @param t Untyped T following java collections compatibility
     * @return The argument 
     */
    public T remove(Object t);

    public T removeFirst();

    /**
     * @see ListListener#itemsRemoved(List, int, Sequence)
     */
    public void clear();

    /**
     * Returns the length of the list.
     *
     * @return The number of items in the list, or -1 if the list's length is
     * not known. In this case, the iterator must be used to retrieve the
     * contents of the list.
     */
    public int getLength();

    public int size();

    /**
     * @see ListListener#comparatorChanged(List, Comparator)
     */
    public void setComparator(Comparator<T> comparator);

    public Iterable<T> values();

    public List<T> clone();

    public boolean isEmpty();

    public boolean isNotEmpty();
    /**
     * Unsafe array is the internal list.
     */
    public Object[] array();
}

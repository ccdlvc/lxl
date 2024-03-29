/*
 * Copyright (c) 2008 VMware, Inc.
 * Copyright (c) 2009 John Pritchard, JBXML Project Group
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

/**
 * Interface representing a set of key/value pairs.
 *
 * @author gbrown
 * @author jdp
 */
public interface Dictionary<K extends Comparable, V>
    extends Cloneable
{

    /**
     * Retrieves the value for the given key.
     *
     * @param key
     * The key whose value is to be returned.
     *
     * @return
     * The value corresponding to <tt>key</tt>, or null if the key does not
     * exist. Will also return null if the key refers to a null value.
     * Use <tt>containsKey()</tt> to distinguish between these two cases.
     */
    public V get(Object key);

    /**
     * Sets the value of the given key, creating a new entry or replacing the
     * existing value.
     *
     * @param key
     * The key whose value is to be set.
     *
     * @param value
     * The value to be associated with the given key.
     */
    public V put(K key, V value);

    /**
     * Removes a key/value pair from the map.  This method should
     * throw an instance of {@link PropertyNotFoundException} if it is
     * unable to perform the requested operation due to an
     * unrecognized name.
     *
     * @param key
     * The key whose mapping is to be removed.
     *
     * @return
     * The value that was removed.
     */
    public V remove(Object key);

    /**
     * Tests the existence of a key in the dictionary.
     *
     * @param key
     * The key whose presence in the dictionary is to be tested.
     *
     * @return
     * <tt>true</tt> if the key exists in the dictionary; <tt>false</tt>,
     * otherwise.
     */
    public boolean containsKey(Object key);

    /**
     * Tests the emptiness of the dictionary.
     *
     * @return
     * <tt>true</tt> if the dictionary contains no keys; <tt>false</tt>,
     * otherwise.
     */
    public boolean isEmpty();

    public java.util.Iterator<K> iteratorKeys();

    public Set<K> keySet();

    public java.util.Iterator<V> iteratorValues();

    public Iterable<K> keys();

    public Dictionary<K,V> cloneDictionary();
}

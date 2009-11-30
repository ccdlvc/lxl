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
 * Collection interface representing a group of unique elements.
 *
 * @author gbrown
 */
public interface Set<E>
    extends Collection<E>
{

    /**
     * @see SetListener#elementAdded(Set, Object)
     */
    public void add(E element);

    /**
     * @see SetListener#elementRemoved(Set, Object)
     */
    public void remove(E element);

    /**
     * @see SetListener#setCleared(Set)
     */
    public void clear();

    /**
     * @see SetListener#setCleared(Set)
     */
    public void setComparator(Comparator<E> comparator);

}

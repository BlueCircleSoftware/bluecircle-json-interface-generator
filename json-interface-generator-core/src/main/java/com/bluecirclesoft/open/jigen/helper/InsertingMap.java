/*
 * Copyright 2015 Blue Circle Software, LLC
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

package com.bluecirclesoft.open.jigen.helper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * InsertingMap is a wrapper around a map. It adds a new method, elem(), which is like get(), but ensures a new value is created if it's
 * missing in the map.
 */
public class InsertingMap<K, V> implements Map<K, V> {

	final private Map<K, V> base;

	final private Function<K, V> newElementProducer;

	public InsertingMap(Map<K, V> base, Function<K, V> newElementProducer) {
		this.base = base;
		this.newElementProducer = newElementProducer;
	}

	/**
	 * Returns the number of key-value mappings in this map.  If the
	 * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of key-value mappings in this map
	 */
	@Override
	public int size() {
		return base.size();
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.  More formally, returns <tt>true</tt> if and only if
	 * this map contains a mapping for a key <tt>k</tt> such that
	 * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
	 * at most one such mapping.)
	 *
	 * @param key key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 * key
	 * @throws ClassCastException   if the key is of an inappropriate type for
	 *                              this map
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified key is null and this map
	 *                              does not permit null keys
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public boolean containsKey(Object key) {
		return base.containsKey(key);
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the
	 * specified value.  More formally, returns <tt>true</tt> if and only if
	 * this map contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
	 * will probably require time linear in the map size for most
	 * implementations of the <tt>Map</tt> interface.
	 *
	 * @param value value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 * specified value
	 * @throws ClassCastException   if the value is of an inappropriate type for
	 *                              this map
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified value is null and this
	 *                              map does not permit null values
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public boolean containsValue(Object value) {
		return base.containsValue(value);
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 * <p>
	 * <p>More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise
	 * it returns {@code null}.  (There can be at most one such mapping.)
	 * <p>
	 * <p>If this map permits null values, then a return value of
	 * {@code null} does not <i>necessarily</i> indicate that the map
	 * contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}.  The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or
	 * {@code null} if this map contains no mapping for the key
	 * @throws ClassCastException   if the key is of an inappropriate type for
	 *                              this map
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified key is null and this map
	 *                              does not permit null keys
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public V get(Object key) {
		return base.get(key);
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation).  If the map previously contained a mapping for
	 * the key, the old value is replaced by the specified value.  (A map
	 * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
	 * if {@link #containsKey(Object) m.containsKey(k)} would return
	 * <tt>true</tt>.)
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or
	 * <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 * (A <tt>null</tt> return can also indicate that the map
	 * previously associated <tt>null</tt> with <tt>key</tt>,
	 * if the implementation supports <tt>null</tt> values.)
	 * @throws UnsupportedOperationException if the <tt>put</tt> operation
	 *                                       is not supported by this map
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 * @throws NullPointerException          if the specified key or value is null
	 *                                       and this map does not permit null keys or values
	 * @throws IllegalArgumentException      if some property of the specified key
	 *                                       or value prevents it from being stored in this map
	 */
	@Override
	public V put(K key, V value) {
		return base.put(key, value);
	}

	/**
	 * Removes the mapping for a key from this map if it is present
	 * (optional operation).   More formally, if this map contains a mapping
	 * from key <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
	 * is removed.  (The map can contain at most one such mapping.)
	 * <p>
	 * <p>Returns the value to which this map previously associated the key,
	 * or <tt>null</tt> if the map contained no mapping for the key.
	 * <p>
	 * <p>If this map permits null values, then a return value of
	 * <tt>null</tt> does not <i>necessarily</i> indicate that the map
	 * contained no mapping for the key; it's also possible that the map
	 * explicitly mapped the key to <tt>null</tt>.
	 * <p>
	 * <p>The map will not contain a mapping for the specified key once the
	 * call returns.
	 *
	 * @param key key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or
	 * <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation
	 *                                       is not supported by this map
	 * @throws ClassCastException            if the key is of an inappropriate type for
	 *                                       this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified key is null and this
	 *                                       map does not permit null keys
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public V remove(Object key) {
		return base.remove(key);
	}

	/**
	 * Copies all of the mappings from the specified map to this map
	 * (optional operation).  The effect of this call is equivalent to that
	 * of calling {@link #put(Object, Object) put(k, v)} on this map once
	 * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
	 * specified map.  The behavior of this operation is undefined if the
	 * specified map is modified while the operation is in progress.
	 *
	 * @param m mappings to be stored in this map
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> operation
	 *                                       is not supported by this map
	 * @throws ClassCastException            if the class of a key or value in the
	 *                                       specified map prevents it from being stored in this map
	 * @throws NullPointerException          if the specified map is null, or if
	 *                                       this map does not permit null keys or values, and the
	 *                                       specified map contains null keys or values
	 * @throws IllegalArgumentException      if some property of a key or value in
	 *                                       the specified map prevents it from being stored in this map
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		base.putAll(m);
	}

	/**
	 * Removes all of the mappings from this map (optional operation).
	 * The map will be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException if the <tt>clear</tt> operation
	 *                                       is not supported by this map
	 */
	@Override
	public void clear() {
		base.clear();
	}

	/**
	 * Returns a {@link Set} view of the keys contained in this map.
	 * The set is backed by the map, so changes to the map are
	 * reflected in the set, and vice-versa.  If the map is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation), the results of
	 * the iteration are undefined.  The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	@Override
	public Set<K> keySet() {
		return base.keySet();
	}

	/**
	 * Returns a {@link Collection} view of the values contained in this map.
	 * The collection is backed by the map, so changes to the map are
	 * reflected in the collection, and vice-versa.  If the map is
	 * modified while an iteration over the collection is in progress
	 * (except through the iterator's own <tt>remove</tt> operation),
	 * the results of the iteration are undefined.  The collection
	 * supports element removal, which removes the corresponding
	 * mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
	 * support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a collection view of the values contained in this map
	 */
	@Override
	public Collection<V> values() {
		return base.values();
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map.
	 * The set is backed by the map, so changes to the map are
	 * reflected in the set, and vice-versa.  If the map is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation, or through the
	 * <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined.  The set
	 * supports element removal, which removes the corresponding
	 * mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
	 * <tt>clear</tt> operations.  It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return base.entrySet();
	}

	/**
	 * Compares the specified object with this map for equality.  Returns
	 * <tt>true</tt> if the given object is also a map and the two maps
	 * represent the same mappings.  More formally, two maps <tt>m1</tt> and
	 * <tt>m2</tt> represent the same mappings if
	 * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
	 * <tt>equals</tt> method works properly across different implementations
	 * of the <tt>Map</tt> interface.
	 *
	 * @param o object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	@Override
	public boolean equals(Object o) {
		return base.equals(o);
	}

	/**
	 * Returns the hash code value for this map.  The hash code of a map is
	 * defined to be the sum of the hash codes of each entry in the map's
	 * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
	 * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
	 * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 *
	 * @return the hash code value for this map
	 * @see Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		return base.hashCode();
	}

	/**
	 * Returns the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key.
	 *
	 * @param key          the key whose associated value is to be returned
	 * @param defaultValue the default mapping of the key
	 * @return the value to which the specified key is mapped, or
	 * {@code defaultValue} if this map contains no mapping for the key
	 * @throws ClassCastException   if the key is of an inappropriate type for
	 *                              this map
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified key is null and this map
	 *                              does not permit null keys
	 *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                              or atomicity properties of this method. Any implementation providing
	 *                              atomicity guarantees must override this method and document its
	 *                              concurrency properties.
	 * @since 1.8
	 */
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return base.getOrDefault(key, defaultValue);
	}

	/**
	 * Performs the given action for each entry in this map until all entries
	 * have been processed or the action throws an exception.   Unless
	 * otherwise specified by the implementing class, actions are performed in
	 * the order of entry set iteration (if an iteration order is specified.)
	 * Exceptions thrown by the action are relayed to the caller.
	 *
	 * @param action The action to be performed for each entry
	 * @throws NullPointerException if the specified action is null
	 *                              <pre> {@code
	 *                              for (Map.Entry<K, V> entry : map.entrySet())
	 *                                  action.accept(entry.getKey(), entry.getValue());
	 *                              }</pre>
	 *                              <p>
	 *                              The default implementation makes no guarantees about synchronization
	 *                              or atomicity properties of this method. Any implementation providing
	 *                              atomicity guarantees must override this method and document its
	 *                              concurrency properties.
	 * @since 1.8
	 */
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		base.forEach(action);
	}

	/**
	 * Replaces each entry's value with the result of invoking the given
	 * function on that entry until all entries have been processed or the
	 * function throws an exception.  Exceptions thrown by the function are
	 * relayed to the caller.
	 *
	 * @param function the function to apply to each entry
	 * @throws UnsupportedOperationException if the {@code set} operation
	 *                                       is not supported by this map's entry set iterator.
	 * @throws ClassCastException            if the class of a replacement value
	 *                                       prevents it from being stored in this map
	 * @throws NullPointerException          if the specified function is null, or the
	 *                                       specified replacement value is null, and this map does not permit null
	 *                                       values
	 * @throws ClassCastException            if a replacement value is of an inappropriate
	 *                                       type for this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if function or a replacement value is null,
	 *                                       and this map does not permit null keys or values
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws IllegalArgumentException      if some property of a replacement value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       <pre> {@code
	 *                                       for (Map.Entry<K, V> entry : map.entrySet())
	 *                                           entry.setValue(function.apply(entry.getKey(), entry.getValue()));
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties.
	 * @since 1.8
	 */
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		base.replaceAll(function);
	}

	/**
	 * If the specified key is not already associated with a value (or is mapped
	 * to {@code null}) associates it with the given value and returns
	 * {@code null}, else returns the current value.
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with the specified key, or
	 * {@code null} if there was no mapping for the key.
	 * (A {@code null} return can also indicate that the map
	 * previously associated {@code null} with the key,
	 * if the implementation supports null values.)
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the key or value is of an inappropriate
	 *                                       type for this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified key or value is null,
	 *                                       and this map does not permit null keys or values
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws IllegalArgumentException      if some property of the specified key
	 *                                       or value prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       map}:
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       V v = map.get(key);
	 *                                       if (v == null)
	 *                                           v = map.put(key, value);
	 *
	 *                                       return v;
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties.
	 * @since 1.8
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		return base.putIfAbsent(key, value);
	}

	/**
	 * Removes the entry for the specified key only if it is currently
	 * mapped to the specified value.
	 *
	 * @param key   key with which the specified value is associated
	 * @param value value expected to be associated with the specified key
	 * @return {@code true} if the value was removed
	 * @throws UnsupportedOperationException if the {@code remove} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the key or value is of an inappropriate
	 *                                       type for this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified key or value is null,
	 *                                       and this map does not permit null keys or values
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       if (map.containsKey(key) && Objects.equals(map.get(key), value)) {
	 *                                           map.remove(key);
	 *                                           return true;
	 *                                       } else
	 *                                           return false;
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties.
	 * @since 1.8
	 */
	@Override
	public boolean remove(Object key, Object value) {
		return base.remove(key, value);
	}

	/**
	 * Replaces the entry for the specified key only if currently
	 * mapped to the specified value.
	 *
	 * @param key      key with which the specified value is associated
	 * @param oldValue value expected to be associated with the specified key
	 * @param newValue value to be associated with the specified key
	 * @return {@code true} if the value was replaced
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of a specified key or value
	 *                                       prevents it from being stored in this map
	 * @throws NullPointerException          if a specified key or newValue is null,
	 *                                       and this map does not permit null keys or values
	 * @throws NullPointerException          if oldValue is null and this map does not
	 *                                       permit null values
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws IllegalArgumentException      if some property of a specified key
	 *                                       or value prevents it from being stored in this map
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       if (map.containsKey(key) && Objects.equals(map.get(key), value)) {
	 *                                           map.put(key, newValue);
	 *                                           return true;
	 *                                       } else
	 *                                           return false;
	 *                                       }</pre>
	 *                                       <p>
	 *                                       The default implementation does not throw NullPointerException
	 *                                       for maps that do not support null values if oldValue is null unless
	 *                                       newValue is also null.
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties.
	 * @since 1.8
	 */
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return base.replace(key, oldValue, newValue);
	}

	/**
	 * Replaces the entry for the specified key only if it is
	 * currently mapped to some value.
	 *
	 * @param key   key with which the specified value is associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with the specified key, or
	 * {@code null} if there was no mapping for the key.
	 * (A {@code null} return can also indicate that the map
	 * previously associated {@code null} with the key,
	 * if the implementation supports null values.)
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified key or value is null,
	 *                                       and this map does not permit null keys or values
	 * @throws IllegalArgumentException      if some property of the specified key
	 *                                       or value prevents it from being stored in this map
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       if (map.containsKey(key)) {
	 *                                           return map.put(key, value);
	 *                                       } else
	 *                                           return null;
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties.
	 * @since 1.8
	 */
	@Override
	public V replace(K key, V value) {
		return base.replace(key, value);
	}

	/**
	 * If the specified key is not already associated with a value (or is mapped
	 * to {@code null}), attempts to compute its value using the given mapping
	 * function and enters it into this map unless {@code null}.
	 * <p>
	 * <p>If the function returns {@code null} no mapping is recorded. If
	 * the function itself throws an (unchecked) exception, the
	 * exception is rethrown, and no mapping is recorded.  The most
	 * common usage is to construct a new object serving as an initial
	 * mapped value or memoized result, as in:
	 * <p>
	 * <pre> {@code
	 * map.computeIfAbsent(key, k -> new Value(f(k)));
	 * }</pre>
	 * <p>
	 * <p>Or to implement a multi-value map, {@code Map<K,Collection<V>>},
	 * supporting multiple values per key:
	 * <p>
	 * <pre> {@code
	 * map.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
	 * }</pre>
	 *
	 * @param key             key with which the specified value is to be associated
	 * @param mappingFunction the function to compute a value
	 * @return the current (existing or computed) value associated with
	 * the specified key, or null if the computed value is null
	 * @throws NullPointerException          if the specified key is null and
	 *                                       this map does not support null keys, or the mappingFunction
	 *                                       is null
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       {@code map}, then returning the current value or {@code null} if now
	 *                                       absent:
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       if (map.get(key) == null) {
	 *                                           V newValue = mappingFunction.apply(key);
	 *                                           if (newValue != null)
	 *                                               map.put(key, newValue);
	 *                                       }
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties. In particular, all implementations of
	 *                                       subinterface {@link ConcurrentMap} must document
	 *                                       whether the function is applied once atomically only if the value is not
	 *                                       present.
	 * @since 1.8
	 */
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return base.computeIfAbsent(key, mappingFunction);
	}

	/**
	 * If the value for the specified key is present and non-null, attempts to
	 * compute a new mapping given the key and its current mapped value.
	 * <p>
	 * <p>If the function returns {@code null}, the mapping is removed.  If the
	 * function itself throws an (unchecked) exception, the exception is
	 * rethrown, and the current mapping is left unchanged.
	 *
	 * @param key               key with which the specified value is to be associated
	 * @param remappingFunction the function to compute a value
	 * @return the new value associated with the specified key, or null if none
	 * @throws NullPointerException          if the specified key is null and
	 *                                       this map does not support null keys, or the
	 *                                       remappingFunction is null
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       steps for this {@code map}, then returning the current value or
	 *                                       {@code null} if now absent:
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       if (map.get(key) != null) {
	 *                                           V oldValue = map.get(key);
	 *                                           V newValue = remappingFunction.apply(key, oldValue);
	 *                                           if (newValue != null)
	 *                                               map.put(key, newValue);
	 *                                           else
	 *                                               map.remove(key);
	 *                                       }
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties. In particular, all implementations of
	 *                                       subinterface {@link ConcurrentMap} must document
	 *                                       whether the function is applied once atomically only if the value is not
	 *                                       present.
	 * @since 1.8
	 */
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return base.computeIfPresent(key, remappingFunction);
	}

	/**
	 * Attempts to compute a mapping for the specified key and its current
	 * mapped value (or {@code null} if there is no current mapping). For
	 * example, to either create or append a {@code String} msg to a value
	 * mapping:
	 * <p>
	 * <pre> {@code
	 * map.compute(key, (k, v) -> (v == null) ? msg : v.concat(msg))}</pre>
	 * (Method {@link #merge merge()} is often simpler to use for such purposes.)
	 * <p>
	 * <p>If the function returns {@code null}, the mapping is removed (or
	 * remains absent if initially absent).  If the function itself throws an
	 * (unchecked) exception, the exception is rethrown, and the current mapping
	 * is left unchanged.
	 *
	 * @param key               key with which the specified value is to be associated
	 * @param remappingFunction the function to compute a value
	 * @return the new value associated with the specified key, or null if none
	 * @throws NullPointerException          if the specified key is null and
	 *                                       this map does not support null keys, or the
	 *                                       remappingFunction is null
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 *                                       steps for this {@code map}, then returning the current value or
	 *                                       {@code null} if absent:
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       V oldValue = map.get(key);
	 *                                       V newValue = remappingFunction.apply(key, oldValue);
	 *                                       if (oldValue != null ) {
	 *                                          if (newValue != null)
	 *                                             map.put(key, newValue);
	 *                                          else
	 *                                             map.remove(key);
	 *                                       } else {
	 *                                          if (newValue != null)
	 *                                             map.put(key, newValue);
	 *                                          else
	 *                                             return null;
	 *                                       }
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties. In particular, all implementations of
	 *                                       subinterface {@link ConcurrentMap} must document
	 *                                       whether the function is applied once atomically only if the value is not
	 *                                       present.
	 * @since 1.8
	 */
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return base.compute(key, remappingFunction);
	}

	/**
	 * If the specified key is not already associated with a value or is
	 * associated with null, associates it with the given non-null value.
	 * Otherwise, replaces the associated value with the results of the given
	 * remapping function, or removes if the result is {@code null}. This
	 * method may be of use when combining multiple mapped values for a key.
	 * For example, to either create or append a {@code String msg} to a
	 * value mapping:
	 * <p>
	 * <pre> {@code
	 * map.merge(key, msg, String::concat)
	 * }</pre>
	 * <p>
	 * <p>If the function returns {@code null} the mapping is removed.  If the
	 * function itself throws an (unchecked) exception, the exception is
	 * rethrown, and the current mapping is left unchanged.
	 *
	 * @param key               key with which the resulting value is to be associated
	 * @param value             the non-null value to be merged with the existing value
	 *                          associated with the key or, if no existing value or a null value
	 *                          is associated with the key, to be associated with the key
	 * @param remappingFunction the function to recompute a value if present
	 * @return the new value associated with the specified key, or null if no
	 * value is associated with the key
	 * @throws UnsupportedOperationException if the {@code put} operation
	 *                                       is not supported by this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws ClassCastException            if the class of the specified key or value
	 *                                       prevents it from being stored in this map
	 *                                       (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified key is null and this map
	 *                                       does not support null keys or the value or remappingFunction is
	 *                                       null
	 *                                       steps for this {@code map}, then returning the current value or
	 *                                       {@code null} if absent:
	 *                                       <p>
	 *                                       <pre> {@code
	 *                                       V oldValue = map.get(key);
	 *                                       V newValue = (oldValue == null) ? value :
	 *                                                    remappingFunction.apply(oldValue, value);
	 *                                       if (newValue == null)
	 *                                           map.remove(key);
	 *                                       else
	 *                                           map.put(key, newValue);
	 *                                       }</pre>
	 *                                       <p>
	 *                                       <p>The default implementation makes no guarantees about synchronization
	 *                                       or atomicity properties of this method. Any implementation providing
	 *                                       atomicity guarantees must override this method and document its
	 *                                       concurrency properties. In particular, all implementations of
	 *                                       subinterface {@link ConcurrentMap} must document
	 *                                       whether the function is applied once atomically only if the value is not
	 *                                       present.
	 * @since 1.8
	 */
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return base.merge(key, value, remappingFunction);
	}

	/**
	 * Get the element from the map, and if it does not exist, create it.
	 *
	 * @param key the key
	 * @return an element
	 */
	public V elem(K key) {
		V elem = base.get(key);
		if (elem == null) {
			elem = newElementProducer.apply(key);
			base.put(key, elem);
		}
		return elem;
	}


}

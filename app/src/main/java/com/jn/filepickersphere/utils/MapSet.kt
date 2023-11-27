package com.jn.filepickersphere.utils

open class MapSet<K, V> protected constructor(
    private val keyExtractor: (V) -> K,
    isLinked: Boolean
) : AbstractMutableSet<V>() {

    private val map: MutableMap<K, V>

    init {
        this.map = if (isLinked) linkedMapOf() else hashMapOf()
    }

    override fun iterator(): MutableIterator<V> = map.values.iterator()

    override val size: Int
        get() = map.size

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun contains(element: V): Boolean = map.containsKey(keyExtractor(element))

    override fun add(element: V): Boolean = map.put(keyExtractor(element), element) == null

    override fun remove(element: V): Boolean = map.remove(keyExtractor(element)) != null

    override fun clear() {
        map.clear()
    }
}

open class LinkedMapSet<K, V>(keyExtractor: (V) -> K) : MapSet<K, V>(keyExtractor, true)
package com.example.esp32gauges.ds

class CircularBuffer<T>(private val capacity: Int) {
    private val buffer = arrayOfNulls<Any?>(capacity)
    private var index = 0

    fun add(element: T) {
        buffer[index] = element
        index++
    }

    fun getLatest(): T? {
        @Suppress("UNCHECKED_CAST") // lmao
        return buffer[index - 1 % capacity] as T?
    }

    fun getLast(n: Int): List<T> {
        return listOf()
    }
}
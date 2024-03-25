package com.example.esp32gauges.ds

class CircularBuffer<T>(private val capacity: Int) {
    private val buffer = ArrayDeque<T>(capacity)

    fun add(element: T) {
        if (buffer.size == capacity) {
            buffer.removeFirst()
        }

        buffer.addLast(element)
    }

    fun get(): List<T> {
        return  buffer.toList()
    }
}
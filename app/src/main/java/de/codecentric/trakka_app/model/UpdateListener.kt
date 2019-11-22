package de.codecentric.trakka_app.model

import android.util.Log
import java.lang.Exception
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KProperty

@FunctionalInterface
interface UpdateListener<T> {
    fun onUpdated(oldValue: T, newValue: T)
}

class UpdateDispatcher<T>(private var value: T, private val listeners: CopyOnWriteArraySet<UpdateListener<T>>) {
    operator fun getValue(owner: Any, property: KProperty<*>): T = value

    @Suppress("UNCHECKED_CAST")
    operator fun setValue(owner: Any, property: KProperty<*>, any: Any) {
        val newValue = any as T
        if (value != newValue) {
            val oldValue = value
            value = newValue
            for (listener in listeners) {
                try {
                    listener.onUpdated(oldValue, newValue)
                } catch (e: Exception) {
                    Log.e(TAG, "Error notifying listener $listener about change $oldValue -> $newValue", e)
                }
            }
        }
    }
}
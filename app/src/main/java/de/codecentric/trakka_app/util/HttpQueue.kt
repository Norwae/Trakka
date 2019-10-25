package de.codecentric.trakka_app.util

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

private var queue: RequestQueue? = null

fun volleyQueue(context: Context): RequestQueue {
    if (queue == null) synchronized(Volley::class.java) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.applicationContext).also {
                it.start()
            }

        }
    }

    return queue!!
}
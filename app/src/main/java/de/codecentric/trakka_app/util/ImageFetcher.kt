package de.codecentric.trakka_app.util

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest
import java.lang.ref.WeakReference

fun ImageView.setImageFromURI(uri: Uri) {
    val queue = volleyQueue(context)
    val ref = WeakReference(this)

    val tag = "ImageFetch"
    Log.i(tag, "Starting fetch of $uri for $this")
    queue.add(ImageRequest(
        uri.toString(),
        {
            val view = ref.get()
            Log.d(tag, "Successfully got image from $uri, current referent view $view")
            view?.setImageBitmap(it)
        },
        maxWidth,
        maxHeight,
        ImageView.ScaleType.FIT_CENTER,
        Bitmap.Config.ARGB_8888,
        { Log.e(tag, "Could not fetch image from $uri", it) }
    ))
}


package com.catchday.catchapp.ui.base.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

abstract class BaseSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback, Runnable {

    private var thread: Thread? = null

    var isThreadRunning = false
        private set

    init {
        // init surface holder callback
        holder.addCallback(this)
        // make surface background trans
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // init thread
        isThreadRunning = true
        thread = Thread(this)
        thread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isThreadRunning = false
        if (null == thread) return
        while (true) {
            try {
                thread!!.join()
                return
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun run() {
        var canvas: Canvas?

        while (isThreadRunning) {
            synchronized(holder) {
                // get canvas
                canvas = holder.lockCanvas()
                if (canvas == null) {
                    return
                }
                // refresh canvas
                canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                // draw canvas
                onDrawSurfaceCanvas(canvas!!)
                holder.unlockCanvasAndPost(canvas)

                try {
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    protected abstract fun onDrawSurfaceCanvas(canvas: Canvas)
}
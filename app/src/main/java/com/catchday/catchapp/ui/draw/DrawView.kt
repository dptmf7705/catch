package com.catchday.catchapp.ui.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.catchday.catchapp.ui.base.views.BaseSurfaceView
import com.catchday.catchapp.ui.common.rx.RxChangeStream
import java.util.*

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BaseSurfaceView(context, attrs, defStyle) {

    companion object {

        private val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            maskFilter = null
            isAntiAlias = true
            isDither = true
            color = Color.WHITE
        }

        private val eraser = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val drawingStack = Stack<Drawing>()

    private val drawingBackStack = Stack<Drawing>()

    private var currentStrokeWidth = 40f

    private var currentPaintType = PaintType.PEN

    private var cachedBitmap: Bitmap? = null

    private var cachedCanvas: Canvas? = null

    val drawingStackChanges = RxChangeStream<Unit>()

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        cachedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cachedBitmap!!.eraseColor(Color.TRANSPARENT)
        cachedCanvas = Canvas().apply {
            setBitmap(cachedBitmap)
        }
    }

    override fun onDrawSurfaceCanvas(canvas: Canvas) {
        drawAllPathToCanvas(canvas)
    }

    private fun drawAllPathToCanvas(canvas: Canvas) {
        synchronized(drawingStack) {
            drawingStack.takeIf { it.isNotEmpty() }?.forEach { drawing ->
                canvas.drawPath(
                    drawing.path,
                    paint.apply {
                        strokeWidth = drawing.width
                        xfermode = if (drawing.type == PaintType.ERASER) eraser else null
                    })
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart(eventX, eventY)
            MotionEvent.ACTION_MOVE -> onTouchMove(eventX, eventY)
            MotionEvent.ACTION_UP -> onTouchEnd(eventX, eventY)
        }
        return true
    }

    private fun onTouchStart(x: Float, y: Float) {
        synchronized(drawingStack) {
            val newDrawing = Drawing(
                Path().apply { moveTo(x, y) },
                currentStrokeWidth,
                currentPaintType
            )
            drawingStack.push(newDrawing)
            drawingBackStack.clear()
        }
    }

    private fun onTouchMove(x: Float, y: Float) {
        synchronized(drawingStack) {
            drawingStack.peek().path.lineTo(x, y)
        }
    }

    private fun onTouchEnd(x: Float, y: Float) {
        synchronized(drawingStack) {
            drawingStack.peek().path.lineTo(x, y)
            drawingStackChanges.onNext(Unit)
        }
    }

    fun getDrawBitmap(): Bitmap? {
        drawAllPathToCanvas(cachedCanvas!!)
        return cachedBitmap
    }

    fun removeLatestPath() {
        synchronized(drawingStack) {
            if (drawingStack.isNotEmpty()) {
                drawingBackStack.push(drawingStack.pop())
                drawingStackChanges.onNext(Unit)
                invalidate()
            }
        }
    }

    fun redrawLatestRemovedPath() {
        synchronized(drawingStack) {
            if (drawingBackStack.isNotEmpty()) {
                drawingStack.push(drawingBackStack.pop())
                drawingStackChanges.onNext(Unit)
                invalidate()
            }
        }
    }

    fun changePaintWidth(width: Float) {
        currentStrokeWidth = width
    }

    fun changePaintType(paintType: PaintType) {
        currentPaintType = paintType
    }

    fun isEmptyDrawing() = synchronized(drawingStack) {
        drawingStack.isNullOrEmpty()
    }

    fun isEmptyBackStack() = synchronized(drawingBackStack) {
        drawingBackStack.isNullOrEmpty()
    }

    enum class PaintType { PEN, ERASER }

    private class Drawing(

        val path: Path,

        val width: Float,

        val type: PaintType
    )
}
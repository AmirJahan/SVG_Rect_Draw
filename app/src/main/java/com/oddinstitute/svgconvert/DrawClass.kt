package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.log


class DrawView : View
{
    var artworkData: Artwork = Artwork()

    var mainPaint: Paint = Paint()

    var cornerSelectionHandle: RectF? = null
    var selectionBorder: RectF? = null


    var cornerEditingMode = false

    var polygonSelectionMode = true


    //    var selectedPolygon: PolygonAllPaths? = null
    var selectedPolygon: Path? = null


//    var selectedPolygon: PolygonPath? = null


    var selectedPolygonIndex = -1

    var corners: ArrayList<Path>? = null
    var selectedCornerIndex: Int = -1


    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context?,
                artworkData: Artwork) : super(context)
    {
        this.artworkData = artworkData


        mainPaint = Paint()
//        cornersPaint = Paint ()
        mainPaint.isAntiAlias = true


        makePaths()


        this.setOnTouchListener(onTouchListener())
//        this.setOnTouchListener(cornerTouchListener())
    }


/*
constructor(context: Context?, attrs: AttributeSet?) : super(context,
                                                             attrs)
{
    init(null)
}

constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
                                                                           )
{
    init(null)
}

 */

//    @SuppressLint("ClickableViewAccessibility")
//    private fun init(polygonsData: ArrayList<PolygonData>)
//    {
//
//
//
//    }


    var dX = 0f
    var dY = 0f

    fun onTouch(view: View, event: MotionEvent): Boolean
    {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> view.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
            else -> return false
        }
        return true
    }

    var xStart = 0f
    var yStart = 0f

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListener(): OnTouchListener?
    {
        return OnTouchListener { view, event ->

            val touchX = event.x
            val touchY = event.y

            when (event.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN -> // select something
                {
                    xStart = event.rawX
                    yStart = event.rawY


                    // We are either in poly selection mode or corner editing mode
                    // the default is polygon selection mode
                    if (polygonSelectionMode)
                    {
                        // no polygon selected
                        if (selectedPolygonIndex == -1)
                        {
                            polySelection(touchX,
                                          touchY)
                        }
                        else
                        {
                            switchPolySelection(event)
                            // some polygon selected
//                            cornerSelectionHandle.let {
//                                if (cornerSelectionHandle!!.contains(touchX,
//                                                                     touchY))
//                                {
//                                    // Check if we touched a handle, let's go corner editing mode
//                                    cornerEditingMode = true
//                                    // so no more polygon selection
//                                    polygonSelectionMode = false
//                                }
//                                else
//                                {
//                                    // we haven't touched the handle
//                                    // so, let's drop everything back to the default
//                                    selectedPolygonIndex = -1
//                                    selectedPolygon = null
//
//                                    cornerSelectionHandle = null
//                                    selectionBorder = null
//
//                                    polygonSelectionMode = true
//                                    polySelection(touchX,
//                                                  touchY)
//                                }
//                            }
                        }
                    }
                    else if (cornerEditingMode)
                    {
                        pickCorner(event)
//                        // In Corner Editing Mode, check if we tapped don the selection handle
//                        cornerSelectionHandle.let {
//                            if (cornerSelectionHandle!!.contains(touchX,
//                                                                 touchY))
//                            {
//                                // we were in corner editing
//                                // let's go out now
//                                cornerEditingMode = false
//                                polygonSelectionMode = true
//                            }
//                        }
//
//                        // In corner Editing Mode, we didn't touch the handle
//                        // now, we have to deal with corners
//                        selectedPolygon.let {
//
//                            corners.let {
//
//                                for (i in 0 until corners!!.count())
//                                {
//                                    val corner = corners!![i]
//                                    val pathBounds = RectF()
//
//                                    corner.computeBounds(pathBounds,
//                                                         true)
//
//                                    val outerInvisibleRect = RectF(pathBounds.left - 25f,
//                                                                   pathBounds.top - 25f,
//                                                                   pathBounds.right + 25f,
//                                                                   pathBounds.bottom + 25f)
//
//                                    if (outerInvisibleRect.contains(touchX,
//                                                                    touchY))
//                                    {
//                                        selectedCornerIndex = i
//                                        this.invalidate()
//                                        break
//                                    }
//
//                                    // we reached here, it means no corner was tapped on
//                                    selectedCornerIndex = -1
//                                    this.invalidate()
//                                }
//                            }
//                        }
                    }
                }

                MotionEvent.ACTION_MOVE ->
                {
                    val deltaX = event.rawX - xStart
                    val deltaY = event.rawY - yStart
                    xStart = event.rawX
                    yStart = event.rawY


                    Log.d("MyTag", "\nDelta X is: $deltaX\nDelta Y is: $deltaY")
                    // here, we are moving a corner


                    // a polygon is selected but we hhave NOT entered corner editing mode
                    if (selectedPolygonIndex != -1 && !cornerEditingMode)
                    {
                        moveAllCornersTo(deltaX, deltaY)
                    }
                    // corner is selected
                    else if (selectedCornerIndex != -1)
                    {
                        moveCorner(event)
                    }


//                        val newCorner = PointF(touchX, touchY)
//                        this.artworkData.polygons[selectedPolygonIndex].data.pathData[selectedCornerIndex] =
//                                newCorner
//                        this.invalidate()
//                    }
                }


                MotionEvent.ACTION_UP ->
                {
                    // deselect corner

                }
            }

            this.invalidate()

            true
        }
    }

    fun pickCorner(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y
        // In Corner Editing Mode, check if we tapped don the selection handle
        cornerSelectionHandle.let {
            if (cornerSelectionHandle!!.contains(touchX,
                                                 touchY))
            {
                // we were in corner editing
                // let's go out now
                cornerEditingMode = false
                polygonSelectionMode = true
            }
        }

        // In corner Editing Mode, we didn't touch the handle
        // now, we have to deal with corners
        selectedPolygon.let {

            corners.let {

                for (i in 0 until corners!!.count())
                {
                    val corner = corners!![i]
                    val pathBounds = RectF()

                    corner.computeBounds(pathBounds,
                                         true)

                    val outerInvisibleRect = RectF(pathBounds.left - 25f,
                                                   pathBounds.top - 25f,
                                                   pathBounds.right + 25f,
                                                   pathBounds.bottom + 25f)

                    if (outerInvisibleRect.contains(touchX,
                                                    touchY))
                    {
                        selectedCornerIndex = i
                        this.invalidate()
                        break
                    }

                    // we reached here, it means no corner was tapped on
                    selectedCornerIndex = -1
                    this.invalidate()
                }
            }
        }
    }

    fun switchPolySelection(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

        cornerSelectionHandle.let {
            if (cornerSelectionHandle!!.contains(touchX,
                                                 touchY))
            {
                // Check if we touched a handle, let's go corner editing mode
                cornerEditingMode = true
                // so no more polygon selection
                polygonSelectionMode = false
            }
            else
            {
                // we haven't touched the handle
                // so, let's drop everything back to the default
                selectedPolygonIndex = -1
                selectedPolygon = null

                cornerSelectionHandle = null
                selectionBorder = null

                polygonSelectionMode = true
                polySelection(touchX,
                              touchY)
            }
        }
    }


    fun moveAllCornersTo (deltaX: Float, deltaY: Float)
    {
        val pathData = this.artworkData.polygons[selectedPolygonIndex].data.pathData
        var newPathData: ArrayList<PointF> = arrayListOf()
        for (i in 0 until pathData.count())
        {
            val corner = pathData[i]
            val newCorner = PointF (corner.x + deltaX, corner.y + deltaY)
            newPathData.add(newCorner)
        }

        this.artworkData.polygons[selectedPolygonIndex].data.pathData = newPathData
    }


    fun moveCorner(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

            val newCorner = PointF(touchX,
                                   touchY)
            this.artworkData.polygons[selectedPolygonIndex].data.pathData[selectedCornerIndex] =
                    newCorner
            this.invalidate()
    }


    fun polySelection(x: Float, y: Float)
    {
        cornerSelectionHandle = null

        for (i in 0 until artworkData.polygons.count())
        {
            val polygon = artworkData.polygons[i]
            val path = polygon.path

            val pathBounds = RectF()
            path.computeBounds(pathBounds,
                               true)

            if (pathBounds.contains(x,
                                    y))
            {
                selectedPolygonIndex = i
                selectedPolygon = path

                return
            }
        }

    }


    fun cleanCanvas(mCanvas: Canvas)
    {
        // clear and refresh paths
        artworkData.clearPaths()

        mainPaint.color = Color.TRANSPARENT
        mCanvas.drawPaint(mainPaint)
    }


    fun drawCornerFor(polygonData: PolygonData): ArrayList<Path>
    {
        val cornerSize: Float = 10f
        val cornerRadius: Float = 7f
        var cornersPaths: ArrayList<Path> = arrayListOf()

        for (corner in polygonData.pathData)
        {
            val cornerPoint = RectF(corner.x - cornerSize,
                                    corner.y + cornerSize,
                                    corner.x + cornerSize,
                                    corner.y - cornerSize)

            val cornerPath = Path()
            cornerPath.addRoundRect(cornerPoint,
                                    cornerRadius,
                                    cornerRadius,
                                    Path.Direction.CCW)

            cornersPaths.add(cornerPath)
        }

        return cornersPaths
    }


    fun makePathFor(data: PolygonData): Path
    {
        // Here, draw the MAIN Path
        var path = Path()
        path.fillType = Path.FillType.EVEN_ODD

        path.moveToPoint(data.pathData[0])
        path.lineToPoint(data.pathData[1])
        path.lineToPoint(data.pathData[2])
        path.lineToPoint(data.pathData[3])
        path.lineToPoint(data.pathData[4])
        path.close()

        return path
    }





    fun drawCorners ()
    {
        val polygon = artworkData.polygons[selectedPolygonIndex]

        val polygonData = polygon.data
        corners = drawCornerFor(polygonData)
    }


    // should become make or refresh
    fun makePaths()
    {
        artworkData.clearPaths()

        for (i in 0 until artworkData.polygons.count())
        {
            val polygon = artworkData.polygons[i]
            val path = makePathFor(polygon.data)
            polygon.path = path
            artworkData.polygons[i] = polygon
        }

        // if a polygon is selected, we should draw its corners
        if (selectedPolygonIndex != -1)
        {
            // 1) we draw its corners
            drawCorners()

            // b) draw selection  border
            drawSelectionBorder ()

            // c) draw selection Handle
            drawCornerSelectionHandle ()
        }

        // if we are in the corner editing mode and moved
        // we have to update the selected polygon
        if (cornerEditingMode)
        {
            selectedPolygon = artworkData.polygons[selectedPolygonIndex].path
//            selectedPolygon = polygonsPaths[selectedPolygonIndex]
        }
    }


    override fun onDraw(canvas: Canvas)
    {
        cleanCanvas(canvas)


        // if a polygon is selected, we will update only that one.
        // otherwise, we will update everyone
        makePaths()

        // drawing all paths
        for (each in artworkData.polygons)
        {
            val path = each.path
            val data = each.data
            styleFillPaint(data)
            canvas.drawPath(path,
                            mainPaint)

            styleBorderPaint(data)
            canvas.drawPath(path,
                            mainPaint)
        }


        selectionBorder?.let {
            styleSelectionBorderPaint()
            canvas.drawRoundRect(selectionBorder!!,
                                 8f,
                                 8f,
                                 mainPaint)
        }

        // we should draw the handle
        cornerSelectionHandle?.let {
            styleNormalSelectionHandlePaint()

            if (cornerEditingMode)
                styleHighlightedSelectionHandlePaint()

            canvas.drawRoundRect(cornerSelectionHandle!!,
                                 8f,
                                 8f,
                                 mainPaint)
        }


        if (cornerEditingMode)
        {
            corners.let {
                for (i in 0 until corners!!.count())
                {
                    val corner = corners!![i]
                    styleNormalCornerPaint()

                    if (i == selectedCornerIndex)
                        styleSelectedCornerPaint()

                    canvas.drawPath(corner,
                                    mainPaint)
                }
            }
        }
    }

    fun drawSelectionBorder()
    {
        // DRAW THE BORDER
        val path = artworkData.polygons[selectedPolygonIndex].path
        val pathBounds = RectF()
        path.computeBounds(pathBounds,
                           true)

        val rectf = RectF(pathBounds.left - 10f,
                          pathBounds.top - 10f,
                          pathBounds.right + 10f,
                          pathBounds.bottom + 10f)
        selectionBorder = rectf
    }

    fun drawCornerSelectionHandle()
    {
        if (selectedPolygonIndex == -1)
            cornerSelectionHandle = null

        val path = artworkData.polygons[selectedPolygonIndex].path

        val pathBounds = RectF()
        path.computeBounds(pathBounds,
                           true)

        val height = pathBounds.bottom - pathBounds.top
        val rectf = RectF(pathBounds.right + 20f,
                          pathBounds.top + (height / 2f) - 25f,
                          pathBounds.right + 70f,
                          pathBounds.top + (height / 2f) + 25f)

        cornerSelectionHandle = rectf
    }

    fun styleNormalCornerPaint()
    {
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 5.0f
        mainPaint.color = Color.YELLOW
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleSelectedCornerPaint()
    {
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 5.0f
        mainPaint.color = Color.RED
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleBorderPaint(polygonData: PolygonData)
    {
        // stroke
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = polygonData.strokeWidth
        mainPaint.color = polygonData.strokeColor
        mainPaint.strokeCap = polygonData.strokeLineCap
    }

    fun styleSelectionBorderPaint()
    {
        // stroke
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 3f
        mainPaint.color =
                Color.rgb(0,
                          0,
                          153)
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleNormalSelectionHandlePaint()
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.DKGRAY
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleHighlightedSelectionHandlePaint()
    {

        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.RED
        mainPaint.strokeCap = Paint.Cap.ROUND

    }

    fun styleFillPaint(polygonData: PolygonData)
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = polygonData.fillColor
    }
}


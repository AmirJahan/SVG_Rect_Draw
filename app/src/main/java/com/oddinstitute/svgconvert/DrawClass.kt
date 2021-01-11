package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener


class PolygonData
{
    var corners: ArrayList<PointF> = arrayListOf()
}

class PolygonPath
{
    lateinit var mainPath: Path
    lateinit var cornersPaths: ArrayList<Path>
}

class DrawView : View
{
    lateinit var mainPaint: Paint

    var cornerSelectionHandle: RectF? = null
    var cornerEditingMode = false

    var polygonSelectionMode = true


    var selectedPolygon: PolygonPath? = null

    var polygonsPaths: ArrayList<PolygonPath> = arrayListOf()
//    var selectedPolygon: PolygonPath? = null


    var selectedPolygonIndex = -1

    var selectedCornerIndex: Int = -1

    lateinit var polygonsDataArray: ArrayList<PolygonData>


    var clear = false


    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context?,
                polygons: ArrayList<PolygonData>) : super(context)
    {
        init(polygons)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun init(polygonsData: ArrayList<PolygonData>)
    {
        this.polygonsDataArray = polygonsData


        mainPaint = Paint()
//        cornersPaint = Paint ()
        mainPaint.isAntiAlias = true


        makeAllPaths()


        this.setOnTouchListener(onTouchListener())
//        this.setOnTouchListener(cornerTouchListener())


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListener(): OnTouchListener?
    {
        return OnTouchListener { view, event ->

            val touchX = event.x
            val touchY = event.y

            when (event.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN ->
                {
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
                            // some polygon selected
                            cornerSelectionHandle.let {
                                if (cornerSelectionHandle!!.contains(touchX,
                                                                     touchY))
                                {
                                    // Che if we touched a handle, let's go corner editing mode
                                    cornerEditingMode = true
                                    // so no more polygon selection
                                    polygonSelectionMode = false
                                }
                                else
                                {
                                    // we haven't touched the handle
                                    // so, let's drop everything back to the default
                                    selectedPolygonIndex = -1
                                    cornerEditingMode = false
                                    cornerSelectionHandle = null
                                    selectedPolygon = null

                                    polygonSelectionMode = true
                                    polySelection(touchX,
                                                  touchY)
                                }
                            }
                        }
                    }
                    else if (cornerEditingMode)
                    {
                        // In Corner Editing Mode, check if we tapped don the selection handle
                        cornerSelectionHandle.let {
                            if (cornerSelectionHandle!!.contains(touchX,
                                                                 touchY))
                            {
                                // we were in corner editing
                                    // let's go out now
                                cornerEditingMode = false
                                polygonSelectionMode = true
                                true
                            }
                        }

                        // In corner Editing Mode, we didn't touch the handle
                        // now, we have to deal with corners
                        selectedPolygon.let {
                            val cornersPaths = selectedPolygon!!.cornersPaths

                            for (i in 0 until cornersPaths.count())
                            {
                                val corner = cornersPaths[i]
                                val pathBounds = RectF()
                                corner.computeBounds(pathBounds,
                                                     true)

                                if (pathBounds.contains(touchX,
                                                        touchY))
                                {
                                    Log.d("MyTag", "Index is: $selectedCornerIndex")

                                    selectedCornerIndex = i
                                    this.invalidate()
                                    true
                                }
                            }
                        }
                    }
                }

                MotionEvent.ACTION_MOVE ->
                {
//                    Log.d("MyTag", "Selected corner is: $selectedCornerIndex")
                    if (selectedCornerIndex != -1)
                    {
                        val newCorner =
                                PointF(touchX,
                                       touchY)

                        this.polygonsDataArray[selectedPolygonIndex].corners[selectedCornerIndex] =
                                newCorner

//                        Log.d("MyTag", "Moving corner to: $touchX and $touchY ")
                        this.invalidate()
                    }
                }


                MotionEvent.ACTION_UP ->
                {
                    if (selectedCornerIndex != -1)
                    {
                        selectedCornerIndex = -1
                        this.invalidate()
                    }
                }
            }


            this.invalidate()
            Log.d("MyTag", "\npolygon Selection: $polygonSelectionMode\ncorner editing: $cornerEditingMode\nSelection handle: $cornerSelectionHandle")


            true
        }
    }


    fun polySelection(x: Float, y: Float)
    {
        cornerSelectionHandle = null

        for (i in 0 until polygonsPaths.count())
        {
            val polygon = polygonsPaths[i]
            val polygonPath = polygon.mainPath
            val pathBounds = RectF()
            polygonPath.computeBounds(pathBounds,
                                      true)

            if (pathBounds.contains(x,
                                    y))
            {
                selectedPolygonIndex = i
                selectedPolygon = polygonsPaths[selectedPolygonIndex]

                return
            }
        }

    }





    fun cleanCanvas(mCanvas: Canvas)
    {
        polygonsPaths = arrayListOf()

        mainPaint.color = Color.TRANSPARENT
        mCanvas.drawPaint(mainPaint)
    }


    fun makeAllPaths()
    {
        polygonsPaths.clear()
        for (polygonData in polygonsDataArray)
        {
            // CORNERS
            val cornerSize: Float = 10f
            val cornerRadius: Float = 7f
            var cornersPaths: ArrayList<Path> = arrayListOf()

            for (corner in polygonData.corners)
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


            // MAIN
            var mainPath = Path()
            mainPath.fillType = Path.FillType.EVEN_ODD

            mainPath.moveToPoint(polygonData.corners[0])
            mainPath.lineToPoint(polygonData.corners[1])
            mainPath.lineToPoint(polygonData.corners[2])
            mainPath.lineToPoint(polygonData.corners[3])
            mainPath.close()

            val newPolygon = PolygonPath()
            newPolygon.mainPath = mainPath
            newPolygon.cornersPaths = cornersPaths

            polygonsPaths.add(newPolygon)
        }


        // if we are in the corner editing mode
        // we have to update the selected polygon
        if (cornerEditingMode)
        {
            selectedPolygon = polygonsPaths[selectedPolygonIndex]
        }
    }


    override fun onDraw(canvas: Canvas)
    {
        cleanCanvas(canvas)

        if (clear)
        {
            clear = false
            cornerEditingMode = false
            return
        }


        makeAllPaths()

        for (each in polygonsPaths)
        {
            val mainPath = each.mainPath
            styleFillPaint()
            canvas.drawPath(mainPath,
                            mainPaint)

            styleBorderPaint()
            canvas.drawPath(mainPath,
                            mainPaint)
        }

        val selectionBorder = drawSelectionBorderAndHandle()

        selectionBorder?.let {
            styleSelectionBorderPaint()
            canvas.drawRoundRect(selectionBorder,
                                 8f,
                                 8f,
                                 mainPaint)
        }

        cornerSelectionHandle?.let {
            styleSelectionHandlePaint()
//            Log.d("MyTag", "There is Handle")

            canvas.drawRoundRect(cornerSelectionHandle!!,
                                 8f,
                                 8f,
                                 mainPaint)
        }


        if (cornerEditingMode)
        {
//            Log.d("MyTag", "We are in corner Editing mode")
            val polygonPath = polygonsPaths[selectedPolygonIndex]

            for (i in 0 until polygonPath.cornersPaths.count())
            {
                val corner = polygonPath.cornersPaths[i]
                styleNormalCornerPaint()

                if (i == selectedCornerIndex)
                    styleSelectedCornerPaint()

                canvas.drawPath(corner,
                                mainPaint)
            }

        }
    }

    fun drawSelectionBorderAndHandle(): RectF?
    {
        if (selectedPolygonIndex == -1)
            return null

        // DRAW the HANDLE
        drawCornerSelectionHandle()

        // DRAW AND RETURN THE BORDER
        val polygon = polygonsPaths[selectedPolygonIndex].mainPath
        val pathBounds = RectF()
        polygon.computeBounds(pathBounds,
                              true)

        val rectf = RectF(pathBounds.left - 10f,
                          pathBounds.top - 10f,
                          pathBounds.right + 10f,
                          pathBounds.bottom + 10f)
        return rectf
    }

    fun drawCornerSelectionHandle()
    {
        if (selectedPolygonIndex == -1)
            cornerSelectionHandle = null

        val polygon = polygonsPaths[selectedPolygonIndex].mainPath
        val pathBounds = RectF()
        polygon.computeBounds(pathBounds,
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

    fun styleBorderPaint()
    {
        // stroke
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 2f
        mainPaint.color = Color.BLACK
        mainPaint.strokeCap = Paint.Cap.ROUND
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

    fun styleSelectionHandlePaint()
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.DKGRAY
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleFillPaint()
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.MAGENTA
        mainPaint.strokeCap = Paint.Cap.ROUND
    }
}

fun Path.moveToPoint(point: PointF)
{
    this.moveTo(point.x,
                point.y)
}

fun Path.lineToPoint(point: PointF)
{
    this.lineTo(point.x,
                point.y)
}
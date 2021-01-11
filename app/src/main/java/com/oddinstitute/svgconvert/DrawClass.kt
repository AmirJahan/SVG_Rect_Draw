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

                polyTouchListener(event)


            this.invalidate()


            true
        }
    }



    fun polySelection (x: Float, y: Float)
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
                    return
                }
            }

    }


    fun polyTouchListener(event: MotionEvent)
    {
        var touchX = event.x
        var touchY = event.y
        when (event.action and MotionEvent.ACTION_MASK)
        {
            MotionEvent.ACTION_DOWN ->
            {
                when
                {
                    cornerEditingMode ->
                    {
                    // In Corner Editing Mode, we tapped don the selection handle
                        cornerSelectionHandle.let {
                            if (cornerSelectionHandle!!.contains(touchX,
                                                                 touchY))
                            {
                                cornerEditingMode = false
                                return
                            }
                        }

                        // In corner Editing Mode, we didn't touch the corner
                        // now, we have to deal with corners
                        cornerTouchListener (event)
                    }
                    selectedPolygonIndex == -1 ->
                    {
                        polySelection (touchX, touchY)
                    }
                    else ->
                    {
                        cornerSelectionHandle.let {
                            if (cornerSelectionHandle!!.contains(touchX,
                                                                 touchY))
                            {
                                cornerEditingMode = true
                            }
                            else
                            {
                                // we haven't touched the corner
                                // so, let's drop everything
                                selectedPolygonIndex = -1
                                cornerEditingMode = false
                                cornerSelectionHandle = null

                                polySelection (touchX, touchY)
                            }
                        }
                    }
                }
            }
        }
    }


    fun cornerTouchListener(event: MotionEvent)
    {
        //screen touch get x and y of the touch event
        var touchX = event.x
        var touchY = event.y

        if (selectedPolygonIndex == -1)

            when (event.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN ->
                {

                    val polygon: PolygonPath = polygonsPaths[selectedPolygonIndex]

                    val cornersPaths = polygon.cornersPaths

                    for (i in 0 until cornersPaths.count())
                    {
                        val corner = cornersPaths[i]
                        val pathBounds = RectF()
                        corner.computeBounds(pathBounds,
                                             true)

                        if (pathBounds.contains(touchX,
                                                touchY))
                        {
                            // re-draw
//                            editingCornerState = true
                            Log.d("MyTag",
                                  "Found Corner at: $touchX and $touchY")

                            selectedCornerIndex = i
                            this.invalidate()
                        }
                    }

                }

                MotionEvent.ACTION_MOVE ->
                {
                    if (selectedCornerIndex != -1)
                    {

                        //screen touch get x and y of the touch event
                        touchX = event.x
                        touchY = event.y

                        val newCorner =
                                PointF(touchX,
                                       touchY)

                        this.polygonsDataArray[selectedPolygonIndex].corners[selectedCornerIndex] =
                                newCorner

                        Log.d("MyTag",
                              "Moving corner to: $touchX and $touchY ")
                        this.invalidate()
                    }
                }
                MotionEvent.ACTION_UP ->
                {
                    if (selectedCornerIndex != -1)
                    {

//                    editingCornerState = false
                        selectedCornerIndex = -1
                        this.invalidate()
                    }
                }
            }
        true
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

        val selectionBorder = drawSelectionBorderAndHandle ()

        selectionBorder?.let {
            styleSelectionBorderPaint()
            canvas.drawRoundRect(selectionBorder,
                                 8f,
                                 8f,
                                 mainPaint)
        }

        cornerSelectionHandle?.let {
            styleSelectionHandlePaint ()
            Log.d("MyTag", "There is Handle")

            canvas.drawRoundRect(cornerSelectionHandle!!,
                                 8f,
                                 8f,
                                 mainPaint)
        }


        if (cornerEditingMode)
        {
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

    fun drawSelectionBorderAndHandle () : RectF?
    {
        if (selectedPolygonIndex == -1)
            return null

        // DRAW the HANDLE
        drawCornerSelectionHandle ()

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

    fun drawCornerSelectionHandle ()
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
        mainPaint.color = Color.rgb(0, 0, 153)
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
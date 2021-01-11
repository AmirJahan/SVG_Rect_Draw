package com.oddinstitute.svgconvert

import android.content.Context
import android.graphics.*
import android.graphics.Path.FillType
import android.view.View


class MyDraw  : View
{
//    lateinit var strokePaint: Paint
    lateinit var fillPaint: Paint
//    lateinit var cornersPaint: Paint


    lateinit var fillPath: Path
//    lateinit var strokePath: Path
//    lateinit var cornersPath: Path

    lateinit var polygon: PolygonData


    var clear = false
    var showCorners = false

    constructor(context: Context?) : super(context)
    {
        init()
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

    private fun init()
    {
        var polygon: PolygonData = PolygonData()
        val point1 =
                PointF(100f,
                       100f)
        val point2 =
                PointF(300f,
                       100f)
        val point3 =
                PointF(300f,
                       300f)
        val point4 =
                PointF(100f,
                       300f)

        polygon.corners.add(point1)
        polygon.corners.add(point2)
        polygon.corners.add(point3)
        polygon.corners.add(point4)


//        strokePaint = Paint()
        fillPaint = Paint()
//        cornersPaint = Paint ()

        fillPath = Path()
//        strokePath = Path()
//        cornersPath = Path()
    }




    override fun onDraw(canvas: Canvas)
    {
        val paint = Paint()
        paint.isAntiAlias = true

        paint.color = Color.TRANSPARENT
        canvas.drawPaint(paint)




        val a =
                PointF(100f,
                      100f)
        val b =
                PointF(500f,
                      100f)
        val c =
                PointF(300f,
                      500f)

        val path = Path()
        path.fillType = FillType.EVEN_ODD


        path.moveToPoint(a)
        path.lineToPoint(b)
        path.lineToPoint(c)

        path.close()


        // fill
        paint.style = Paint.Style.FILL
        paint.color = Color.MAGENTA
        canvas.drawPath(path,
                        paint)

        // border
        paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f

        paint.color = Color.BLACK
        canvas.drawPath(path,
                        paint)




    }
}

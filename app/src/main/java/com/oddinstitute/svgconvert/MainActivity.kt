package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity


class Artwork
{
    var polygons : ArrayList<Polygon> = arrayListOf()

    fun clearPathAtIndex (index: Int)
    {
        this.polygons[index].clearPath()
    }

    fun clearPaths ()
    {
        for (i in 0 until this.polygons.count())
        {
            this.polygons[i].clearPath()
        }
    }
}

class Polygon
{
    var data: PolygonData = PolygonData()
    var path: Path = Path ()

    fun clearPath ()
    {
        this.path = Path ()
    }
//    var polygonAllPaths: PolygonAllPaths = PolygonAllPaths()
}

class PolygonData
{
    var pathData: ArrayList<PointF> = arrayListOf()
    var fillColor = Color.BLACK
    var strokeColor = Color.TRANSPARENT
    var strokeLineCap : Paint.Cap = Paint.Cap.ROUND
    var strokeWidth = 0.0f
    var fillType = Path.FillType.EVEN_ODD
    var closed = true
}

/*
class PolygonAllPaths
{
    // this hsas to be dropped
    lateinit var mainPath: Path
    lateinit var cornersPaths: ArrayList<Path>

    fun clearPaths ()
    {
        this.mainPath = Path ()
        this.cornersPaths = arrayListOf()
    }
}
*/



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

class MainActivity : AppCompatActivity()
{
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boom = findViewById<FrameLayout>(R.id.canvasView)


        val polygon_1_data = AppData.readPolygonsFromFile()
        val artwork = Artwork()

        for (each in polygon_1_data)
        {
            val poly = Polygon ()
            poly.data = each
            artwork.polygons.add(poly)
        }


        val drawingView =
                DrawView(this,
                         artwork)

        // boom means drawing canvas in Farsi
        boom.addView(drawingView)
    }
}

class AppData
{
    companion object
    {
        fun readPolygonsFromFile () : ArrayList<PolygonData>
        {
            val foundData = arrayListOf(makeTemp_1(), makeTemp_2(), makeTemp_3())

            return foundData
        }

        fun makeTemp_1() : PolygonData
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
                    PointF(250f,
                           290f)

            val point5 =
                    PointF(100f,
                           300f)// M42.3803,56.9069

            polygon.closed = true
            polygon.pathData.add(point1)
            polygon.pathData.add(point2)
            polygon.pathData.add(point3)
            polygon.pathData.add(point4)
            polygon.pathData.add(point5)

            polygon.fillColor = Color.BLUE
            polygon.strokeColor = Color.parseColor("#979797")
            polygon.strokeLineCap = Paint.Cap.ROUND
            polygon.strokeWidth = 2.0f
            polygon.fillType = Path.FillType.EVEN_ODD

            return polygon
        }

        fun makeTemp_2() : PolygonData
        {
            var polygon: PolygonData = PolygonData()
            val point1 =
                    PointF(250f,
                           220f)
            val point2 =
                    PointF(330f,
                           110f)
            val point3 =
                    PointF(540f,
                           120f)
            val point4 =
                    PointF(360f,
                           270f)

            val point5 =
                    PointF(100f,
                           325f)// M42.3803,56.9069

            polygon.closed = true
            polygon.pathData.add(point1)
            polygon.pathData.add(point2)
            polygon.pathData.add(point3)
            polygon.pathData.add(point4)
            polygon.pathData.add(point5)

            polygon.fillColor = Color.MAGENTA
            polygon.strokeColor = Color.parseColor("#897854")
            polygon.strokeLineCap = Paint.Cap.ROUND
            polygon.strokeWidth = 4.0f
            polygon.fillType = Path.FillType.EVEN_ODD

            return polygon
        }

        fun makeTemp_3() : PolygonData
        {
            var polygon: PolygonData = PolygonData()
            val point1 =
                    PointF(350f,
                           420f)
            val point2 =
                    PointF(530f,
                           610f)
            val point3 =
                    PointF(640f,
                           720f)
            val point4 =
                    PointF(560f,
                           470f)

            val point5 =
                    PointF(300f,
                           225f)// M42.3803,56.9069

            polygon.closed = true
            polygon.pathData.add(point1)
            polygon.pathData.add(point2)
            polygon.pathData.add(point3)
            polygon.pathData.add(point4)
            polygon.pathData.add(point5)

            polygon.fillColor = Color.CYAN
            polygon.strokeColor = Color.parseColor("#658921")
            polygon.strokeLineCap = Paint.Cap.ROUND
            polygon.strokeWidth = 4.0f
            polygon.fillType = Path.FillType.EVEN_ODD

            return polygon
        }
    }
}

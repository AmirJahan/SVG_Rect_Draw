package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class Artwork
{
    var polygons : ArrayList<Polygon> = arrayListOf()

//    fun clearPathAtIndex (index: Int)
//    {
//        this.polygons[index].clearPath()
//    }

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

    lateinit var rootView: ConstraintLayout

    var selectedArtworkView: DrawView? = null
    lateinit var boom : FrameLayout
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boom = findViewById<FrameLayout>(R.id.canvasView)

        rootView = findViewById<ConstraintLayout>(R.id.rootView)




        val temp_polygon_data = AppData.readPolygonsFromFile()


        val artwork1 = Artwork()

        for (i in 0 until 3)
        {
            val poly = Polygon ()
            poly.data = temp_polygon_data[i]
            artwork1.polygons.add(poly)
        }
        val drawingView1 = DrawView(this,
                                    artwork1)
        /*
                val layout = findViewById<View>(R.id.testing) as LinearLayout
        mAdView.setLayoutParams(FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                                         height))
         */

        drawingView1.layoutParams = FrameLayout.LayoutParams(512, 512)
//        drawingView1.setBackgroundColor(Color.DKGRAY)

        drawingView1.setOnTouchListener(artworkOnTouchListener())
        boom.addView(drawingView1)



        val artwork2 = Artwork()

        for (i in 3..4)
        {
            val poly = Polygon ()
            poly.data = temp_polygon_data[i]
            artwork2.polygons.add(poly)
        }
        val drawingView2 = DrawView(this,
                                    artwork2)

        drawingView2.setOnTouchListener(artworkOnTouchListener())

        // boom means drawing canvas in Farsi
        drawingView2.layoutParams = FrameLayout.LayoutParams(512, 512)
//        drawingView2.setBackgroundColor(Color.BLACK)
        boom.addView(drawingView2)


        boom.setOnTouchListener(boomOnTouchListener())
    }


    private var xDelta = 0
    private var yDelta = 0

    fun deselectArtwork ()
    {
        selectedArtworkView.let {
            selectedArtworkView?.viewSelected = false
            selectedArtworkView?.invalidate()

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun boomOnTouchListener(): View.OnTouchListener?
    {
        return View.OnTouchListener { view, event ->

            if (view == boom)
            {
                // if touched the boom, just deselect everyone
                deselectArtwork()
            }

            true
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun artworkOnTouchListener(): View.OnTouchListener?
    {
        return View.OnTouchListener { view, event ->


            val x = event.rawX.toInt()
            val y = event.rawY.toInt()

            when (event.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN ->
                {
                    deselectArtwork ()
                    val lParams = view.layoutParams as FrameLayout.LayoutParams
                    xDelta = x - lParams.leftMargin
                    yDelta = y - lParams.topMargin

                    selectedArtworkView = view as DrawView
                    selectedArtworkView!!.viewSelected = true
                    selectedArtworkView!!.invalidate()


                }
                MotionEvent.ACTION_UP ->
                {
//                    (view as DrawView).viewSelected = false
//                    (view as DrawView).invalidate()
                }
                MotionEvent.ACTION_MOVE ->
                {
                    val layoutParams = view
                            .layoutParams as FrameLayout.LayoutParams
                    layoutParams.leftMargin = x - xDelta
                    layoutParams.topMargin = y - yDelta
                    layoutParams.rightMargin = 0
                    layoutParams.bottomMargin = 0
                    view.layoutParams = layoutParams
                }
            }
            rootView!!.invalidate()
            true
        }
    }
}

class AppData
{
    companion object
    {
        fun readPolygonsFromFile () : ArrayList<PolygonData>
        {
            val foundData = arrayListOf(makeTemp_1(),
                                        makeTemp_2(),
                                        makeTemp_3(),
                                        makeTemp_4(),
                                        makeTemp_5())

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



        fun makeTemp_4() : PolygonData
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

        fun makeTemp_5() : PolygonData
        {
            var polygon: PolygonData = PolygonData()
            val point1 =
                    PointF(530f,
                           240f)
            val point2 =
                    PointF(350f,
                           160f)
            val point3 =
                    PointF(460f,
                           270f)
            val point4 =
                    PointF(650f,
                           740f)

            val point5 =
                    PointF(30f,
                           225f)// M42.3803,56.9069

            polygon.closed = true
            polygon.pathData.add(point1)
            polygon.pathData.add(point2)
            polygon.pathData.add(point3)
            polygon.pathData.add(point4)
            polygon.pathData.add(point5)

            polygon.fillColor = Color.YELLOW
            polygon.strokeColor = Color.parseColor("#658921")
            polygon.strokeLineCap = Paint.Cap.ROUND
            polygon.strokeWidth = 4.0f
            polygon.fillType = Path.FillType.EVEN_ODD

            return polygon
        }
    }
}

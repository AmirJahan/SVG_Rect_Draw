package com.oddinstitute.svgconvert

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF


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

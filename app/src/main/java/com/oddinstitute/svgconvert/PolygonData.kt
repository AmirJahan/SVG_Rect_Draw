package com.oddinstitute.svgconvert

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

class PolygonData
{
    var pathData: ArrayList<PointF> = arrayListOf()
    var fillColor = Color.BLACK
    var strokeColor = Color.TRANSPARENT
    var strokeWidth = 0.0f
    var strokeLineCap : Paint.Cap = Paint.Cap.ROUND

    var closed = true

    var fillType = Path.FillType.EVEN_ODD

}
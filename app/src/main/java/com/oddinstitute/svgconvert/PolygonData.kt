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
    var strokeLineCap : Paint.Cap = Paint.Cap.ROUND
    var strokeWidth = 0.0f
    var fillType = Path.FillType.EVEN_ODD
    var closed = true
}
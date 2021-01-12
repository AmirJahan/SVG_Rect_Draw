package com.oddinstitute.svgconvert

import android.graphics.Path
import android.graphics.PointF

fun Path.moveToPoint(point: PointF)
{
    this.moveTo(point.x,
                point.y)
}


package com.oddinstitute.svgconvert

import android.graphics.Path

class Polygon
{
    var data: PolygonData = PolygonData()
    var path: Path = Path()

    fun clearPath ()
    {
        this.path = Path ()
    }
}
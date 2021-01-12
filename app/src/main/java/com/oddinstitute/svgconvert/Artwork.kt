package com.oddinstitute.svgconvert

class Artwork
{
    var polygons : ArrayList<Polygon> = arrayListOf()

    fun clearPaths ()
    {
        for (i in 0 until this.polygons.count())
        {
            this.polygons[i].clearPath()
        }
    }
}
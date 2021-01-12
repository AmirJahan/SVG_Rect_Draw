package com.oddinstitute.svgconvert


import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/*
TODO:

makePathFor | is currently static. It has to read the actual file
The selection border is continues. We should make it dashed and also a different color depending of what is selected
Selection handles should literally say open artwork, closer artwork, open polygon, close polygon
Don’t draw the artwork selection handle in the corner editing mode
Show the layers of a single artwork
Show the various artworks in a layer system
We should allow for multiple polygon selection, multiple corner selection, multiple artwork selection
In corner mode, we should be able to drag the body of the polygon and move the entire polygon
When one artwork is selected, other artworks should get a tint color
When one polygon is selected, others should get a tint
When we are in artwork selection mode, the border should be one color, when we go to the polygon selection mode, the border of the ARTWORK should be the same color as the Artwork handle. Then, when we select a polygon, the border of the artwork should stay as a tint and the border of the polygon should now be the same color of the polygon handle. When we go to corner mode, the border of the polygon should become tinted and the same color as the polygon selection.
There should be a dashed line between the belie points.
Control points should have a different color
If we draw over empty, we should be able to select multiple polygons, artworks, or corners.
We should be able to rotate and scale, polygons and Artwork. But not a selection of the vertices.
We should be able to zoom on the screen with a button to go back to the normal situation.
*/

class MainActivity : AppCompatActivity()
{
    lateinit var boom : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boom = findViewById<FrameLayout>(R.id.canvasView)


        val temp_polygon_data = AppData.readPolygonsFromFile()


        val artwork1 = Artwork()

        for (i in 0 until 3)
        {
            val poly = Polygon()
            poly.data = temp_polygon_data[i]
            artwork1.polygons.add(poly)
        }

        val artwork2 = Artwork()

        for (i in 3..4)
        {
            val poly = Polygon()
            poly.data = temp_polygon_data[i]
            artwork2.polygons.add(poly)
        }


        val drawingView1 = DrawView(this,
                                    arrayListOf(artwork1,
                                                artwork2))

        boom.addView(drawingView1)
    }

}

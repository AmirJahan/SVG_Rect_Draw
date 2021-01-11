package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity





class MainActivity : AppCompatActivity()
{



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val boom = findViewById<FrameLayout>(R.id.canvasView)
        val poly1 = makePolygonFromFile(0.8f)
        val poly2 = makePolygonFromFile(1.8f)


        val polies = arrayListOf(poly1, poly2)

        val drawingView = DrawView(this, polies)
        boom.addView(drawingView)

        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener {

            drawingView.clear = true

            drawingView.invalidate()
            Toast.makeText(this, "removing", Toast.LENGTH_SHORT).show()
        }



        val newDrawingButton = findViewById<Button>(R.id.newDrawingButton)
        newDrawingButton.setOnClickListener {

            drawingView.clear = false

            drawingView.polygonsDataArray = polies


            drawingView.invalidate()
            Toast.makeText(this, "Adding", Toast.LENGTH_SHORT).show()
        }


        val goEditButton = findViewById<Button>(R.id.goEditButton)

//        goEditButton.setOnClickListener {
//            if (!drawingView.cornerEditingMode)
//            {
//                goEditButton.text = "Done"
//                Toast.makeText(this@MainActivity,
//                               "Edit",
//                               Toast.LENGTH_SHORT)
//                        .show()
//
//                drawingView.cornerEditingMode = true
//
//
//                drawingView.invalidate()
//            }
//            else
//            {
//                goEditButton.text = "Edit"
//                Toast.makeText(this@MainActivity,
//                               "Normal",
//                               Toast.LENGTH_SHORT)
//                        .show()
//
//                drawingView.cornerEditingMode = false
//
//
//                drawingView.invalidate()
//            }
//        }

        // or you could do WHAT WE DO



    }

    fun makePolygonFromFile (random: Float) : PolygonData
    {
        var polygon: PolygonData = PolygonData()
        val point1 =
                PointF(100f * random,
                       100f * random)
        val point2 =
                PointF(300f * random,
                       100f * random)
        val point3 =
                PointF(300f * random,
                       300f * random)
        val point4 =
                PointF(100f * random,
                       300f * random)

        polygon.corners.add(point1)
        polygon.corners.add(point2)
        polygon.corners.add(point3)
        polygon.corners.add(point4)



        return polygon
    }

}
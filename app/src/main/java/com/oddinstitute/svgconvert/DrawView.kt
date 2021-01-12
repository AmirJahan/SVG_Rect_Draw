package com.oddinstitute.svgconvert

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener




class DrawView : View
{
    var selectionMode: SelectionMode = SelectionMode.ARTWORK

    // list of all artworks in the view
    // this is the only holder of all Artworks in a scene
    var artworks: ArrayList<Artwork> = arrayListOf()

    var mainPaint: Paint = Paint()

    // selection borders and handles
    var artworkSelectionBorder: RectF? = null
    var polygonSelectionHandle: RectF? = null

    var polygonSelectionBorder: RectF? = null
    var cornerSelectionHandle: RectF? = null


    // Selections
    var selectedArtwork: Artwork? = null
    var selectedArtworkIndex = -1

    var selectedPolygon: Path? = null
    var selectedPolygonIndex = -1

    var selectedCorner: Path? = null
    var selectedCornerIndex: Int = -1

    // these are drawn temporarily
    var corners: ArrayList<Path>? = null

    // toch x and y start values
    var xStart = 0f
    var yStart = 0f

    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context?,
                artworks: ArrayList<Artwork>) : super(context)
    {
        this.artworks = artworks

        mainPaint = Paint()
        mainPaint.isAntiAlias = true

        makePaths()

        this.setOnTouchListener(onTouchListener())
    }

    fun artworkSelection(x: Float, y: Float)
    {
        // if we are in artwork selection mode, we should select the entirety of the artwork
        artworkLoop@ for (artwork in artworks)
        {
            // let's test each of the polygons
            for (i in 0 until artwork.polygons.count())
            {
                val polygon = artwork.polygons[i]
                val path = polygon.path

                val pathBounds = RectF()
                path.computeBounds(pathBounds,
                                   true)

                if (pathBounds.contains(x,
                                        y))
                {
                    // this is the first artwork found under the tap
                    // let's remember it and step out
                    selectedArtwork = artwork

                    selectedArtworkIndex = artworks.indexOf(artwork)

                    break@artworkLoop
                }
                else
                {
                    // if we reach else, it means, we landed on the view itself
                    selectedArtwork = null
                    selectedArtworkIndex = -1
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListener(): OnTouchListener?
    {
        return OnTouchListener { _, event ->

            val touchX = event.x
            val touchY = event.y

            when (event.action and MotionEvent.ACTION_MASK)
            {
                MotionEvent.ACTION_DOWN -> // select something
                {
                    xStart = event.rawX
                    yStart = event.rawY

                    when (selectionMode)
                    {
                        SelectionMode.ARTWORK ->
                        {
                            // no artwork selected
                            if (selectedArtworkIndex == -1)
                            {
                                artworkSelection(touchX,
                                                 touchY)
                            }
                            else
                            {
                                switchArtworkSelection(event)
                            }
                        }
                        SelectionMode.POLYGON ->
                        {
                            // no polygon selected
                            if (selectedPolygonIndex == -1)
                            {
                                polySelection(touchX,
                                              touchY)
                            }
                            else
                            {
                                switchPolySelection(event)
                            }
                        }
                        SelectionMode.CORNER ->
                        {
                            pickCorner(event)
                        }
                    }

                }

                MotionEvent.ACTION_MOVE ->
                {
                    val deltaX = event.rawX - xStart
                    val deltaY = event.rawY - yStart
                    xStart = event.rawX
                    yStart = event.rawY


                    when (selectionMode)
                    {
                        SelectionMode.ARTWORK ->
                        {
                            selectedArtwork?.let {
                                moveArtWorkTo(deltaX,
                                              deltaY)
                            }
                        }
                        SelectionMode.POLYGON ->
                        {
                            selectedPolygon?.let {
                                moveSelectedPolygon(deltaX,
                                                    deltaY)
                            }
                        }
                        SelectionMode.CORNER ->
                        {
                            selectedCorner?.let {
                                moveSelectedCorner(event)
                            }
                        }
                    }
                }

                MotionEvent.ACTION_UP ->
                {
                    // Just to exhaust the "when"
                }
            }

            this.invalidate()

            true
        }
    }

    fun pickCorner(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

        // We are in Corner Editing Mode, check if we tapped don the selection handle
        cornerSelectionHandle?.let {
            if (it.contains(touchX, touchY))
            {
                // we were in corner editing, tapped the selection handle
                // let's go back now to polygon mode
                selectionMode = SelectionMode.POLYGON
//                cornerSelectionMode = false
//                polygonSelectionMode = true
            }
        }

        // In corner Editing Mode, we didn't touch the handle
        // now, we have to deal with corners
        selectedPolygon?.let {
            corners?.let {
                for (i in 0 until it.count())
                {
                    val corner = it[i]
                    val pathBounds = RectF()

                    corner.computeBounds(pathBounds,
                                         true)

                    val outerInvisibleRect = RectF(pathBounds.left - 25f,
                                                   pathBounds.top - 25f,
                                                   pathBounds.right + 25f,
                                                   pathBounds.bottom + 25f)

                    if (outerInvisibleRect.contains(touchX,
                                                    touchY))
                    {
                        this.selectedCornerIndex = i
                        this.selectedCorner = it[i]
                        this.invalidate()
                        break
                    }

                    // we reached here, it means no corner was tapped on
                    this.selectedCornerIndex = -1
                    this.selectedCorner = null
                    this.invalidate()
                }
            }
        }
    }

    fun switchPolySelection(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

        cornerSelectionHandle?.let {
            if (it.contains(touchX,
                            touchY))
            {
                // Check if we touched a handle, let's go corner editing mode
                selectionMode = SelectionMode.CORNER
//                cornerSelectionMode = true
                // so no more polygon selection
//                polygonSelectionMode = false
            }
            else
            {
                // we haven't touched the handle
                // so, let's drop everything back to the normal polygon selection
                selectedPolygonIndex = -1
                selectedPolygon = null

                cornerSelectionHandle = null
                polygonSelectionBorder = null

                selectionMode = SelectionMode.POLYGON
//                polygonSelectionMode = true
                polySelection(touchX,
                              touchY)
            }
        }
    }


    fun switchArtworkSelection(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

        polygonSelectionHandle?.let {
            if (it.contains(touchX,
                            touchY))
            {
                // Check if we touched a handle, let's go polygon editing mode
//                polygonSelectionMode = true
                // so no more artwork selection
//                artworkSelectionMode = false

                selectionMode = SelectionMode.POLYGON
            }
            else
            {
                // we haven't touched the handle
                // so, let's drop everything back to the Artwork Selection Mode
                selectedArtworkIndex = -1
                selectedArtwork = null

                polygonSelectionHandle = null
                artworkSelectionBorder = null

                selectionMode = SelectionMode.ARTWORK
//                artworkSelectionMode = true
                artworkSelection(touchX,
                                 touchY)
            }
        }
    }

    fun moveSelectedPolygon(deltaX: Float, deltaY: Float)
    {
//        val pathData = this.artwork.polygons[selectedPolygonIndex].data.pathData
        selectedArtwork?.let {
            selectedPolygon?.let {
                val pathData = selectedArtwork!!.polygons[selectedPolygonIndex].data.pathData
                var newPathData: ArrayList<PointF> = arrayListOf()
                for (i in 0 until pathData.count())
                {
                    val corner = pathData[i]
                    val newCorner =
                            PointF(corner.x + deltaX,
                                   corner.y + deltaY)
                    newPathData.add(newCorner)
                }

                this.artworks[selectedArtworkIndex]
                        .polygons[selectedPolygonIndex]
                        .data
                        .pathData = newPathData
            }
        }
    }

    fun movePolygonAtIndex(index: Int, deltaX: Float, deltaY: Float)
    {
        selectedArtwork?.let {
            val pathData = selectedArtwork!!.polygons[index].data.pathData
            var newPathData: ArrayList<PointF> = arrayListOf()
            for (i in 0 until pathData.count())
            {
                val corner = pathData[i]
                val newCorner =
                        PointF(corner.x + deltaX,
                               corner.y + deltaY)
                newPathData.add(newCorner)
            }

            this.artworks[selectedArtworkIndex]
                    .polygons[index]
                    .data
                    .pathData = newPathData
        }
    }

    fun moveArtWorkTo(deltaX: Float, deltaY: Float)
    {
        selectedArtwork?.let {
            for (i in 0 until it.polygons.count())
            {
                movePolygonAtIndex(i,
                                   deltaX,
                                   deltaY)
            }
        }
    }

    fun moveSelectedCorner(event: MotionEvent)
    {
        val touchX = event.x
        val touchY = event.y

        val newCorner = PointF(touchX,
                               touchY)

        this.artworks[selectedArtworkIndex]
                .polygons[selectedPolygonIndex]
                .data
                .pathData[selectedCornerIndex] = newCorner
    }

    fun polySelection(x: Float, y: Float)
    {
        cornerSelectionHandle = null

        polygonSelectionHandle?.let {
            if (it.contains(x,
                            y))
            {
                // Check if we touched the artwork handle,
                    // if so let's go back to Artwork Selection Mode
                // so no more polygon selection

                selectionMode = SelectionMode.ARTWORK
            }
        }

        selectedArtwork?.let {
            for (i in 0 until it.polygons.count())
            {
                val polygon = it.polygons[i]
                val path = polygon.path

                val pathBounds = RectF()
                path.computeBounds(pathBounds,
                                   true)

                if (pathBounds.contains(x,
                                        y))
                {
                    selectedPolygonIndex = i
                    selectedPolygon = path

                    return
                }
            }
        }
    }

    fun cleanCanvas(mCanvas: Canvas)
    {
        // clear and refresh paths of the selected Artwork (ONLY)
        selectedArtwork?.let {
            it.clearPaths()
        }

        mainPaint.color = Color.TRANSPARENT
        mCanvas.drawPaint(mainPaint)
    }

    fun makeCornersFor(polygonData: PolygonData): ArrayList<Path>
    {
        val cornerSize: Float = 10f
        val cornerRadius: Float = 7f
        var cornersPaths: ArrayList<Path> = arrayListOf()

        for (corner in polygonData.pathData)
        {
            val cornerPoint = RectF(corner.x - cornerSize,
                                    corner.y + cornerSize,
                                    corner.x + cornerSize,
                                    corner.y - cornerSize)

            val cornerPath = Path()
            cornerPath.addRoundRect(cornerPoint,
                                    cornerRadius,
                                    cornerRadius,
                                    Path.Direction.CCW)

            cornersPaths.add(cornerPath)
        }

        return cornersPaths
    }

    fun makePathFor(data: PolygonData): Path
    {
        // Here, draw the MAIN Path
        var path = Path()
        path.fillType = Path.FillType.EVEN_ODD

        path.moveToPoint(data.pathData[0])
        path.lineToPoint(data.pathData[1])
        path.lineToPoint(data.pathData[2])
        path.lineToPoint(data.pathData[3])
        path.lineToPoint(data.pathData[4])
        path.close()

        return path
    }


    fun makeCorners()
    {
        selectedArtwork?.let {
            val polygon = it.polygons[selectedPolygonIndex]

            val polygonData = polygon.data
            corners = makeCornersFor(polygonData)
        }

    }

    fun makePaths()
    {
        for (artwork in artworks)
        {
            artwork.clearPaths()
        }

        // Make All Main Paths
        for (j in 0 until artworks.count())
        {
            val artwork = artworks[j]

            for (i in 0 until artwork.polygons.count())
            {
                val polygon = artwork.polygons[i]
                val path = makePathFor(polygon.data)
                polygon.path = path
                artwork.polygons[i] = polygon
            }
            artworks[j] = artwork
        }

        // if an artwork is selected, we should :
        selectedArtwork?.let {
            // 1) make selection  border
            makeArtworkSelectionBorder()

            // 2) make selection Handle
            makeArtworkSelectionHandle()
        }

        // if a polygon is selected, we should :
        selectedPolygon?.let {
            // 1) we make its corners
            makeCorners()

            // b) make selection  border
            makePolygonSelectionBorder()

            // c) make selection Handle
            makeCornerSelectionHandle()
        }

        // if we are in the corner selection mode and moved
        // we have to update the selected polygon
        // so that its new data is reflected
        if (selectionMode == SelectionMode.CORNER)
        {
            selectedPolygon = selectedArtwork!!.polygons[selectedPolygonIndex].path
        }
    }

    fun drawArtworks(canvas: Canvas)
    {
        for (artwork in artworks)
        {
            for (each in artwork.polygons)
            {
                val path = each.path
                val data = each.data
                styleFillPaint(data)
                canvas.drawPath(path,
                                mainPaint)

                styleBorderPaint(data)
                canvas.drawPath(path,
                                mainPaint)
            }
        }
    }

    fun drawArtworkSelectionBorder(canvas: Canvas)
    {
        selectedArtwork?.let {
            artworkSelectionBorder?.let {
                styleSelectionBorderPaint()
                canvas.drawRoundRect(it,
                                     8f,
                                     8f,
                                     mainPaint)
            }
        }
    }

    fun drawArtworkSelectionHandle(canvas: Canvas)
    {
        selectedArtwork?.let {

            polygonSelectionHandle?.let {
                styleNormalSelectionHandlePaint()

                // if we are drawing this handle, and we have moved into the
                // polygon selection, let's draw it highlighted
                if (selectionMode == SelectionMode.POLYGON)
                    styleHighlightedSelectionHandlePaint()

                canvas.drawRoundRect(it,
                                     8f,
                                     8f,
                                     mainPaint)
            }
        }
    }

    fun drawPolygonSelectionHandle(canvas: Canvas)
    {
        // we should draw the handle for corners
        cornerSelectionHandle?.let {
            styleNormalSelectionHandlePaint()

            if (selectionMode == SelectionMode.CORNER)
                styleHighlightedSelectionHandlePaint()

            canvas.drawRoundRect(it,
                                 8f,
                                 8f,
                                 mainPaint)
        }

    }

    fun drawPolygonSelectionBorder(canvas: Canvas)
    {
        polygonSelectionBorder?.let {
            styleSelectionBorderPaint()
            canvas.drawRoundRect(it,
                                 8f,
                                 8f,
                                 mainPaint)
        }
    }


    override fun onDraw(canvas: Canvas)
    {
        cleanCanvas(canvas)

        // if a polygon is selected, we will update only that one.
        // otherwise, we will update everyone
        makePaths()

        // drawing all paths
        drawArtworks(canvas)

        when (selectionMode)
        {
            SelectionMode.ARTWORK ->
            {
                drawArtworkSelectionBorder(canvas)
                drawArtworkSelectionHandle(canvas)
            }

            SelectionMode.POLYGON ->
            {
                // we are now in polygon selection mode
                // but we don't know if there is a polygon selected or not
                if (selectedPolygonIndex != -1)
                {
                    // there is a polygon selected
                    // so let's only draw the polygon stuff
                    drawPolygonSelectionBorder(canvas)
                    drawPolygonSelectionHandle(canvas)
                }
                else
                {
                    drawArtworkSelectionBorder(canvas)
                }
                // in any case, draw the artwork selection handle
                drawArtworkSelectionHandle(canvas)
            }

            SelectionMode.CORNER ->
            {
                // if we are in the corner mode, we should draw
                // a) the corners,
                // b) the polygon selection/ handle, and
                // c) artwork handle

                drawCorners(canvas)
                drawPolygonSelectionHandle(canvas)
                drawPolygonSelectionBorder(canvas)
                drawArtworkSelectionHandle(canvas)
            }
        }
    }

    fun drawCorners(canvas: Canvas)
    {
        corners.let {
            for (i in 0 until corners!!.count())
            {
                val corner = corners!![i]
                styleNormalCornerPaint()

                selectedCorner?.let {
                    if (i == selectedCornerIndex)
                        styleSelectedCornerPaint()
                }

                canvas.drawPath(corner,
                                mainPaint)
            }
        }
    }

    fun makePolygonSelectionBorder()
    {
        // DRAW THE BORDER
//        val path = artwork.polygons[selectedPolygonIndex].path

        selectedArtwork?.let {
            val path = it.polygons[selectedPolygonIndex].path

            val pathBounds = RectF()
            path.computeBounds(pathBounds,
                               true)

            val rectf = RectF(pathBounds.left - 10f,
                              pathBounds.top - 10f,
                              pathBounds.right + 10f,
                              pathBounds.bottom + 10f)
            polygonSelectionBorder = rectf
        }
    }


    fun calculateArtworkBoundingBox(): RectF
    {
        var pathBoundsArr = ArrayList<RectF>()

        // let's first add all rects in the array
        selectedArtwork?.let {
            for (i in 0 until it.polygons.count())
            {
                val pathBounds = RectF()
                val path = it.polygons[i].path
                path.computeBounds(pathBounds,
                                   true)

                pathBoundsArr.add(pathBounds)
            }
        }

        // then we intersect all the paths
        var finalPathBounds = RectF()
        for (any in pathBoundsArr)
        {
            finalPathBounds.union(any)
        }

        return finalPathBounds
    }

    fun makeArtworkSelectionBorder()
    {
        // DRAW THE BORDER
        val finalPathBounds = calculateArtworkBoundingBox()

        val rectf = RectF(finalPathBounds.left - 10f,
                          finalPathBounds.top - 10f,
                          finalPathBounds.right + 10f,
                          finalPathBounds.bottom + 10f)

        println(rectf)
        artworkSelectionBorder = rectf
    }

    fun makeCornerSelectionHandle()
    {
        if (selectedPolygonIndex == -1)
            cornerSelectionHandle = null

//        val path = artwork.polygons[selectedPolygonIndex].path

        selectedArtwork?.let {
            val path = it.polygons[selectedPolygonIndex].path

            val pathBounds = RectF()
            path.computeBounds(pathBounds,
                               true)

            val height = pathBounds.bottom - pathBounds.top
            val rectf = RectF(pathBounds.right + 20f,
                              pathBounds.top + (height / 2f) - 25f,
                              pathBounds.right + 70f,
                              pathBounds.top + (height / 2f) + 25f)

            cornerSelectionHandle = rectf
        }

    }


    fun makeArtworkSelectionHandle()
    {
        val finalPathBounds = calculateArtworkBoundingBox()

        val height = finalPathBounds.bottom - finalPathBounds.top
        val rectf = RectF(finalPathBounds.right + 20f,
                          finalPathBounds.top /* + (height / 2f) - 25f */,
                          finalPathBounds.right + 70f,
                          finalPathBounds.top + /*(height / 2f) +  25f */ 50f)

        polygonSelectionHandle = rectf
    }

    fun styleNormalCornerPaint()
    {
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 5.0f
        mainPaint.color = Color.YELLOW
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleSelectedCornerPaint()
    {
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 5.0f
        mainPaint.color = Color.RED
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleBorderPaint(polygonData: PolygonData)
    {
        // stroke
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = polygonData.strokeWidth
        mainPaint.color = polygonData.strokeColor
        mainPaint.strokeCap = polygonData.strokeLineCap
    }

    fun styleSelectionBorderPaint()
    {
        // stroke
        mainPaint.style = Paint.Style.STROKE
        mainPaint.strokeWidth = 6f
        mainPaint.color =
                Color.rgb(0,
                          0,
                          153)
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleNormalSelectionHandlePaint()
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.DKGRAY
        mainPaint.strokeCap = Paint.Cap.ROUND
    }

    fun styleHighlightedSelectionHandlePaint()
    {

        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = Color.RED
        mainPaint.strokeCap = Paint.Cap.ROUND

    }

    fun styleFillPaint(polygonData: PolygonData)
    {
        // fill
        mainPaint.style = Paint.Style.FILL
        mainPaint.color = polygonData.fillColor
    }
}


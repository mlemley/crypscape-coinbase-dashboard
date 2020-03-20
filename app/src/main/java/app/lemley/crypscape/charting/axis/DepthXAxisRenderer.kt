package app.lemley.crypscape.charting.axis

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class DepthXAxisRenderer constructor(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    private val limitLineSegmentsBuffer = FloatArray(4)
    private val limitLinePath = Path()

    override fun renderLimitLineLine(c: Canvas, limitLine: LimitLine, position: FloatArray) {
        limitLineSegmentsBuffer[0] = position[0]
        limitLineSegmentsBuffer[1] = mViewPortHandler.contentBottom() / 3
        limitLineSegmentsBuffer[2] = position[0]
        limitLineSegmentsBuffer[3] = mViewPortHandler.contentBottom()
        limitLinePath.reset()
        limitLinePath.moveTo(limitLineSegmentsBuffer[0], limitLineSegmentsBuffer[1])
        limitLinePath.lineTo(limitLineSegmentsBuffer[2], limitLineSegmentsBuffer[3])
        mLimitLinePaint.style = Paint.Style.STROKE
        mLimitLinePaint.color = limitLine.lineColor
        mLimitLinePaint.strokeWidth = limitLine.lineWidth
        mLimitLinePaint.pathEffect = limitLine.dashPathEffect
        c.drawPath(limitLinePath, mLimitLinePaint)
    }
}
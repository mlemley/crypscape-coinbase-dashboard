package app.lemley.crypscape.extensions

import android.graphics.Paint
import app.lemley.crypscape.R
import app.lemley.crypscape.charting.axis.XAxisFormatter
import app.lemley.crypscape.charting.axis.YAxisFormatter
import app.lemley.crypscape.extensions.app.persistance.visibleXRange
import app.lemley.crypscape.extensions.app.persistance.xAxisLabelCount
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.renderer.CombinedChartRenderer


fun CombinedChart.configureForCrypScape(candleGranularity: Granularity) {
    //setDescription(description)
    description.isEnabled = false
    setBackgroundColor(context.getColor(R.color.chart_background))
    setDrawGridBackground(false)
    isAutoScaleMinMaxEnabled = true
    setDrawBarShadow(false)
    isHighlightFullBarEnabled = false
    renderer = CombinedChartRenderer(this, this.animator, this.viewPortHandler)
    setVisibleXRangeMaximum(candleGranularity.visibleXRange(context))

    // draw bars behind lines
    this.drawOrder = arrayOf(
        CombinedChart.DrawOrder.BAR,
        CombinedChart.DrawOrder.LINE,
        CombinedChart.DrawOrder.CANDLE,
        CombinedChart.DrawOrder.SCATTER
    )

    isHighlightPerDragEnabled = true
    setDrawBorders(false)
    setBorderColor(context.getColor(R.color.candlestick_markers))
    isAutoScaleMinMaxEnabled = false
    animateY(300, Easing.EasingOption.EaseInElastic)
    requestDisallowInterceptTouchEvent(true)
    resetZoom()
    setViewPortOffsets(100f, 0f, 0f, 50f)

    axisRight.apply {
        isEnabled = false
        setDrawGridLines(false)
        setDrawLabels(false)
        setDrawAxisLine(false)
        setDrawZeroLine(false)
    }

    axisLeft.apply {
        textColor = context.getColor(R.color.candlestick_labels)
        spaceBottom = 10F
        spaceTop = 10F
        setDrawGridLines(true)
        setDrawLabels(true)
        setDrawAxisLine(false)
        setDrawZeroLine(false)
        setDrawLimitLinesBehindData(false)
        labelCount = 19
        valueFormatter = YAxisFormatter()
        gridColor = context.getColor(R.color.candlestick_markers)
    }

    xAxis.apply {
        setDrawGridLines(true)// disable x axis grid lines
        gridColor = context.getColor(R.color.candlestick_markers)
        setDrawLabels(true)
        setDrawAxisLine(false)
        position = XAxis.XAxisPosition.BOTTOM
        textColor = context.getColor(R.color.candlestick_labels)
        this.granularity = 1f
        spaceMax = 1.5F
        textSize = 8f
        labelCount = candleGranularity.xAxisLabelCount(context)
        valueFormatter = XAxisFormatter(candleGranularity)
        setDrawLimitLinesBehindData(true)
        setAvoidFirstLastClipping(true)
    }

    legend.apply {
        isEnabled = false
    }
}


fun CandleDataSet.configureForCrypScape(chart: CombinedChart) {
    color = chart.context.getColor(R.color.candlestick_set_color)
    shadowWidth = 0.8f
    decreasingColor = chart.context.getColor(R.color.candlestick_decreasing)
    decreasingPaintStyle = Paint.Style.FILL_AND_STROKE
    increasingColor = chart.context.getColor(R.color.candlestick_increasing)
    increasingPaintStyle = Paint.Style.FILL_AND_STROKE
    shadowColorSameAsCandle = true
    neutralColor = chart.context.getColor(R.color.candlestick_neutral)
    setDrawValues(false)
    axisDependency = YAxis.AxisDependency.LEFT

}

package app.lemley.crypscape.ui.main

import app.lemley.crypscape.ui.market.MarketChartingManager
import org.koin.dsl.module


val mainScreenModule = module {
    factory { Configuration() }

    // View Managers
    factory { MarketChartingManager(get()) }
}
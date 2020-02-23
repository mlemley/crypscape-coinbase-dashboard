package app.lemley.crypscape.charting

import org.koin.dsl.module


val chartingModule = module {
    factory { ChartRenderer() }
}
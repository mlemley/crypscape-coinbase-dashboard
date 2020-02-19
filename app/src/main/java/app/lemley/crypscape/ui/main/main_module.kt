package app.lemley.crypscape.ui.main

import org.koin.dsl.module


val mainScreenModule = module {
    factory { Configuration() }
}
package app.lemley.crypscape.extensions.app.persistance

import app.lemley.crypscape.persistance.entities.Product


val Product.baseCurrencyLabel: String get() = serverId.split("-")[0]

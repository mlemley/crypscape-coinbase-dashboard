package app.lemley.crypscape.ui.main

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import app.lemley.crypscape.R


class Configuration {

    fun configureWithDrawer(drawerLayout: DrawerLayout): AppBarConfiguration = AppBarConfiguration(
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setOf(
            R.id.nav_market
        ), drawerLayout
    )
}

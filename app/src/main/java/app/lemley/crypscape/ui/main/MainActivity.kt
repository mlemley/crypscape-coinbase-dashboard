package app.lemley.crypscape.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.lemley.crypscape.R
import app.lemley.crypscape.extensions.app.withView
import com.google.android.material.navigation.NavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val configuration: Configuration by inject()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        appBarConfiguration = configuration.configureWithDrawer(withView(R.id.drawer_layout))
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_market -> supportActionBar?.hide()
                else -> supportActionBar?.show()
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        withView<NavigationView>(R.id.navigation).setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}

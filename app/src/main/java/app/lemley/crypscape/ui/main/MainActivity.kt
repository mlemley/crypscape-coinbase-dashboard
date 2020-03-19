package app.lemley.crypscape.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.lemley.crypscape.R
import app.lemley.crypscape.databinding.ActivityMainBinding
import app.lemley.crypscape.extensions.app.withView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val configuration: Configuration by inject()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binder: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binder.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        appBarConfiguration = configuration.configureWithDrawer(withView(R.id.drawer_layout))
        findNavController(R.id.nav_host_fragment).apply {
            addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.nav_market -> supportActionBar?.hide()
                    else -> supportActionBar?.show()
                }
            }
            setupActionBarWithNavController(this, appBarConfiguration)
            binder.navigation.setupWithNavController(this)
            withView<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment)
        .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}

package co.tddl.mylga

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import co.tddl.mylga.adapter.TabPagerAdapter
import co.tddl.mylga.location.GeocoderHelper
import co.tddl.mylga.location.MY_PERMISSIONS_REQUEST_LOCATION
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoderHelper: GeocoderHelper
    private lateinit var sharedPref:SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        toolbar.title = "Home"

        geocoderHelper = GeocoderHelper()
        sharedPref = SharedPreferenceHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val tabLayout = tab_layout as TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Your Feed"))
        tabLayout.addTab(tabLayout.newTab().setText("Around You"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val viewPager = pager as ViewPager
        val adapter = TabPagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        addClickListeners()
        checkLocationPermission()

    }

    private fun addClickListeners(){
        val headerView = nav_view.getHeaderView(0)
        val profilell = headerView.findViewById<LinearLayout>(R.id.profile_linear_layout)
        val settingsiv = headerView.findViewById<ImageView>(R.id.image_view_settings)

        profilell.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }
        settingsiv.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(sharedPref.lastLocationUpdatedOverOneDay()) {
                // try and get location if its been two days since the user updated locatiob
                getLocationAddress()
            }
        }else{
            // Request for permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAddress(){
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                val geocoderHelper = GeocoderHelper()
                val address = geocoderHelper.getAddressFromLocation(location.latitude, location.longitude, applicationContext)
                // Save address in shared preferences
                val sharedPreferenceHelper = SharedPreferenceHelper(this)
                sharedPreferenceHelper.setLastLocation(address)
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // save location in preference
            getLocationAddress()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            /*R.id.nav_camera -> {
            }*/
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

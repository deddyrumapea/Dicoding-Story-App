package com.romnan.dicodingstory.features.storiesMap

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.Story
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoriesMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: StoriesMapViewModel by viewModels()

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.stories_map)
        setContentView(R.layout.activity_stories_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val latLngIndonesia = LatLng(0.7893, 113.9213)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngIndonesia))

        enableMyLocation()
        setMapStyle()

        viewModel.storiesList.observe(this) { storiesList -> addStoriesMarkers(storiesList) }
    }

    private fun addStoriesMarkers(storiesList: List<Story>) {
        for (story in storiesList) {
            val latLng = LatLng(
                story.lat ?: continue,
                story.lon ?: continue
            )

            val marker = MarkerOptions()
                .position(latLng)
                .title(story.name)
                .snippet(story.description)

            mMap.addMarker(marker)
        }
    }

    private fun setMapStyle() {
        try {
            val mapStyle = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            val success = mMap.setMapStyle(mapStyle)
            if (!success) Toast.makeText(
                this,
                getString(R.string.em_loading_map_style),
                Toast.LENGTH_LONG
            ).show()
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.em_loading_map_style),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) enableMyLocation()
        }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stories_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.mi_satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.mi_terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.mi_hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
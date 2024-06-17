package com.example.gpstest

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gpstest.databinding.ActivityMapsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapsBinding
    private var mapView: MapView? = null
    private var mapController = mapView?.controller
    private lateinit var markerInfoLayout: LinearLayout
    private lateinit var markerTitleTextView: TextView
    private lateinit var markerDescriptionTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_maps)
        settingsOsm()
        binding = ActivityMapsBinding.inflate(layoutInflater)

        mapView = findViewById(R.id.mapView)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)


        mapController?.setZoom(15.0)
        mapController?.setCenter(GeoPoint(55.751244, 37.618423))
        //map.mapCenter(GeoPoint(55.751244, 37.618423), 13.0)

        addMarker(55.751244, 37.618423, "Marker 1")
        addMarker(55.749, 37.615, "Marker 2")
        addMarker(55.753, 37.620, "Marker 3")

        val centerOnLocationButton = findViewById<ImageButton>(R.id.centerOnLocationButton)
        centerOnLocationButton.setOnClickListener {
            centerOnCurrentLocation()
        }
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val marker = org.osmdroid.views.overlay.Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.title = title
        marker.setOnMarkerClickListener { _, _ ->
            showBottomSheet(title)
            true
        }
        mapView?.overlays?.add(marker)
    }

    @SuppressLint("MissingPermission")
    private fun centerOnCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            mapController?.setCenter(GeoPoint(location.latitude, location.longitude))
        }
    }

    private fun showBottomSheet(title: String) {

    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            this, this
                .getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun zoomIn() {
        findViewById<ImageButton>(R.id.plus).setOnClickListener() {
            mapController?.zoomIn()
        }
    }


    private fun zoomOut() {
        findViewById<ImageButton>(R.id.minus).setOnClickListener() {
            mapController?.zoomOut()
        }
    }



fun clickOnMarker() {
    mapView?.overlays?.forEach { overlay ->
        if (overlay is Marker) {
            overlay.infoWindow = object : BasicInfoWindow(R.layout.marker_info_window, mapView) {
                override fun onOpen(item: Any?) {
                    markerTitleTextView.text = (item as Marker).title
                    markerDescriptionTextView.text = (item as Marker).position.toString()
                    markerInfoLayout.visibility = View.VISIBLE
                }

                override fun onClose() {
                    markerInfoLayout.visibility = View.GONE
                }
            }
        }
    }
}
}

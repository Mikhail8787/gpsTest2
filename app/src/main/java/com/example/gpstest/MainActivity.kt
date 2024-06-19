package com.example.gpstest

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.PointerIcon
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gpstest.databinding.ActivityMapsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

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
        initOsm()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setOnClick()


        mapView = findViewById(R.id.mapView)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)

        settingsOsm()
        initOsm()

        mapController = mapView?.controller as MapController


        mapController?.setZoom(15.0)
        mapController?.animateTo(GeoPoint(55.751244, 37.618423))


        addMarker(55.751244, 37.618423, "Илья")
        addMarker(55.749, 37.615, "Рома")
        addMarker(55.753, 37.620, "Вася")
        setOnClick()
        clickOnMarker()
        zoomOut()
        zoomIn()




        val centerOnLocationButton = findViewById<ImageButton>(R.id.centerOnLocationButton)
        centerOnLocationButton.setOnClickListener {
            centerMarker()
        }

    }




    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val marker = org.osmdroid.views.overlay.Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.icon= ContextCompat.getDrawable(this, R.drawable.ic_mylocation_55dp4)
        marker.title = title
        marker.setOnMarkerClickListener { _, _ ->
            showBottomSheet(title)
            true
        }
        mapView?.overlays?.add(marker)
        mapView?.invalidate()
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

        Toast.makeText(this, "$title\n GPS", Toast.LENGTH_SHORT).show()


    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            this, this
                .getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun initOsm() = with(binding){
        mapController?.setZoom(20.0)
        mapController?.setCenter(GeoPoint(55.751244, 37.618423))
        mapController?.animateTo(GeoPoint(55.751244, 37.618423))
        val mLocProvider = GpsMyLocationProvider(this@MainActivity)



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

    private fun setOnClick() = with(binding){
        val listener = onClicks()
        centerOnLocationButton.setOnClickListener(listener)
    }

    private fun onClicks(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                R.id.centerOnLocationButton -> centerMarker()
            }
        }
    }

    private fun centerMarker(){
        binding.mapView.controller.animateTo(GeoPoint(55.751244, 37.618423), 15.0, 1)


    }
}

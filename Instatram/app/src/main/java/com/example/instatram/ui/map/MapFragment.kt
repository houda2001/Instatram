package com.example.instatram.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.instatram.gridDisplay.PhotosActivity
import com.example.instatram.R
import com.example.instatram.ui.home.STATION_ID
import com.example.instatram.ui.home.STATION_NAME
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private lateinit var mapViewModel: MapViewModel

    val mMarkerMap: MutableMap<String, Int> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate and return the layout
        val v: View = inflater.inflate(
            R.layout.fragment_map, container,
            false
        )
        mMapView = v.findViewById<View>(R.id.mapView) as MapView
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        mMapView!!.getMapAsync {
            googleMap = it
            it.setOnMarkerClickListener(this)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.37, 2.13), 11.0F))
            val bitmapTramMarker =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_tram_marker)
                    ?.toBitmap()

            mapViewModel.stationData.observe(viewLifecycleOwner, {
                for (station in it) {
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(station.lat, station.lon))
                            .title(station.name)
                            .snippet(station.connections)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmapTramMarker))
                    )
                    if (marker != null) {
                        mMarkerMap[marker.id] = station.id
                    }
                }
            })
        }

        // Perform any camera updates here
        return v
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {

        val intent = Intent(activity, PhotosActivity::class.java).apply {
            if (p0 != null) {
                putExtra(STATION_ID, mMarkerMap[p0.id])
                putExtra(STATION_NAME, p0.title)
            }
        }
        startActivity(intent)
        return false
    }

}
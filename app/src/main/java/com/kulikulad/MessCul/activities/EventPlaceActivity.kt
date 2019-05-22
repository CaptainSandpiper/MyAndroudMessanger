package com.kulikulad.MessCul.activities

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kulikulad.MessCul.R

class EventPlaceActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap;
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var lastLocation: Location

    //everytime
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    var meetingCoordinate: LatLng? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                //placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        createLocationRequest();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.select_place_menu, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if(item != null)
        {
            if(item.itemId == R.id.okButton && meetingCoordinate != null)
            {
                Toast.makeText(this, "PLACE SELECTED", Toast.LENGTH_LONG).show()
                var returnIntent = this.intent;
                returnIntent.putExtra("selectedPlaceLat", meetingCoordinate!!.latitude);
                returnIntent.putExtra("selectedPlaceLng", meetingCoordinate!!.longitude);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            else
            {
                Toast.makeText(this, "SELECT PLACE", Toast.LENGTH_LONG).show()
            }
        }

        return true;
    }

    override fun onMapClick(coordinats: LatLng?) {

        Toast.makeText(this,"AAAAAAAAAAA",Toast.LENGTH_LONG).show()
        meetingCoordinate = coordinats!!;
        map.clear();
        map.addMarker(MarkerOptions().position(coordinats!!).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinats))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true;
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this);

        // Add a marker in Sydney and move the camera
//        val myPlace = LatLng(40.73, -73.99)
//        map.addMarker(MarkerOptions().position(myPlace).title("New Your City"))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))

        setUpMap();
    }

    private fun setUpMap()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        map.isMyLocationEnabled = true;

        fusedLocationClient.lastLocation.addOnSuccessListener(this){

                location ->
            if(location != null)
            {
                lastLocation = location
                var currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
            }
        }
    }

    private fun startLocationUpdates()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private fun createLocationRequest()
    {
        locationRequest = LocationRequest();
        locationRequest.interval = 10000;
        locationRequest.fastestInterval = 5000;
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this);
        val task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener {
            locationUpdateState = true;
            startLocationUpdates();
        }

        task.addOnFailureListener{
                err ->
            if(err is ResolvableApiException)
            {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try
                {
                    err.startResolutionForResult(this@EventPlaceActivity, REQUEST_CHECK_SETTINGS)
                }
                catch(sendEx: IntentSender.SendIntentException)
                {
                    //something to do
                }

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }
}

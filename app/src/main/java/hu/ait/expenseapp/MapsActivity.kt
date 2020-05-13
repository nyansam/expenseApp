package hu.ait.expenseapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import hu.ait.expenseapp.data.CodeResult
import hu.ait.expenseapp.data.CountryResult
import hu.ait.expenseapp.data.MoneyResult
import hu.ait.expenseapp.network.CodeAPI
import hu.ait.expenseapp.network.CountryAPI
import hu.ait.expenseapp.network.MoneyAPI
import kotlinx.android.synthetic.main.activity_maps.*

import kotlinx.android.synthetic.main.activity_scrolling.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    MyLocationProvider.OnNewLocationAvailable {

    private lateinit var mMap: GoogleMap
    private lateinit var myLocationProvider: MyLocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestNeededPermission()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofit2 = Retrofit.Builder()
            .baseUrl("http://api.geonames.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofit3 = Retrofit.Builder()
            .baseUrl("https://restcountries.eu")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val currencyAPI = retrofit.create(MoneyAPI::class.java)

        val countAPI = retrofit2.create(CountryAPI::class.java)

        val codeAPI = retrofit3.create(CodeAPI::class.java)

        btnFind.setOnClickListener {
            val countryCall = countAPI.getCountry(tvLocationLat.text.toString(), tvLocationLng.text.toString(), "syan2")

            countryCall.enqueue(object : Callback<CountryResult> {
                override fun onFailure(call: Call<CountryResult>, t: Throwable) {
                    tvErrorHandle.text = t.message
                }

                override fun onResponse(
                    call: Call<CountryResult>,
                    response: Response<CountryResult>
                ) {
                    var countryResult = response.body()

                    tvLocationName.text = "${countryResult?.countryName}"
                }
            })

            val codeCall = codeAPI.getCode(tvLocationName.text.toString())

            codeCall.enqueue(object: Callback<List<CodeResult>> {
                override fun onFailure(call: Call<List<CodeResult>>, t: Throwable) {
                    tvErrorHandle.text = t.message
                }

                override fun onResponse(
                    call: Call<List<CodeResult>>,
                    response: Response<List<CodeResult>>
                ) {
                    var codeResult = response.body()


                    tvLocationCurr.text = "${codeResult?.get(0)?.currencies?.get(0)?.code}"
                }
            })




            val moneyCall = currencyAPI.getMoney(tvLocationCurr.text.toString())

            moneyCall.enqueue(object : Callback<MoneyResult> {
                override fun onFailure(call: Call<MoneyResult>, t: Throwable) {
                    tvErrorHandle.text = t.message
                }

                override fun onResponse(call: Call<MoneyResult>, response: Response<MoneyResult>) {
                    var moneyResult = response.body()

                    tvLocationRate.text =  "${moneyResult?.rates?.USD}"
                }
            })






        }


    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                Toast.makeText(this, addressData.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    this,
                    "I need it for location", Toast.LENGTH_SHORT
                ).show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        } else {
            startLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION perm granted", Toast.LENGTH_SHORT)
                        .show()

                    startLocation()
                } else {
                    Toast.makeText(
                        this,
                        "ACCESS_FINE_LOCATION perm NOT granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun startLocation() {
        myLocationProvider = MyLocationProvider(
            this, this
        )
        myLocationProvider.startLocationMonitoring()
    }

    override fun onStop() {
        super.onStop()
        myLocationProvider.stopLocationMonitoring()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true


        mMap.setOnMapClickListener {
        }

        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener{
            override fun onMarkerClick(marker: Marker?): Boolean {
                Toast.makeText(this@MapsActivity, marker?.title, Toast.LENGTH_LONG).show()

                return true
            }
        })

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragEnd(marker: Marker?) {
                Toast.makeText(this@MapsActivity, "DRAG ENDED", Toast.LENGTH_LONG).show()
            }

            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }
        })
    }

    var prevLocation: Location? = null
    var distance: Float = 0f

    override fun onNewLocation(location: Location) {
        if (location.accuracy < 25) {
            if (prevLocation != null) {
                if (location.distanceTo(prevLocation)>3) {
                    distance += location.distanceTo(prevLocation)
                }
            }
            prevLocation = location
        }

        tvLocationLng.text = "${location.longitude}"
        tvLocationLat.text = "${location.latitude}"


        mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
    }
}

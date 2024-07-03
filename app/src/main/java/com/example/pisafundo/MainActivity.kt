package com.example.pisafundo

import android.R.attr.value
import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pisafundo.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private val timer = Timer();
    private lateinit var locationManager: LocationManager;

    data class PositionDataClass(
        val lat: Double, val long: Double
    )

    data class PositionsDataClass(
        val oldPosition: PositionDataClass?, val actualPosition: PositionDataClass?
    )

    var positions = PositionsDataClass(null, null);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager;

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                getPositions();
                val actualSpeed = calcActualSpeed();

                runOnUiThread {
                    binding.actualSpeedTxt.text = actualSpeed.toInt().toString();
                }
            }
        }, 0, 1000);
    }

    override fun onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @SuppressLint("MissingPermission")
    fun getPositions() {
        val oldPosition = positions.actualPosition;

        // Get the actual position
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(
                LocationManager.GPS_PROVIDER,
                null,
                mainExecutor
            ) { location ->
                positions = PositionsDataClass(
                    oldPosition,
                    PositionDataClass(location.latitude, location.longitude)
                );
            }
        } else {
            return;
        };
    }

    fun calcActualSpeed(): Double {
        if (positions.oldPosition == null || positions.actualPosition == null) {
            return 0.0;
        }

        val distance = calcDistance(positions.oldPosition!!, positions.actualPosition!!);
        return distance;
    }

    fun calcDistance(oldPosition: PositionDataClass, finalPosition: PositionDataClass): Double {
        val earthRadius = 6371e3 // Earth radius in meters

        val dLat = Math.toRadians(finalPosition.lat - oldPosition.lat)
        val dLon = Math.toRadians(finalPosition.long - oldPosition.long)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(oldPosition.lat)) * cos(Math.toRadians(finalPosition.lat)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c;
    }
}
package com.example.pisafundo

import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pisafundo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity(), View.OnClickListener {

    private val permissionsToAsk: Array<String> = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    private lateinit var binding: ActivitySplashBinding;
    private lateinit var locationManager: LocationManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.btnPermitir.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager;
        var isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGpsEnabled) {
            binding.btnPermitir.visibility = View.VISIBLE;
        } else {
            binding.btnPermitir.visibility = View.GONE;
        }

        continueToMainActivity();
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnPermitir -> {
                onClickAskPermissions();
            }
            R.id.btnContinue -> {
                continueToMainActivity();
            }
        }
    }

    private fun onClickAskPermissions() {
        askPermission();
        binding.btnContinue.visibility = View.VISIBLE;
    }

    private fun continueToMainActivity() {
        val isPermissionGranted = validatePermissions();
        if (isPermissionGranted) {
            navigateToMainActivity();
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(this, permissionsToAsk, 0);
    }

    private fun validatePermissions(): Boolean {
        var permissionsRefused: Array<Boolean> = arrayOf();

        for (permission in permissionsToAsk) {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;

            if (!isPermissionGranted) {
                permissionsRefused += false;
            }
        }

        return permissionsRefused.isEmpty()
    }

    private fun navigateToMainActivity() {
        val intentMainActivity = Intent(this, MainActivity::class.java);
        startActivity(intentMainActivity);
        finish(); // fecha a tela atual, não permitindo que o usuário entre nela novamente;
    }
}
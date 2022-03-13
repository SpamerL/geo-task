package com.spamerl.geo_task.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.spamerl.geo_task.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.Nav_Host_Fragment) as NavHostFragment? ?: return
        navController = host.navController

        checkAppPermissions()
    }

    private val PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val granted = it.value
            val permission = it.key
            Log.e("DEBUG", "${it.key} = ${it.value}")
        }
    }

    private fun checkAppPermissions() {
        PERMISSIONS_REQUIRED.forEach { permission ->
            if (ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_DENIED) {
                requestMultiplePermissions.launch(PERMISSIONS_REQUIRED)
                return
            }
        }
    }
}

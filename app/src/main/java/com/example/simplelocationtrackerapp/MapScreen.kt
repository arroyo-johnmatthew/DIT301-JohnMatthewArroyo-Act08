package com.example.simplelocationtrackerapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasPermissions by remember { mutableStateOf(locationManager.hasLocationPermissions()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Camera position for the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }
    
    // Marker state for current location
    val markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    
    // Request permissions on first launch
    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    // Get initial location and start updates
    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            isLoading = true
            
            // Get last known location
            locationManager.getLastKnownLocation { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                    markerState.position = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                    isLoading = false
                } ?: run {
                    isLoading = false
                }
            }
            
            // Start location updates
            locationManager.startLocationUpdates { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                currentLocation = latLng
                markerState.position = latLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                isLoading = false
            }
        }
    }
    
    Scaffold { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasPermissions) {
                if (currentLocation != null) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = markerState,
                            title = "Current Location",
                            snippet = "You are here"
                        )
                    }
                } else if (isLoading) {
                    Text(
                        text = "Getting location...",
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Location not available\nPlease check GPS settings",
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "Location permission required\nPlease grant permission to use this app",
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

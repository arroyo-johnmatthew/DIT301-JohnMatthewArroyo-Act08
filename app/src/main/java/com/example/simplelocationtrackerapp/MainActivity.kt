package com.example.simplelocationtrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplelocationtrackerapp.ui.theme.SimpleLocationTrackerAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize LocationManager
        locationManager = LocationManager(this)
        
        setContent {
            SimpleLocationTrackerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Resume location updates when app comes to foreground
        if (locationManager.hasLocationPermissions()) {
            // LocationManager will handle updates in MapScreen
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Stop location updates when app goes to background to save battery
        locationManager.stopLocationUpdates()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up location resources
        locationManager.stopLocationUpdates()
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    SimpleLocationTrackerAppTheme {
        MapScreen()
    }
}
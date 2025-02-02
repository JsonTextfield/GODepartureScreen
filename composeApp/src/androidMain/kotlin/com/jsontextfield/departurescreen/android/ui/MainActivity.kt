package com.jsontextfield.departurescreen.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import com.jsontextfield.departurescreen.android.R
import com.jsontextfield.departurescreen.ui.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            App(
                title = stringResource(R.string.app_name),
            )
        }
    }
}
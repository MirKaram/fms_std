package com.example.secure.feemanagmentsystem

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build


object CheckInternet {

    fun isInternetConnected(getApplicationContext: Context): Boolean {
        var status = false
        val cm =
            getApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                    // connected to the internet
                    status = true
                }
            } else {
                if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting) {
                    // connected to the internet
                    status = true
                }
            }
        }
        return status
    }
}
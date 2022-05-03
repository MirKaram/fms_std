package com.example.secure.feemanagmentsystem

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build


object CheckInternet {

    fun isInternetConnected(getApplicationContext: Context): Boolean {
        val cm =
            getApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null
    }
}
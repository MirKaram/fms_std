package com.example.secure.feemanagmentsystem.network

import com.example.secure.feemanagmentsystem.network.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {
     private val retrofit by lazy {
         Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl(BASE_URL)
             .build()
     }

    val api:StudentApiInterface by lazy {
        retrofit.create(StudentApiInterface::class.java)
    }


    private val retrofitString by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val apiString:StudentApiInterface by lazy {
        retrofitString.create(StudentApiInterface::class.java)
    }

}
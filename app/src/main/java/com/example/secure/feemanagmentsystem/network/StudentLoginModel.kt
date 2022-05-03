package com.example.secure.feemanagmentsystem.network

data class StudentLoginModel(
    val `data`: StudentData,
    val message: String,
    val success: Boolean
)
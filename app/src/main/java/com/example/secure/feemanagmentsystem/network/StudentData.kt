package com.example.secure.feemanagmentsystem.network

import java.io.Serializable

data class StudentData(
    val created_at: String,
    val current_semester: String,
    val dob: String,
    val gender: String,
    val id: Int,
    val name: String,
    val password: String,
    val program: StudentProgram,
    val program_id: String,
    val roll_no: String,
    val updated_at: String
) : Serializable
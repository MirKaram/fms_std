package com.example.secure.feemanagmentsystem.network

import java.io.Serializable

data class StudentProgram(
    val Lab_fee: Int,
    val admission_fee: Int,
    val created_at: String,
    val end_date_fall_semester: String,
    val end_date_spring_semester: String,
    val id: Int,
    val initial_date_fall_semester: String,
    val initial_date_spring_semester: String,
    val late_fee: Int,
    val name: String,
    val number_of_days_late: Int,
    val semester_fee: Int,
    val total_semesters: Int,
    val updated_at: String
):Serializable
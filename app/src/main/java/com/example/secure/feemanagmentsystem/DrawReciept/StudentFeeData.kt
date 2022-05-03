package com.example.secure.feemanagmentsystem.DrawReciept

data class StudentFeeData(
    val amount: String,
    val created_at: String,
    val id: Int,
    val program_id: String,
    val receipt_image: String,
    val semester: String,
    val student_id: String,
    val transaction_state: String,
    val updated_at: String
)
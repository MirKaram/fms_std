package com.example.secure.feemanagmentsystem.network

import com.example.secure.feemanagmentsystem.DrawReciept.StudentFeeModel
import retrofit2.Call
import retrofit2.http.*

interface StudentApiInterface {
    @FormUrlEncoded
    @POST("login")
    fun postStudentLoginInfo(
        @Field("roll_no") roll_no: String,
        @Field("password") password: String,
    ): Call<StudentLoginModel>


    @GET("feestatus/{student_id}")
    fun getStudentFeeStatus(@Path("student_id") student_id: Int): Call<String>


    @FormUrlEncoded
    @POST("fees")
    fun saveStudentFeeData(
        @Field("program_id") program_id: Int,
        @Field("student_id") student_id: Int,
        @Field("transaction_state") transaction_state: String,
        @Field("amount") amount: Int,
        @Field("semester") semester: String,
        @Field("receipt_image") receipt_image: String,
    ) : Call<StudentFeeModel>
}
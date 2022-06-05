package com.example.secure.feemanagmentsystem.network

import com.example.secure.feemanagmentsystem.DrawReciept.StudentFeeModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


    @Multipart
    @POST("fees")
    fun saveStudentFeeData(
        @Part image: MultipartBody.Part,
        @Part("fees_data") feeData: RequestBody
    ) : Call<StudentFeeModel>
}
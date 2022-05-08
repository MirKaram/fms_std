package com.example.secure.feemanagmentsystem.network

import com.example.secure.feemanagmentsystem.DrawReciept.StudentFeeModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class Repository {
    fun getStudentLoginStatus(rollno:String, password:String) : Call<StudentLoginModel>{
     return   RetrofitInstance.api.postStudentLoginInfo(rollno,password)

    }
    fun getStudentFeeStatus(student_id:Int) : Call<String>{
        return   RetrofitInstance.apiString.getStudentFeeStatus(student_id)
    }


    fun saveStudentFeeFormData(body: MultipartBody.Part, requestJsonFile: RequestBody): Call<StudentFeeModel> {
        return   RetrofitInstance.api.saveStudentFeeData(body,requestJsonFile)

    }
}
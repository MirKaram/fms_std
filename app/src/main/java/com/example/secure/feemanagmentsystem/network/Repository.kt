package com.example.secure.feemanagmentsystem.network

import com.example.secure.feemanagmentsystem.DrawReciept.StudentFeeModel
import retrofit2.Call

class Repository {
    fun getStudentLoginStatus(rollno:String, password:String) : Call<StudentLoginModel>{
     return   RetrofitInstance.api.postStudentLoginInfo(rollno,password)

    }
    fun getStudentFeeStatus(student_id:Int) : Call<String>{
        return   RetrofitInstance.apiString.getStudentFeeStatus(student_id)
    }

    fun saveStudentFeeForm(program_id:Int,student_id:Int,transaction_state:String="paid",amount:Int,semester:String,receipt_image:String):Call<StudentFeeModel>{
        return   RetrofitInstance.api.saveStudentFeeData(program_id,student_id,transaction_state,amount,semester,receipt_image)
    }
}
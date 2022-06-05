package com.example.secure.feemanagmentsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secure.feemanagmentsystem.databinding.ActivityStudentLoginBinding
import com.example.secure.feemanagmentsystem.network.Constants.ARG_POJO
import com.example.secure.feemanagmentsystem.network.Constants.BASE_URL
import com.example.secure.feemanagmentsystem.network.Repository
import com.example.secure.feemanagmentsystem.network.StudentData
import com.example.secure.feemanagmentsystem.network.StudentLoginModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


const val TAG = "StudentLoginActivity"

class StudentLoginActivity : AppCompatActivity() {
    val viewBinding: ActivityStudentLoginBinding by lazy {
        ActivityStudentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
//       BASE_URL= viewBinding.etbase.text.toString()
        initListener()
    }

    private fun initListener() {
        viewBinding.btnLogin.setOnClickListener {
            if (CheckInternet.isInternetConnected(this)){
                viewBinding.apply {
                    if (userRollno.text.trim().isNotEmpty() && password.text.trim().isNotEmpty()) {
                        pb.visibility= View.VISIBLE
                        val repository = Repository()
                        val retrofit =
                            repository.getStudentLoginStatus(userRollno.text.toString(), password.text.toString())
                        Log.e("req","   req----${retrofit.request().url()}")
                        retrofit.enqueue(object : Callback<StudentLoginModel?> {
                            override fun onResponse(
                                call: Call<StudentLoginModel?>,
                                response: Response<StudentLoginModel?>,
                            ) {
                                Log.e("res","   ress---- ${response.message()}")
                                if (response.isSuccessful) {
                                    if (response.body()?.success == true){
                                        val studenData:StudentData= response.body()?.data!!
                                        Toast.makeText(this@StudentLoginActivity, "Login Successfully.", Toast.LENGTH_SHORT).show()
                                        val intent= Intent(this@StudentLoginActivity,HomeActivity::class.java)
                                        intent.putExtra(ARG_POJO,studenData)
                                        startActivity(intent)
                                    }
                                    else {

                                        Toast.makeText(this@StudentLoginActivity, "Please Enter Correct RollNo and Password", Toast.LENGTH_SHORT).show()
                                    }

                                    pb.visibility=View.GONE
                                }
                            }

                            override fun onFailure(
                                call: Call<StudentLoginModel?>,
                                t: Throwable,
                            ) {
                                Log.e("error","   ${t.message}")
                                Toast.makeText(this@StudentLoginActivity, "Something went wrong ", Toast.LENGTH_SHORT).show()
                                pb.visibility=View.GONE
                            }
                        })
                    }
                }
            }else{
                Toast.makeText(this, "Internet Not Connected please Connect first", Toast.LENGTH_SHORT).show()
            }
          
        }
    }


}
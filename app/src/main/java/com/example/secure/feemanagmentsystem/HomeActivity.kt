package com.example.secure.feemanagmentsystem

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.secure.feemanagmentsystem.databinding.ActivityHomeBinding
import com.example.secure.feemanagmentsystem.network.Constants.ARG_POJO
import com.example.secure.feemanagmentsystem.network.Repository
import com.example.secure.feemanagmentsystem.network.StudentData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    val viewBinding:ActivityHomeBinding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    lateinit var studentInfo:StudentData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        getStudentData()
        displaystudentInfo()
    }


    private fun getStudentData() {
        studentInfo=intent.getSerializableExtra(ARG_POJO) as StudentData
        checkFeeStatus()
    }

    private fun displaystudentInfo() {
        viewBinding.apply {
         tvStudentname.text=studentInfo.name
         tvStudentDegree.text=studentInfo.program.name
         tvStudentCurrentSemester.text=studentInfo.current_semester
        }


        viewBinding.btnPayFee.setOnClickListener {
            val intent= Intent(this,FeeFormActivity::class.java)
            intent.putExtra(ARG_POJO,studentInfo)
            startActivity(intent)
        }
    }

    private fun checkFeeStatus() {
        viewBinding.pb.visibility= View.VISIBLE
        val repository = Repository()
        val retrofit =
            repository.getStudentFeeStatus(studentInfo.id)
        retrofit.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                viewBinding.tvStudentFeeStatus.text=response.body()
               viewBinding.pb.visibility= View.GONE
                if (response.body().equals("unpaid"))
                    viewBinding.btnPayFee.visibility=View.VISIBLE
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Something went wrong ", Toast.LENGTH_SHORT).show()
                viewBinding.pb.visibility= View.GONE
            }
        })
    }
}

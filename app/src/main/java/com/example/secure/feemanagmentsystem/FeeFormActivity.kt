package com.example.secure.feemanagmentsystem

import UtilsFile
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secure.feemanagmentsystem.DrawReciept.ReceiptBuilder
import com.example.secure.feemanagmentsystem.DrawReciept.StudentFeeModel
import com.example.secure.feemanagmentsystem.databinding.ActivityFeeFormBinding
import com.example.secure.feemanagmentsystem.network.Constants
import com.example.secure.feemanagmentsystem.network.Constants.pickImage
import com.example.secure.feemanagmentsystem.network.Repository
import com.example.secure.feemanagmentsystem.network.StudentData
import com.example.secure.feemanagmentsystem.network.UploadFeeData
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class FeeFormActivity : AppCompatActivity() {
    val viewBinding: ActivityFeeFormBinding by lazy { ActivityFeeFormBinding.inflate(layoutInflater) }
    lateinit var studentInfo: StudentData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        getStudentData()
    }

    private fun getStudentData() {
        studentInfo = intent.getSerializableExtra(Constants.ARG_POJO) as StudentData
        viewBinding.apply {
            tvStudentname.text = studentInfo.name
            tvStudentDegree.text = studentInfo.program.name
            tvStudentSemesterFee.text = studentInfo.program.semester_fee.toString()
            tvStudentAdmissionFee.text = studentInfo.program.admission_fee.toString()
            tvStudentLabFee.text = studentInfo.program.Lab_fee.toString()
        }

        viewBinding.btnDownload.setOnClickListener {
            runtimeTimePermission()
        }

        viewBinding.btnUpload.setOnClickListener {
            if (CheckInternet.isInternetConnected(this))
                pickImageFormFromGalley()
            else
                Toast.makeText(
                    this,
                    "Internet Not Connected please Connect first",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    fun runtimeTimePermission() {
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    viewBinding.pb.visibility=View.VISIBLE
                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            runOnUiThread {
                                saveFeeFormToGalley()
                                viewBinding.pb.visibility=View.GONE
                            }
                        },3000)

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    private fun pickImageFormFromGalley() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val uri = data?.data

            if (uri != null)
                saveFeeFormData(uri)
            else
                Toast.makeText(this, "Someting            if (isDownloadsDocument(uri)) {\n went wrong", Toast.LENGTH_SHORT).show()
        }
    }
    fun getPath(uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = getContentResolver().query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }
    private fun saveFeeFormData(uri: Uri) {
        val repository = Repository()
        val imagefile = File(getPath(uri))

        val requestFile = RequestBody.create(MediaType.parse("image/*"), imagefile)
        val body = MultipartBody.Part.createFormData("image", imagefile.name, requestFile)

        val uploadFeeData = UploadFeeData(
            studentInfo.program.id,
            studentInfo.id,
            studentInfo.program.semester_fee + studentInfo.program.admission_fee + studentInfo.program.Lab_fee,
            studentInfo.current_semester
        )

        val gson = Gson()
        val jsonStr = gson.toJson(uploadFeeData)
        Log.d("fee_data"," $jsonStr")

        val requestJsonFile = RequestBody.create(MediaType.parse("multipart/form-data"), jsonStr)


        val retrofit = repository.saveStudentFeeFormData(body,requestJsonFile)

        retrofit.enqueue(object : Callback<StudentFeeModel?> {
            override fun onResponse(
                call: Call<StudentFeeModel?>,
                response: Response<StudentFeeModel?>
            ) {
                Log.d("COmplere"," ${response.message()}   ${response.body()}  ")
                if(response.isSuccessful){
                    if (response.body()?.success == true)
                        Toast.makeText(this@FeeFormActivity, "Fee Form submit Successfully.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this@FeeFormActivity, "Something went wrong.", Toast.LENGTH_SHORT).show()

                }
            }
            override fun onFailure(call: Call<StudentFeeModel?>, t: Throwable) {
                t.message?.let { Log.d("eeee_+_", it) }
                Toast.makeText(this@FeeFormActivity, "Error : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



   private fun saveFeeFormToGalley() {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/test_pictures")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "studentFeeForm")

            val uri: Uri? =
                this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(contentResolver.openOutputStream(uri)!!)
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                this.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory =
                File(
                    this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + separator + "test_pictures"
                )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "studentFeeForm.png"
            val file = File(directory, fileName)
            saveImageToStream(FileOutputStream(file))
        }
    }

    private fun saveImageToStream(outputStream: OutputStream) {
        try {
            drawReciept().compress(CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            Toast.makeText(this, "Fee Form download successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "${e.message}.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun drawReciept(): Bitmap {
        val receipt = ReceiptBuilder(800)
        receipt.setMargin(20, 20).setAlign(Paint.Align.CENTER).setColor(Color.BLACK)
            .addParagraph()
            .addParagraph()
            .addParagraph()

            .setTextSize(20f).setTypeface(this, "fonts/RobotoMono-Bold.ttf")
            .addText("Application For Obtaining Fee Reciept")
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()
            .addParagraph()

            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Student Name", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.name).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Student Id", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.program_id).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()

            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20).addText("Degree", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.program.name).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Current Semester", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.current_semester).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Semester Fee", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.program.semester_fee.toString()).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Admission Fee", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.program.admission_fee.toString()).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


            .setMarginLeft(20).setAlign(Paint.Align.LEFT).setMarginLeft(20)
            .addText("Lab Fee", false)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .setAlign(Paint.Align.RIGHT)
            .addText(studentInfo.program.Lab_fee.toString()).addBlankSpace(30)
            .setMarginRight(20)
            .setTypeface(this, "fonts/RobotoMono-Regular.ttf")
            .addLine()
            .addParagraph()
            .addParagraph()


        // .addText("Terminal ID: 123456", false).setAlign(Paint.Align.RIGHT).addText("1234")
//            .setAlign(Paint.Align.LEFT).addLine().addText("08/15/16", false)
//            .setAlign(Paint.Align.RIGHT).addText("SERVER #4").setAlign(Paint.Align.LEFT)
//            .addParagraph().addText("CHASE VISA - INSERT").addText("AID: A000000000011111")
//            .addText("ACCT #: *********1111").addParagraph()
//            .setTypeface(this, "fonts/RobotoMono-Bold.ttf").addText("CREDIT SALE")
//            .addText("UID: 12345678", false).setAlign(Paint.Align.RIGHT).addText("REF #: 1234")
//            .setTypeface(this, "fonts/RobotoMono-Regular.ttf").setAlign(Paint.Align.LEFT)
//            .addText("BATCH #: 091", false).setAlign(Paint.Align.RIGHT).addText("AUTH #: 0701C")
//            .setAlign(Paint.Align.LEFT).addParagraph()
//            .setTypeface(this, "fonts/RobotoMono-Bold.ttf").addText("AMOUNT", false)
//            .setAlign(Paint.Align.RIGHT).addText("$ 15.00").setAlign(Paint.Align.LEFT)
//            .addParagraph().addText("TIP", false).setAlign(Paint.Align.RIGHT).addText("$        ")
//            .addLine(180).setAlign(Paint.Align.LEFT).addParagraph().addText("TOTAL", false)
//            .setAlign(Paint.Align.RIGHT).addText("$        ").addLine(180).addParagraph()
//            .setAlign(Paint.Align.CENTER).setTypeface(this, "fonts/RobotoMono-Regular.ttf")
//            .addText("APPROVED").addParagraph()
//            .addImage(barcode)
        return receipt.build()
    }

}
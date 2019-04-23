package co.tddl.mylga

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.content_share.*
import android.content.Intent
import android.provider.MediaStore
import android.graphics.Bitmap
import android.R.attr.data
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import co.tddl.mylga.onboarding.MainActivity
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.UUID.randomUUID




class ShareActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 21
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //get firebase product instances
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btn_upload_image.setOnClickListener { takePictureWithCamera() }
        btn_share_something.setOnClickListener { uploadImage() }
    }

    // 1. Launch Intent to Choose picture
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // 2. Launch Intent to take picture
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun takePictureWithCamera() {
        // 1
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 2
        val imagePath = File(filesDir, "pictures")
        val dateTime = SimpleDateFormat("yyyyMMddHHmmss'.jpg'").format(Date())
        val newFile = File(imagePath, dateTime)
        if (newFile.exists()) {
            newFile.delete()
        } else {
            newFile.parentFile.mkdirs()
        }
        filePath = getUriForFile(this, "co.tddl.mylga.fileprovider", newFile)

        // 3
        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, filePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            val clip = ClipData.newUri(contentResolver, "A photo", filePath)
            captureIntent.clipData = clip
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE)
    }


    // Helper method
    private fun setImageViewWithImage() {
        val photoPath: Uri = filePath ?: return
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoPath)
            uploadImage.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Toast.makeText(this, "Photopath: $photoPath", Toast.LENGTH_LONG).show()
    }



    // 2. Show bitmap image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                uploadImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // val imageBitmap = data?.extras?.get("data") as Bitmap
            // uploadImage.setImageBitmap(imageBitmap)
            setImageViewWithImage()
        }
    }

    //3. Upload Image to Firebase Storage
    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            ref?.putFile(filePath!!)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_LONG).show()
                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_LONG).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    Toast.makeText(this, "Uploading... ", Toast.LENGTH_LONG).show()
                }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

}

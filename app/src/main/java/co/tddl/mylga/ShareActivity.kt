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
import android.content.ClipData
import android.content.ClipDescription
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
import co.tddl.mylga.networking.FetchPlaces
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID.randomUUID


class ShareActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 21
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var auth: FirebaseAuth

    //var tmdbApiKey = BuildConfig.GCP_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //get firebase product instances
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        btn_upload_image.setOnClickListener { takePictureWithCamera() }
        btn_share_something.setOnClickListener { uploadImage() }
        val fetchPlaces = FetchPlaces()
        fetchPlaces.getMapLocationSuggestions("Alexander road", BuildConfig.GCP_API_KEY)
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
            return
            /*filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                uploadImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }*/
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
            val uploadTask = ref?.putFile(filePath!!)
                /*?.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "Uploaded ${ref.downloadUrl}", Toast.LENGTH_LONG).show()
                    location.text = ref.downloadUrl.toString()
                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_LONG).show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    Toast.makeText(this, "Uploading... ", Toast.LENGTH_LONG).show()
                }*/

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    //Toast.makeText(this, "Uploaded $downloadUri", Toast.LENGTH_LONG).show()
                    location.text = downloadUri.toString()
                    addUploadRecordToDb(downloadUri.toString(), edit_text_location.text.toString(), edit_text_description.text.toString())
                } else {
                    // Handle failures
                    // ...
                }
            }?.addOnFailureListener{

            }


        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUploadRecordToDb(uri: String, location: String, description: String ){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["userId"] = auth.currentUser?.uid.toString()
        data["imageUrl"] = uri
        data["location"] = location
        data["description"] = description

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

}

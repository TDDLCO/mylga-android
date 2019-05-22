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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.tddl.mylga.networking.FetchPlaces
import co.tddl.mylga.networking.MapApi
import co.tddl.mylga.networking.MapApiStatus
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID.randomUUID


class ShareActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 21
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModelJob: Job
    private lateinit var coroutineScope: CoroutineScope
    private var _status: MutableLiveData<MapApiStatus>? = null
    private var _properties: MutableLiveData<JsonObject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //get instances
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        viewModelJob = Job()
        coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

        _status = MutableLiveData<MapApiStatus>()
        _properties = MutableLiveData<JsonObject>()

        // Get latitude & longitude
        val sharedPreferenceHelper = SharedPreferenceHelper(this)
        val lat = sharedPreferenceHelper.getLatitude()
        val long = sharedPreferenceHelper.getLongitude()

        var latlong = ""
        if(lat != null && long != null){
            latlong = "$lat,$long"
        }

        btn_upload_image.setOnClickListener { takePictureWithCamera() }
        btn_share_something.setOnClickListener { uploadImage() }
        edit_text_location.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Log.d("Changed", p0.toString()
                getMapLocationSuggestions(p0.toString(), BuildConfig.GCP_API_KEY, latlong)
            }
        })
        edit_text_location.onItemSelectedListener

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

    private fun getMapLocationSuggestions(input: String, key: String, latlong: String) {
        Log.d("CRT", "Launched")
        coroutineScope.launch {
            Log.d("CRT", "Launched in coroutine")
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MapApi.retrofitService.getMatch(input, key, latlong, "1000", "")
            try {
                _status?.value = MapApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = getPropertiesDeferred.await()
                _status?.value = MapApiStatus.DONE
                _properties?.value = listResult
                Log.d("RESULTS", "DONE")
                Log.d("RESULTS_data", listResult.toString())
                parseJson(listResult)
            } catch (e: Exception) {
                _status?.value = MapApiStatus.ERROR
                _properties?.value = null
                Log.d("RETURN ERR", e.toString())
            }

        }
    }

    fun parseJson(jsonObject: JsonObject){

        if(jsonObject != null) {
            val jsonArray = jsonObject.getAsJsonArray("predictions")
            Log.d("RESULTS_data_array", jsonArray.toString())
            val places = arrayListOf<String>()
            for (i in 0..(jsonArray.size() - 1)) {
                val item = jsonArray.get(i)
                places.add(item.asJsonObject.get("description").asString)
                // Your code here
            }

            runOnUiThread {
                places.toArray()
                val adapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_dropdown_item_1line, places
                )

                edit_text_location?.setAdapter(adapter)
                edit_text_location.showDropDown()
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
        if(filePath == null){
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
            return
        }

        if(edit_text_description.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show()
            return
        }

        if(edit_text_location.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please add a location", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading()
        val pathString = "uploads/${UUID.randomUUID()}"
        val ref = storageReference?.child(pathString)
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
                addUploadRecordToDb(pathString, downloadUri.toString(), edit_text_location.text.toString(), edit_text_description.text.toString())
            } else {
                // Handle failures
                // ...
            }
        }?.addOnFailureListener{

        }
    }

    private fun showLoading(){
        progress_bar.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            edit_text_description.focusable = View.NOT_FOCUSABLE
            edit_text_location.focusable = View.NOT_FOCUSABLE
        }
        edit_text_description.isEnabled = false
        edit_text_location.isEnabled = false
        btn_share_something.text = "Posting"
        btn_upload_image.visibility = View.GONE
    }

    private fun hideLoading(){
        progress_bar.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            edit_text_description.focusable = View.FOCUSABLE
            edit_text_location.focusable = View.FOCUSABLE
        }
        edit_text_description.isEnabled = true
        edit_text_location.isEnabled = true
        btn_share_something.text = "Post"
        btn_upload_image.visibility = View.VISIBLE

        clearInputs()
    }

    private fun clearInputs(){
        edit_text_description.setText("")
        edit_text_location.setText("")
        uploadImage.setImageResource(R.drawable.ic_image_gray_40dp)

    }

    private fun addUploadRecordToDb(pathString: String, uri: String, location: String, description: String ){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()

        val terms = location.split(",")
        val trimmedTerms = arrayListOf<String>()
        terms.forEach {term ->
            trimmedTerms.add(term.trim())
        }

        data["userId"] = auth.currentUser?.uid.toString()
        data["imageUrl"] = uri
        data["location"] = location
        data["description"] = description
        data["terms"] = trimmedTerms
        data["createdAt"] = System.currentTimeMillis()
        data["pathString"] =  pathString

        db.collection("posts")
        .add(data)
        .addOnSuccessListener { documentReference ->
            hideLoading()
            Toast.makeText(this, "Post shared", Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener { e ->
            hideLoading()
            Toast.makeText(this, "Error sharing post", Toast.LENGTH_LONG).show()
        }
    }

}

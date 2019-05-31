package co.tddl.mylga

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.content_share.*
import android.content.Intent
import android.provider.MediaStore
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.util.*
import com.google.firebase.storage.UploadTask
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import co.tddl.mylga.networking.MapApi
import co.tddl.mylga.networking.MapApiStatus
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class ShareActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 21
    private val MY_PERMISSIONS_REQUEST_STORAGE_AND_CAMERA = 201
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModelJob: Job
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var fileCoroutineScope: CoroutineScope
    private var _status: MutableLiveData<MapApiStatus>? = null
    private var _properties: MutableLiveData<JsonObject>? = null
    private var currentPhotoPath: String? = null
    val REQUEST_TAKE_PHOTO = 1

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
        fileCoroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

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

        btn_upload_image.setOnClickListener { /*takePictureWithCamera()*/ selectImage() }
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
    }

    private fun selectImage(){
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options
        ) { dialog, which ->
            when (which) {
                0 -> {
                    if(Build.VERSION.SDK_INT > 21)
                        checkReadWritePermission()
                    else {
                        Toast.makeText(this, "Older Phone", Toast.LENGTH_LONG).show()
                        checkReadWritePermission()
                    }
                }
                1 -> { chooseImage() }
                else -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun checkReadWritePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_STORAGE_AND_CAMERA
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_STORAGE_AND_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // save location in preference
            dispatchTakePictureIntent()
        }else{
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "co.tddl.mylga.fileprovider",
                        it
                    )
                    filePath = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                }
            }
        }
    }

    private fun takePhotoFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    fun saveImage(myBitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream(myBitmap.width * myBitmap.height)
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val wallpaperDirectory = File((Environment.getExternalStorageDirectory().absolutePath).toString() + "/mylga/camera")

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, (("mylga_"+ Calendar.getInstance()
                .timeInMillis) + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray()) //bytes.toByteArray()
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()

            runOnUiThread {
                btn_upload_image.visibility = View.VISIBLE
            }

            return Uri.fromFile( File(f.absolutePath))
        }
        catch (e1: IOException) {
            Log.e("IMG_ERROR", "${e1.message}")
            e1.printStackTrace()
        }

        runOnUiThread {
            btn_upload_image.visibility = View.VISIBLE
        }

        return null
    }

    // 1. Launch Intent to Choose picture
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun getMapLocationSuggestions(input: String, key: String, latlong: String) {
        Log.d("CRT", "Launched")
        coroutineScope.launch {
            Log.d("CRT", "Launched in coroutine")
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MapApi.retrofitService.getMatch(input, key, latlong, "3000", "")
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

    fun parseJson(jsonObject: JsonObject?){

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

        /*if (requestCode == REQUEST_IMAGE_CAPTURE){
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            uploadImage.setImageBitmap(thumbnail)
            btn_upload_image.visibility = View.GONE
            //filePath = saveImage(thumbnail)
            fileCoroutineScope.launch {
                filePath = saveImage(thumbnail)
            }
            //Toast.makeText(applicationContext, "$filePath", Toast.LENGTH_LONG).show()
        }*/

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            uploadImage.setImageBitmap(bitmap)
            Toast.makeText(this, "$filePath", Toast.LENGTH_LONG).show()
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
            }
        }?.addOnFailureListener{

        }
    }

    private fun showLoading(){
        share_loading_screen.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
        share_loading_screen.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

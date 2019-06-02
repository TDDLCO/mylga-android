package co.tddl.mylga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.tddl.mylga.onboarding.RegistrationActivity
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPrefHelper: SharedPreferenceHelper
    private var docId: String? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fetchUserDetails()

        sharedPrefHelper = SharedPreferenceHelper(applicationContext)

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }


    private fun addCheckedChangeListeners(){
        switch_show_picture.setOnCheckedChangeListener { _, isChecked -> updateSettingInDb("showPicture", isChecked) }
        switch_show_location.setOnCheckedChangeListener { _, isChecked -> updateSettingInDb("showLocation", isChecked) }
    }

    private fun updateSettingInDb(setting: String, value: Boolean){

       db!!.collection("users").document(docId!!)
            .update(setting, value)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Updated!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error. Could not update setting", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchUserDetails(){
        db!!.collection("users")
            .whereEqualTo("id", auth!!.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                docId = documents.first().id
                val showLocation = documents.first()["showLocation"] as Boolean
                val showPicture = documents.first()["showPicture"] as Boolean
                showUiFeedback(showPicture, showLocation)
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "An error occurred. Could not fetch details", Toast.LENGTH_LONG).show()
            }
    }

    private fun showUiFeedback(showPicture: Boolean, showLocation: Boolean){
        if(showPicture){
            switch_show_picture.isChecked = true
        }

        if(showLocation){
            switch_show_location.isChecked = true
        }

        addCheckedChangeListeners()
    }

}

package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.HomeActivity
import co.tddl.mylga.R
import co.tddl.mylga.model.Feed
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_confirmation.*

class ConfirmationActivity : AppCompatActivity() {

    private val INTENT_USER_NAME = "INTENT_USER_NAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        btn_next.setOnClickListener {
            val username = edit_text_user_name.text

            if(username == null || username.toString().trim() == ""){
                Toast.makeText(this, "Please fill your username", Toast.LENGTH_SHORT).show()
            }else{
                val sharedPref = SharedPreferenceHelper(this)
                sharedPref.setUserName(username.toString())

                saveUserName(username.toString())

                /* val intent = Intent(this, PermissionActivity::class.java)
                intent.putExtra(INTENT_USER_NAME, username.toString())
                startActivity(intent)
                finish() */
            }
        }
    }

    private fun saveUserName(username: String){
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val user = HashMap<String, Any>()
        user["id"] = auth.currentUser?.uid.toString()
        user["name"] = username

        db.collection("users").whereEqualTo("id", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    // document does not exist
                    //  create record
                    db.collection("cities")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, PermissionActivity::class.java)
                            intent.putExtra(INTENT_USER_NAME, username)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                        }

                }else{
                    val docId = documents.first().id

                    db.collection("users").document(docId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, PermissionActivity::class.java)
                            intent.putExtra(INTENT_USER_NAME, username)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e -> Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show() }

                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }
}

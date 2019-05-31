package co.tddl.mylga

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*

class ProfileActivity : AppCompatActivity() {

    private var docId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fetchUserDetails()

        image_view_profile.setOnClickListener {
            appbar.visibility = View.GONE
            nested_scroll_view_content.visibility = View.GONE
            animateZoom()
        }

        image_button_close.setOnClickListener {
            appbar.visibility = View.VISIBLE
            nested_scroll_view_content.visibility = View.VISIBLE
            relative_layout_expanded_image.visibility = View.GONE
        }

        btn_update_profile.setOnClickListener { updateUserDetails() }
    }

    private fun animateZoom(){
        val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        relative_layout_expanded_image.apply {
            alpha = 0.85f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        nested_scroll_view_content.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    appbar.visibility = View.GONE
                    nested_scroll_view_content.visibility = View.GONE
                }
            })
    }

    private fun fetchUserDetails(){
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("id", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                docId = documents.first().id
                val username = documents.first()["name"].toString()
                showUserDetails(username)
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "An error occurred. Could not fetch details", Toast.LENGTH_LONG).show()
            }
    }

    private fun showUserDetails(username: String){
        edit_text_user_name.setText(username)
        htab_collapse_toolbar.title = username
    }

    private fun updateUserDetails(){
        if(docId.isNullOrEmpty()){
            Toast.makeText(this, "An error occurred. Could not update details", Toast.LENGTH_LONG).show()
            return
        }
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val user = HashMap<String, Any>()
        user["id"] = auth.currentUser?.uid.toString()
        user["name"] = edit_text_user_name.text.toString()

        db.collection("users").document(docId!!)
            .set(user)
            .addOnSuccessListener {
                htab_collapse_toolbar.title = user["name"].toString()
                Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Error. Could not save username", Toast.LENGTH_LONG).show()
            }
    }

}

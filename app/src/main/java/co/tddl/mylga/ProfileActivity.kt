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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fetchUserDetails()

        val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        image_view_profile.setOnClickListener {

            appbar.visibility = View.GONE
            nested_scroll_view_content.visibility = View.GONE

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

        image_button_close.setOnClickListener {
            appbar.visibility = View.VISIBLE
            nested_scroll_view_content.visibility = View.VISIBLE
            relative_layout_expanded_image.visibility = View.GONE
        }

        btn_update_profile.setOnClickListener {  }
    }

    private fun fetchUserDetails(){
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("id", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                val username = documents.first()["name"].toString()
                edit_text_user_name.setText(username)
                htab_collapse_toolbar.title = username
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "An error occurred. Could not fetch details", Toast.LENGTH_LONG).show()
            }
    }

    private fun showUserDetails(){

    }

    private fun updateUserDetails(){

    }

}

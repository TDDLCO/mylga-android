package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.HomeActivity
import co.tddl.mylga.R

import kotlinx.android.synthetic.main.activity_confirmation.*

class ConfirmationActivity : AppCompatActivity() {

    private val INTENT_USER_NAME = "INTENT_USER_NAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        btn_next.setOnClickListener {
            val username = edit_text_user_name.text
            if(username == null || username.trim() == ""){
                Toast.makeText(this, "Please fill your username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, PermissionActivity::class.java)
            intent.putExtra(INTENT_USER_NAME, username)
            startActivity(intent)
        }
    }

}

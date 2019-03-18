package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.R

import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        btn_signup_twitter.setOnClickListener {
            var intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }

    }

}

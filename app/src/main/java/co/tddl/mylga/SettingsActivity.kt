package co.tddl.mylga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.tddl.mylga.onboarding.RegistrationActivity
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPrefHelper: SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPrefHelper = SharedPreferenceHelper(applicationContext)

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }

}

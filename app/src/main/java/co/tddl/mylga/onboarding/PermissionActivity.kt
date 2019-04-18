package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.HomeActivity
import co.tddl.mylga.R
import kotlinx.android.synthetic.main.activity_permission.*


class PermissionActivity : AppCompatActivity() {

    private val INTENT_USER_NAME = "INTENT_USER_NAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val ss:String = intent.getStringExtra("INTENT_USER_NAME")

        btn_next.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

}

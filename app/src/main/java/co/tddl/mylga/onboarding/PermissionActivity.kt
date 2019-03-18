package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.R
import kotlinx.android.synthetic.main.activity_permission.*


class PermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        btn_next.setOnClickListener {
            var intent = Intent(this, ConfirmationActivity::class.java)
            startActivity(intent)
        }
    }

}

package co.tddl.mylga.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity;
import co.tddl.mylga.HomeActivity
import co.tddl.mylga.R
import kotlinx.android.synthetic.main.activity_permission.*


class PermissionActivity : AppCompatActivity() {

    private val INTENT_USER_NAME = "INTENT_USER_NAME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val username:String? = intent.getStringExtra(INTENT_USER_NAME)
        text_view_greeting.text = "Hi $username, nice to meet you."

        btn_next.setOnClickListener {
            permission_loading_screen.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}

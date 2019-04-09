package co.tddl.mylga.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.tddl.mylga.R
import com.google.android.gms.auth.api.signin.GoogleSignIn

import kotlinx.android.synthetic.main.activity_registration.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class RegistrationActivity : AppCompatActivity() {

    private var mGoogleSignInClient : GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        btn_signup_twitter.setOnClickListener {
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(TOKEN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // val account = GoogleSignIn.getLastSignedInAccount(this)
        // updateUI(account)

        btn_signup_email.setOnClickListener {
            signIn()
        }

    }

    private fun updateUI(account: FirebaseUser?) {  //GoogleSignInAccount
        if(account != null){
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "No account yet", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 1234)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1234) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            // handleSignInResult(task)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("FAILED", "Google sign in failed - ${Activity.RESULT_OK}", e)
                // ...
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            // updateUI(account)
        } catch (e: ApiException) {
            updateUI(null)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("USER_ID", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SUCCESS", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FAIL", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication was not done. failed.", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }

                // ...
            }
    }

}

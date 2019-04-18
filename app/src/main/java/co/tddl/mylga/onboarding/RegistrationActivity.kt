package co.tddl.mylga.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.tddl.mylga.R
import com.facebook.*
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
import java.util.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider


class RegistrationActivity : AppCompatActivity() {

    private var mGoogleSignInClient : GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

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

        // Facebook logic
        callbackManager = CallbackManager.Factory.create()
        login_button.setReadPermissions(Arrays.asList("email"))

        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("SUCCESS", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("CANCEL", "facebook:onCancel")
            }

            override fun onError(exception: FacebookException) {
                Log.d("ERROR", "facebook:onCancel")
            }
        })

    }

    private fun handleFacebookAccessToken(accessToken: AccessToken){
        Log.d("TOKEN", "handleFacebookAccessToken:$accessToken")

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SUCCESS", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ERROR", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    private fun updateUI(account: FirebaseUser?) {  //GoogleSignInAccount
        if(account != null){
            val intent = Intent(this, ConfirmationActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "No account yet", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 1234)
    }

    /*
    * val accessToken = AccessToken.getCurrentAccessToken()
      val isLoggedIn = accessToken != null && !accessToken.isExpired
    * */

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
        }else if(requestCode == 1356){
            callbackManager.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
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

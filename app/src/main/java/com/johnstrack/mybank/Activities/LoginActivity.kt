package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.johnstrack.mybank.R
import kotlinx.android.synthetic.main.activity_login.*
import android.support.design.widget.Snackbar
import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount







class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSiginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun loginButtonClicked (view: View) {
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Could not login user: ${e.localizedMessage}")
                    loginEmailText.setText("")
                    loginPasswordText.setText("")
                    Toast.makeText(this, "Could not login user: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("Error", "Google sign in failed: ${e.statusCode}")
            }
        }
    }

    fun createUserButtonClicked (view: View) {
        val createIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createIntent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Debug", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(FragmentActivity.TAG, "signInWithCredential:success")
                        val user = auth.getCurrentUser()
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("Error", "signInWithCredential:failure", task.exception)
                        Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // ...
                })
    }
}
package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.johnstrack.mybank.R
import kotlinx.android.synthetic.main.activity_login.*







class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
                }
                .addOnFailureListener { e ->
                    Log.e("Error", "Could not login user: ${e.localizedMessage}")
                    loginEmailText.setText("")
                    loginPasswordText.setText("")
                    Toast.makeText(this, "Could not login user: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
    }

//    public override fun onStart() {
//        Log.d("OnSTART DEBUG", "In the OnStart command (should we avoid this?)")
//        super.onStart()
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }


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
        Log.d("Debug", "firebaseAuthWithGoogle: ${acct.id}")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Debug", "signInWithCredential:success")
                        updateUI(auth.currentUser)
                    } else {
                        Log.e("Error", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication with Firestore Failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        finish()
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d("Debug", "You don't really need this... ${user?.uid}")
        finish()
    }
}
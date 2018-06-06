package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.johnstrack.mybank.R
import kotlinx.android.synthetic.main.activity_login.*



class LoginActivity : AppCompatActivity() {

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
        val googleSigninClient = GoogleSignIn.getClient(this, gso)

        googleSiginButton.setOnClickListener {

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

    fun createUserButtonClicked (view: View) {
        var createIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createIntent)
    }

    fun googleSigninClicked (view: View) {

    }
}
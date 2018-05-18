package com.johnstrack.mybank.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.mybank.R
import com.johnstrack.mybank.Utilities.DATE_CREATED
import com.johnstrack.mybank.Utilities.USERNAME
import com.johnstrack.mybank.Utilities.USERS_REF
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        auth = FirebaseAuth.getInstance()
    }

    fun createUserButtonClicked(view: View) {

        //add additional validation (not null, password length...)
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()
        val username = createUsernameText.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val changeRequest = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                    result.user.updateProfile(changeRequest)
                            .addOnFailureListener { exception ->
                                Log.e("Error", "Could not update display name: ${exception.localizedMessage}")
                            }

                    val data = HashMap<String, Any>()
                    data.put(USERNAME, username)
                    data.put(DATE_CREATED, FieldValue.serverTimestamp())

                    FirebaseFirestore.getInstance().collection(USERS_REF).document(result.user.uid)
                            .set(data)
                            .addOnSuccessListener {
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Error", "Could not add user: ${exception.localizedMessage}")
                            }

                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "Could not create user: ${exception.localizedMessage}")
                }
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }

    fun cancelButtonClicked(view: View) {
        finish()
    }
}
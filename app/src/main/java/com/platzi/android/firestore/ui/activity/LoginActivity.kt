package com.platzi.android.firestore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.ui.activity.model.User
import com.platzi.android.firestore.ui.activity.network.Callback
import com.platzi.android.firestore.ui.activity.network.Firestoreservice
import com.platzi.android.firestore.ui.activity.network.USERS_COLECTION_NAME
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_trader.*

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"
    private var auth: FirebaseAuth=FirebaseAuth.getInstance()
    lateinit var firestoreservice: Firestoreservice


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firestoreservice=Firestoreservice(FirebaseFirestore.getInstance())
    }


    fun onStartClicked(view: View) {
        view.isEnabled=false
        auth.signInAnonymously()
            .addOnCompleteListener {
                if(it.isSuccessful){

                    val username= usernamet.text.toString()
                    firestoreservice.findUserById(username,object : Callback<User>{
                        override fun onSuccess(result: User?) {
                            if(result==null){
                                val user= User()
                                user.username=username
                                saveUserAndStartMainActivity(user,view)
                            }else{
                                startMainActivity(username)
                            }
                        }

                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }

                    })



                }else{
                    showErrorMessage(view)
                    view.isEnabled=true
                }

            }


    }

    private fun saveUserAndStartMainActivity(user: User, view: View) {
        firestoreservice.setDocument(user, USERS_COLECTION_NAME,user.username,object :
            Callback<Void> {
            override fun onSuccess(result: Void?) {
                startMainActivity(user.username)
            }

            override fun onFailed(exception: Exception) {
                showErrorMessage(view)
            }

        })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}

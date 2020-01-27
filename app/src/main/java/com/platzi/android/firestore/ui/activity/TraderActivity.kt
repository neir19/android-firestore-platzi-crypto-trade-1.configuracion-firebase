package com.platzi.android.firestore.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.ui.activity.adapter.CryptosAdapter
import com.platzi.android.firestore.ui.activity.adapter.CryptosAdapterListener
import com.platzi.android.firestore.ui.activity.model.Crypto
import com.platzi.android.firestore.ui.activity.model.User
import com.platzi.android.firestore.ui.activity.network.Callback
import com.platzi.android.firestore.ui.activity.network.Firestoreservice
import com.platzi.android.firestore.ui.activity.network.RealtimeDataListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*


/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    lateinit var firestoreservice: Firestoreservice
    private val cryptoAdapter:CryptosAdapter= CryptosAdapter(this)
    private  var  username:String?=null
    private var user:User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firestoreservice= Firestoreservice(FirebaseFirestore.getInstance())
        username= intent.extras!![USERNAME_KEY]!!.toString()
        usernameTextView.text=username

        loadCryptos()
        configureRecyclerView()


        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            genereteCrptoCurrenciesRandom()
        }

    }

    private fun genereteCrptoCurrenciesRandom() {
        for(cryto in cryptoAdapter.cryptosList){
            val amount=(1..10).random()
            cryto.available+=amount
            firestoreservice.updateCrypto(cryto)

        }//
    }

    private fun loadCryptos() {
        firestoreservice.getCryptos(object : Callback<List<Crypto>>{
            override fun onSuccess(cryptoList: List<Crypto>?) {


                firestoreservice.findUserById(username!!, object : Callback<User>{
                    override fun onSuccess(result: User?) {
                        user=result
                        if(user!!.cryptosList==null){
                            val userCryptoList= mutableListOf<Crypto>()
                                for (crypto in cryptoList!! ) {
                                    val cryptoUser= Crypto()
                                    cryptoUser.name=crypto.name
                                    cryptoUser.available=10
                                    cryptoUser.imageUrl=crypto.imageUrl
                                    userCryptoList.add(cryptoUser)
                                }
                                user!!.cryptosList=userCryptoList
                                firestoreservice.updateUser(user!!, null)
                        }
                        loadUserCryptos()
                        addRealtimeDataaseListener(user!!,cryptoList!!)
                    }

                    override fun onFailed(exception: Exception) {
                      showGeneralServerErrorMessage()
                    }

                })
                this@TraderActivity.runOnUiThread {
                    cryptoAdapter.cryptosList=cryptoList!!
                    cryptoAdapter.notifyDataSetChanged()
                }

            }

            override fun onFailed(exception: Exception) {
                Log.e("TraiderActivity", "error loading cryptos",exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun addRealtimeDataaseListener(user: User, cryptoList:List<Crypto>) {
        firestoreservice.listenForUpdates(user,object :RealtimeDataListener<User>{
            override fun onDataChange(updatedData: User) {
                this@TraderActivity.user=updatedData
                loadUserCryptos()
            }

            override fun onError(exception: java.lang.Exception) {
               showGeneralServerErrorMessage()
            }

        })
        firestoreservice.listenForUpdates(cryptoList, object : RealtimeDataListener<Crypto>{
            override fun onDataChange(updatedData: Crypto) {
                var pos=0
                Log.e("dato 0","${cryptoAdapter.cryptosList[0].name}")
                for(crypto in cryptoAdapter.cryptosList){
                    if(crypto.name == updatedData.name){
                        crypto.available=updatedData.available
                        cryptoAdapter.notifyItemChanged(pos)
                    }
                    Log.e("dato $pos","${cryptoAdapter.cryptosList[pos].name}")
                    pos++

                }
            }

            override fun onError(exception: java.lang.Exception) {
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun loadUserCryptos() {
        runOnUiThread {
            if(user!=null && user!!.cryptosList!=null){
                infoPanel.removeAllViews()
                for(crypto in user!!.cryptosList!!){
                    addUserCryptonInforRow(crypto)
                }
            }
        }
    }

    private fun addUserCryptonInforRow(crypto: Crypto) {
       val view= LayoutInflater.from(this).inflate(R.layout.coin_info,infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text=
            getString(R.string.coin_info,crypto.name,crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)

    }

    private fun configureRecyclerView() {

       recyclerView.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=cryptoAdapter
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    override fun onBuyCrytoCLicked(cryto: Crypto) {
        if(cryto.available>0){
            for(userCryto in user!!.cryptosList!!){
                if(userCryto.name==cryto.name){
                    userCryto.available+=1
                    Log.e("errorD","1 ${userCryto.name == cryto.name} |||  2 ${cryto.name}")
                    break
                }
            }

            cryto.available-=1
            firestoreservice.updateUser(user!!,null)
            firestoreservice.updateCrypto(cryto)
        }
    }
}
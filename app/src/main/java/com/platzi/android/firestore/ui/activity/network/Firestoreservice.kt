package com.platzi.android.firestore.ui.activity.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.ui.activity.model.Crypto
import com.platzi.android.firestore.ui.activity.model.User


const val  CRYPTO_COLECTION_NAME="cryptos"
const val  USERS_COLECTION_NAME="users"
class Firestoreservice(val firebaseFirestore: FirebaseFirestore) {
    fun setDocument(data:Any,collectionName:String,id:String, callback: Callback<Void>){
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener {
                callback.onSuccess(null)
            }
            .addOnFailureListener {exception ->  callback.onFailed(exception) }

    }
    fun updateUser(user:User,callback: Callback<User>?){
        firebaseFirestore.collection(USERS_COLECTION_NAME).document(user.username)
            .update("cryptosList",user.cryptosList)
            .addOnSuccessListener { result->
                if(callback!= null){
                    callback.onSuccess(user)
                }
            }
            .addOnFailureListener { exception ->callback!!.onFailed(exception)  }

    }
    fun updateCrypto(crypto: Crypto){
        firebaseFirestore.collection(CRYPTO_COLECTION_NAME).document(crypto.getDocumentId())
            .update("available",crypto.available)
    }
    fun getCryptos(callback: Callback<List<Crypto>>?){
        firebaseFirestore.collection(CRYPTO_COLECTION_NAME)
            .get()
            .addOnSuccessListener {result ->

                    val  cryptoList= result.toObjects(Crypto::class.java)
                    callback!!.onSuccess(cryptoList)

            }
            .addOnFailureListener {exception ->callback!!.onFailed(exception)
            }
    }
    fun findUserById(id:String, callback: Callback<User>){
        firebaseFirestore.collection(USERS_COLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener { result->
                if(result.data!=null){
                    callback.onSuccess(result.toObject(User::class.java))
                }else{
                    callback.onSuccess(null)
                }
            }
            .addOnFailureListener { exception -> callback.onFailed(exception)  }
    }
    fun listenForUpdates(
        cryptos:List<Crypto>,
        listener: RealtimeDataListener<Crypto>
    ){
        val cryptoReference= firebaseFirestore.collection(CRYPTO_COLECTION_NAME)
        for(crypto in cryptos){
            cryptoReference.document(crypto.getDocumentId()).addSnapshotListener{snapshot,e->
                if(e!=null){
                    listener.onError(e)
                }
                if(snapshot!=null &&snapshot.exists()){
                    listener.onDataChange(snapshot.toObject(Crypto::class.java)!!)

                }

            }
        }

    }
    fun listenForUpdates(
        user: User,
        listener: RealtimeDataListener<User>
    ){
        val usersReference= firebaseFirestore.collection(USERS_COLECTION_NAME)
        usersReference.document(user.username).addSnapshotListener { snapshot, e ->

            if(e!=null){
                listener.onError(e)
            }
            if(snapshot!=null &&snapshot.exists()){
                listener.onDataChange(snapshot.toObject(User::class.java)!!)

            }
        }

        }

    }

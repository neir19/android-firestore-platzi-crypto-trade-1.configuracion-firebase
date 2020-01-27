package com.platzi.android.firestore.ui.activity.network

interface Callback <T>{
    fun onSuccess(result:T?)
    fun  onFailed(exception:Exception)
}
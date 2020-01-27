package com.platzi.android.firestore.ui.activity.adapter

import com.platzi.android.firestore.ui.activity.model.Crypto

interface CryptosAdapterListener {
    fun onBuyCrytoCLicked(cryto:Crypto)
}
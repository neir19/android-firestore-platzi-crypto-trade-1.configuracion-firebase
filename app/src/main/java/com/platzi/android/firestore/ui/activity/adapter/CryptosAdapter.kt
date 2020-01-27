package com.platzi.android.firestore.ui.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.platzi.android.firestore.R
import com.platzi.android.firestore.ui.activity.model.Crypto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.crypto_row.view.*

class CryptosAdapter( val cryptosAdapterListener: CryptosAdapterListener):RecyclerView.Adapter<CryptosAdapter.Holder>() {

     var cryptosList:List<Crypto> = ArrayList()
    class Holder( itemView:View):RecyclerView.ViewHolder(itemView){
        fun bindView(crypto: Crypto,cryptosAdapterListener: CryptosAdapterListener){

                itemView.nameTextView.text=crypto.name
                itemView.availableTextView.text=itemView.context.getString(R.string.available_message,crypto.available.toString())
                Picasso.get().load(crypto.imageUrl).into(itemView.imgLogo)
                itemView.buyButton.setOnClickListener {
                    cryptosAdapterListener.onBuyCrytoCLicked(crypto)


            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.crypto_row, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = cryptosList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(cryptosList[position],cryptosAdapterListener)
//       val crypto=cryptosList[position]
//
//        Picasso.get().load(crypto.imageUrl).into(holder.image)
//        holder.name.text=crypto.name
//        holder.available.text=holder.itemView.context.getString(R.string.available_message,crypto.available.toString())
//        //holder.buybutton.setOnClickListener {
//          //  cryptosAdapterListener.onBuyCrytoCLicked(crypto)
//        //}
    }

}
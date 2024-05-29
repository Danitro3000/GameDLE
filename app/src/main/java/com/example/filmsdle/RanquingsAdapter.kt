package com.example.filmsdle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.databinding.CardRanquingsBinding

class RanquingsAdapter(private val usuaris: List<User>?) : RecyclerView.Adapter<RanquingsAdapter.ActorViewHolder>() {

     class ActorViewHolder(private val binding: CardRanquingsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.aliasUsuari.text = user.alias
            binding.Puntuacio.text = user.puntos.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = CardRanquingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val user = usuaris!![position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = usuaris!!.size

}
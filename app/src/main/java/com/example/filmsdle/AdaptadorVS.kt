package com.example.filmsdle

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.CardFriendlistBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdaptadorVS(private val Usuaris: List<User>, private val context: Context) : RecyclerView.Adapter<AdaptadorVS.VsViewHolder>() {

    class VsViewHolder(private val binding: CardFriendlistBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usuari: User, idUsuariActual: Int, nomUsuari: String) {

            binding.aliasUsuari.text = usuari.alias

            binding.aliasUsuari.setOnClickListener {
                if (binding.llExpandable.visibility == android.view.View.GONE) {
                    binding.llExpandable.visibility = android.view.View.VISIBLE
                    binding.aliasUsuari.text = usuari.alias
                } else {
                    binding.llExpandable.visibility = android.view.View.GONE
                    binding.aliasUsuari.text = usuari.alias
                }
            }

            binding.AcceptarDesafiu.setOnClickListener {
                val movieAPI = MovieAPI()

                GlobalScope.launch(Dispatchers.Main) {
                    movieAPI.enviarInvitacio(
                        idUsuariActual,
                        usuari.id,
                        true,
                        false,
                        "$nomUsuari vol jugar amb tu!!"
                    )

                    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("idUsuariDesafiar", usuari.id)
                    editor.apply()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VsViewHolder {
        val binding = CardFriendlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VsViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: VsViewHolder, position: Int) {
        val user = Usuaris[position]

        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val idUsuariActual = sharedPreferences.getInt("idUsuariLogged", 0)

        val sharedPreferencesName: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val nomUsuari = sharedPreferencesName.getString("NomUsuariLogged", "")

        holder.bind(user, idUsuariActual, nomUsuari!!)
    }

    override fun getItemCount(): Int = Usuaris.size
}

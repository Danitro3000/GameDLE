package com.example.filmsdle

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Invitation
import com.example.filmsdle.BD.Models.Partida
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.ActivityMenuPrincipalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MenuPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding
    val movieAPI = MovieAPI()
    var allthingsAdivinar: AllThings? = null
    var PartidaPendent : Partida? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.Home -> {
                    val intent = Intent(this, MenuPrincipal::class.java)
                    startActivity(intent)
                    true
                }
                R.id.Classic -> {
                    val intent = Intent(this, SeleccioJoc::class.java)
                    startActivity(intent)
                    true
                }
                R.id.JocVersus -> {
                    val intent = Intent(this, seleccioVS::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)
        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)
        findViewById<ImageView>(R.id.fons).setImageBitmap(blurredBackground)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.modeClassic.setOnClickListener{
            val intent = Intent(this, SeleccioJoc::class.java)
            startActivity(intent)
        }

        binding.modeVS.setOnClickListener {
            val intent = Intent(this, seleccioVS::class.java)
            startActivity(intent)
        }

        binding.butRanguings.setOnClickListener {
            val intent = Intent(this, Ranquings::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            val invitaciones = movieAPI.getInvitacions()
            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val idUsuariLogged = sharedPreferences.getInt("idUsuariLogged", -1)
            if (invitaciones.isNullOrEmpty()) {


            }
            else {

                val editor = sharedPreferences.edit()
                editor.putInt("senderId", invitaciones!!.filter { it.senderId != null }.map { it.senderId }.firstOrNull()!!)
                editor.apply()

                val editor2 = sharedPreferences.edit()
                editor2.putInt("receiverId", invitaciones!!.filter { it.receiverId != null }.map { it.receiverId }.firstOrNull()!!)
                editor2.apply()

                val invitacionesParaUsuario = invitaciones?.filter { it.receiverId == idUsuariLogged }
                if (invitacionesParaUsuario != null && invitacionesParaUsuario.isNotEmpty()) {
                    mostrarInvitaciones(invitacionesParaUsuario)
                } else {
                    Toast.makeText(applicationContext, "No s'han trobat invitacions per tu", Toast.LENGTH_SHORT).show()
                }
            }

            val partidasPendientes = movieAPI.getPartidaById(idUsuariLogged)

            if (partidasPendientes != null && partidasPendientes.isNotEmpty()) {
                mostrarPartidasPendientes(partidasPendientes)
            }


        }
    }

    private fun mostrarPartidasPendientes(partidasPendientes: List<Partida>) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val idUsuariLogged = sharedPreferences.getInt("idUsuariLogged", -1)

        for (partida in partidasPendientes) {
            val builder = AlertDialog.Builder(this)


// usuario1
            if (partida.usuario1Id == idUsuariLogged) {
                var user1time = convertTimeStringToSeconds(partida.tiempoUsuario1)
                var user2time = convertTimeStringToSeconds(partida.tiempoUsuario2)

               if (partida.tiempoUsuario1 == "00:00:00") {
                   builder.setTitle("Tienes una partida pendiente")

                   builder.setMessage("¿Quieres continuar la partida?")

                   builder.setPositiveButton("Aceptar") { dialog, _ ->
                       PartidaPendent = partida
                       val intent = Intent(this@MenuPrincipal, JocVSActivity::class.java)
                       intent.putExtra("partida", partida as Parcelable)
                       startActivity(intent)
                       dialog.dismiss()
                   }
                   builder.setNegativeButton("Cancelar") { dialog, _ ->
                       dialog.dismiss()
                   }
                   val dialog: AlertDialog = builder.create()
                   dialog.show()
               }

               else if(partida.tiempoUsuario2 == "00:00:00") {
                }
                else if( user1time > 0 && user2time > 0){
                   CoroutineScope(Dispatchers.Main).launch {
                       val usuarioGanador = movieAPI.comprobarGuanyadors(partida.partidaId!!)
                       if(usuarioGanador!!.alias == null){
                           Toast.makeText(applicationContext, "Empate", Toast.LENGTH_SHORT).show()
                       }
                       if (usuarioGanador!!.id == idUsuariLogged){
                           Toast.makeText(applicationContext, "Has ganado", Toast.LENGTH_SHORT).show()
                           movieAPI.editarPartida(partida.partidaId!!, -1 , user2time)
                       }
                       else {
                           Toast.makeText(applicationContext, "Has perdido", Toast.LENGTH_SHORT).show()
                           movieAPI.editarPartida(partida.partidaId!!, -2 , user2time)
                       }
                   }
                }
                else if (user1time < 0 && user2time > 0){

                }
               else if (user1time > 0 && user2time < 0){
                   if (user2time == -1){
                       Toast.makeText(applicationContext, "Has perdido", Toast.LENGTH_SHORT).show()
                       CoroutineScope(Dispatchers.Main).launch {
                           movieAPI.editarPartida(partida.partidaId!!, -2 , user2time)
                       }
                   }
                   else if (user2time == -2){
                       Toast.makeText(applicationContext, "Has ganado", Toast.LENGTH_SHORT).show()
                       CoroutineScope(Dispatchers.Main).launch {
                       movieAPI.editarPartida(partida.partidaId!!, -1 , user2time)
                       }
                   }
               }
                 if (user1time < 0 && user2time < 0){
                     CoroutineScope(Dispatchers.Main).launch {
                     movieAPI.deletePartida(partida.partidaId!!)
                     }

                 }

            }
// usuario2
            else if (partida.usuario2Id == idUsuariLogged) {
                var user1time = convertTimeStringToSeconds(partida.tiempoUsuario1)
                var user2time = convertTimeStringToSeconds(partida.tiempoUsuario2)
                if (partida.tiempoUsuario2 == "00:00:00") {
                    builder.setTitle("Tienes una partida pendiente")

                    builder.setMessage("¿Quieres continuar la partida?")

                    builder.setPositiveButton("Aceptar") { dialog, _ ->
                        PartidaPendent = partida
                        val intent = Intent(this@MenuPrincipal, JocVSActivity::class.java)
                        intent.putExtra("partida", partida as Parcelable)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }else if(partida.tiempoUsuario1 == "00:00:00") {
                }
                else if( user1time > 0 && user2time > 0 ){
                    CoroutineScope(Dispatchers.Main).launch {
                        val usuarioGanador = movieAPI.comprobarGuanyadors(partida.partidaId!!)
                        if(usuarioGanador!!.alias == null){
                            Toast.makeText(applicationContext, "Empate", Toast.LENGTH_SHORT).show()
                        }
                        if (usuarioGanador!!.id == idUsuariLogged){
                            Toast.makeText(applicationContext, "Has ganado", Toast.LENGTH_SHORT).show()
                            movieAPI.editarPartida(partida.partidaId!!, user1time , -1)

                        }
                        else {
                            Toast.makeText(applicationContext, "Has perdido", Toast.LENGTH_SHORT).show()
                            movieAPI.editarPartida(partida.partidaId!!, user1time ,-2)

                        }
                    }
                }
                else if (user1time > 0 && user2time < 0){

                }
                else if (user1time < 0 && user2time > 0){
                    if (user1time == -1){
                        Toast.makeText(applicationContext, "Has perdido", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            movieAPI.editarPartida(partida.partidaId!!, user1time ,-2)
                        }
                    }
                    else if (user1time == -2){
                        Toast.makeText(applicationContext, "Has ganado", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            movieAPI.editarPartida(partida.partidaId!!, user1time ,-1)
                        }
                    }
                }
                if (user1time < 0 && user2time < 0){
                    CoroutineScope(Dispatchers.Main).launch {
                        movieAPI.deletePartida(partida.partidaId!!)
                    }
                }

            }

        }
    }
    fun convertTimeStringToSeconds(time: String): Int {
        val isNegative = time.startsWith("-")
        val absoluteTime = if (isNegative) time.substring(1) else time

        val parts = absoluteTime.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()
        val totalSeconds = hours * 3600 + minutes * 60 + seconds

        return if (isNegative) -totalSeconds else totalSeconds
    }

    private fun mostrarInvitaciones(invitaciones: List<Invitation>) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val idUsuariLogged = sharedPreferences.getInt("idUsuariLogged", -1)

        for (invitacion in invitaciones) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Aceptar desafío?")

            builder.setMessage(invitacion.message)

            builder.setPositiveButton("Aceptar") { dialog, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    val idUsuariDesafiar = invitacion.senderId ?: -1
                    if (idUsuariDesafiar != -1) {
                        movieAPI.acceptarInvitacio(invitacion.invitationId, idUsuariLogged, idUsuariDesafiar, true, true, invitacion.message)
                        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val idUsuariActual = sharedPreferences.getInt("receiverId", -1)
                        val idUsuariDesafiar = sharedPreferences.getInt("senderId", -1)

                        var pos = Random.nextInt(1, 4803)
                        allthingsAdivinar = movieAPI.getAllThingsAdivinar(2)

                        var partidaNova = movieAPI.crearPartida(allthingsAdivinar!!.movie.movie_id, idUsuariActual, idUsuariDesafiar)

                        movieAPI.deleteInvitation(invitacion.invitationId)

                        val intent = Intent(this@MenuPrincipal, JocVSActivity::class.java)
                        startActivity(intent)
                    }
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    movieAPI.deleteInvitation(invitacion.invitationId)
                }
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun aplicarBorros(bitmap: Bitmap, radius: Float, context: Context): Bitmap {

        val rsContext = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rsContext, bitmap)

        val output = Allocation.createTyped(rsContext, input.type)

        val script = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))

        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)

        rsContext.finish()

        return bitmap
    }

}
package com.example.filmsdle

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.Invitation
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.ActivitySeleccioVsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class seleccioVS : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccioVsBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: AdaptadorVS
    private var usuaris: MutableList<User> = mutableListOf()
    var nomUsuari = ""

    val movieAPI = MovieAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeleccioVsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)
        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)
        findViewById<ImageView>(R.id.imatgesFonsVS).setImageBitmap(blurredBackground)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = AdaptadorVS(usuaris, this)
        recyclerView = findViewById(R.id.recyclerViewUsuaris)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        binding.butbuscar.setOnClickListener {

            val nomUsuari = binding.tbNomUsuari.text.toString()

            GlobalScope.launch(Dispatchers.IO) {

                val user = movieAPI.getUsuarisDesafiar(nomUsuari)

                val sharedPreferencesName: SharedPreferences =
                    getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val nomUsuariLogged = sharedPreferencesName.getString("NomUsuariLogged", "")

                withContext(Dispatchers.Main) {

                    if (user != null) {
                        if (user.alias.equals(nomUsuariLogged)) {
                            Toast.makeText(
                                applicationContext,
                                "No et pots buscar a tu mateix",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            usuaris.clear()
                            usuaris.add(user)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "No s'ha trobat l'usuari",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
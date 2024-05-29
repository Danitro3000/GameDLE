package com.example.filmsdle

import FilmAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.BD.Models.Partida
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.ActivityJocBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.list
import kotlin.random.Random

class JocActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJocBinding
    private lateinit var recyclerView: RecyclerView

    var allthingsAdivinar: AllThings? = null
    private var todo: MutableList<AllThings> = mutableListOf()
    var nomPeli = ""

    private lateinit var adapter: FilmAdapter
    private var colors: MutableList<Int> = mutableListOf()

    private val movieAPI = MovieAPI()

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var movies: List<Movie>

    private var startTime: Int = 0
    private var elapsedSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJocBinding.inflate(layoutInflater)
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
        findViewById<ImageView>(R.id.imageView4).setImageBitmap(blurredBackground)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = FilmAdapter(todo, colors, allthingsAdivinar)
        recyclerView = findViewById(R.id.recyclerViewJoc)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        binding.butbuscar.setOnClickListener {

            nomPeli = binding.tbBuscar.text.toString()

            GlobalScope.launch(Dispatchers.IO) {

                val pelicula = movieAPI.getMovieByName(nomPeli)

                withContext(Dispatchers.Main) {

                    if (pelicula != null) {

                        todo.add(pelicula)

                        val tituloCorrecto = pelicula.movie.title.equals(allthingsAdivinar?.movie?.title)
                        val color = if (tituloCorrecto) Color.GREEN else Color.RED

                        if (tituloCorrecto) {

                            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val idUsuariActual = sharedPreferences.getInt("idUsuariLogged", -1)

                            elapsedSeconds = (System.currentTimeMillis() - startTime).toInt() / 1000

                            movieAPI.assignarPuntuacio(idUsuariActual, elapsedSeconds,true)

                            val builder = AlertDialog.Builder(this@JocActivity)
                            builder.setMessage("HAS GUANYAT!!")
                                .setCancelable(false)
                                .setPositiveButton("OK") { dialog, _ ->
                                    val intent = Intent(this@JocActivity, MenuPrincipal::class.java)
                                    startActivity(intent)
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                        }

                        colors.add(color)
                        adapter.notifyDataSetChanged()

                        Toast.makeText(
                            applicationContext,
                            if (tituloCorrecto) "Película encontrada" else "Película no encontrada",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "No se encontró la película",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        autoCompleteTextView = findViewById(R.id.tb_buscar)

        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedMovie = movies[position]
            Toast.makeText(this, "Has seleccionado: ${selectedMovie.title}", Toast.LENGTH_SHORT)
                .show()
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getPeliculasAsociadas(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showMovieSuggestions(movieList: List<Movie>) {
        val movieTitles = movieList.map { it.title }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, movieTitles)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun getPeliculasAsociadas(titulo: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val peliculas = movieAPI.getPeliculasAsociadas(titulo)
            peliculas?.let {
                movies = it
                showMovieSuggestions(movies)
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        var pos = Random.nextInt(1, 4803)

        GlobalScope.launch(Dispatchers.IO) {

            allthingsAdivinar = movieAPI.getAllThingsAdivinar(2)

            withContext(Dispatchers.Main) {
                if (allthingsAdivinar != null) {
                    Toast.makeText(applicationContext, "Pelicula per adivinar carregada!!", Toast.LENGTH_SHORT)
                        .show()
                    adapter = FilmAdapter(todo, colors, allthingsAdivinar)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error per carregar peli per adivinar!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
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
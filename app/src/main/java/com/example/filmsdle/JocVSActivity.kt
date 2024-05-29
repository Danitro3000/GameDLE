package com.example.filmsdle

import FilmAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.BD.Models.Partida
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.ActivityJocVsactivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class JocVSActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJocVsactivityBinding
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
    var elapsedSeconds: Int = 0
    var countDownTimer: CountDownTimer? = null
    var partidaNova: Partida? = null
    var tiempo1 = 0
    var tiempo2 = 0

    fun startTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedSeconds++

            }

            override fun onFinish() {
                // Este método se llamará cuando el temporizador termine, pero en este caso, hemos establecido el tiempo a Long.MAX_VALUE, por lo que no debería terminar
            }
        }
        countDownTimer?.start()
    }
    fun stopTimer() {
        countDownTimer?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTimer()

        binding = ActivityJocVsactivityBinding.inflate(layoutInflater)
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
        findViewById<ImageView>(R.id.fonsVS).setImageBitmap(blurredBackground)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = FilmAdapter(todo, colors, allthingsAdivinar)
        recyclerView = findViewById(R.id.recyclerViewJocVS)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        binding.butbuscarvs.setOnClickListener {

            nomPeli = binding.tbBuscarVS.text.toString()

            GlobalScope.launch(Dispatchers.IO) {

                val pelicula = movieAPI.getMovieByName(nomPeli)

                withContext(Dispatchers.Main) {

                    if (pelicula != null) {

                        todo.add(pelicula)

                        val tituloCorrecto = pelicula.movie.title.equals(allthingsAdivinar?.movie?.title)
                        val color = if (tituloCorrecto) Color.GREEN else Color.RED

                        if (tituloCorrecto) {
                            stopTimer()

                            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val idUsuariActual = sharedPreferences.getInt("idUsuariLogged", -1)

                            movieAPI.assignarPuntuacio(idUsuariActual, elapsedSeconds,true)

                            if (idUsuariActual == partidaNova?.usuario1Id) {
                              var resultado = convertSecondsToTimeString(elapsedSeconds)
                                partidaNova?.tiempoUsuario1 = resultado
                                val tiempoUsuario2 = convertTimeStringToSeconds(partidaNova!!.tiempoUsuario2!!)
                                try {
                                    movieAPI.editarPartida(partidaNova!!.partidaId!!, elapsedSeconds , tiempoUsuario2)


                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }

                            } else if (idUsuariActual == partidaNova?.usuario2Id) {
                                var resultado = convertSecondsToTimeString(elapsedSeconds)
                                partidaNova?.tiempoUsuario2 = resultado
                                val tiempoUsuario1 = convertTimeStringToSeconds(partidaNova!!.tiempoUsuario1!!)
                                try {
                                    movieAPI.editarPartida(partidaNova!!.partidaId!!, tiempoUsuario1 ,elapsedSeconds)


                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            }
                        }

                        colors.add(color)
                        adapter.notifyDataSetChanged()

                        Toast.makeText(applicationContext, if (tituloCorrecto) "Película encontrada" else "Película no encontrada", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(applicationContext, "No se encontró la película", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        autoCompleteTextView = findViewById(R.id.tb_buscarVS)

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
    fun convertSecondsToTimeString(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
    fun convertTimeStringToSeconds(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()
        return hours * 3600 + minutes * 60 + seconds
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

                withContext(Dispatchers.Main) {
                    partidaNova = intent.getParcelableExtra<Partida>("partida")
                }

            allthingsAdivinar = movieAPI.getAllThingsAdivinar(2)

            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val idUsuariActual = sharedPreferences.getInt("receiverId", -1)
            val idUsuariDesafiar = sharedPreferences.getInt("senderId", -1)


            withContext(Dispatchers.Main) {
                if (allthingsAdivinar != null) {
                    Toast.makeText(applicationContext, "Pelicula per adivinar carregada!!", Toast.LENGTH_SHORT).show()
                    adapter = FilmAdapter(todo, colors, allthingsAdivinar)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(applicationContext, "Error per carregar peli per adivinar!!", Toast.LENGTH_SHORT).show()
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
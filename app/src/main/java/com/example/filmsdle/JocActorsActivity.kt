package com.example.filmsdle

import ActorsAdapter
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.ActorMoviesDTO
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.ActivityJocActorsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class JocActorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJocActorsBinding
    private lateinit var recyclerView: RecyclerView

    var actorAdivinar: ActorMoviesDTO? = null
    private var actors: MutableList<ActorMoviesDTO> = mutableListOf()
    var nomActor = ""

    private lateinit var adapter: ActorsAdapter
    private var colors: MutableList<Int> = mutableListOf()

    val movieAPI = MovieAPI()

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var actorsAsociats: List<ActorMoviesDTO>

    private var startTime: Int = 0
    private var elapsedSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJocActorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)
        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)
        findViewById<ImageView>(R.id.imageView4).setImageBitmap(blurredBackground)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ActorsAdapter(actors, colors, actorAdivinar)
        recyclerView = findViewById(R.id.recyclerViewJocActors)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        binding.butbuscar.setOnClickListener {

            nomActor = binding.tbBuscar.text.toString()

            GlobalScope.launch(Dispatchers.IO) {

                val actor = movieAPI.getActorByName(nomActor)

                withContext(Dispatchers.Main) {

                    if (actor != null) {

                        actors.add(actor)

                        val actorCorrecte = actor.actorName.equals(actorAdivinar?.actorName)
                        val color = if (actorCorrecte) Color.GREEN else Color.RED

                        if (actorCorrecte) {

                            val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val idUsuariActual = sharedPreferences.getInt("idUsuariLogged", -1)

                            elapsedSeconds = (System.currentTimeMillis() - startTime).toInt() / 1000

                            movieAPI.assignarPuntuacio(idUsuariActual, elapsedSeconds,true)

                            val builder = AlertDialog.Builder(this@JocActorsActivity)
                            builder.setMessage("HAS GUANYAT!!")
                                .setCancelable(false)
                                .setPositiveButton("OK") { dialog, _ ->
                                    val intent = Intent(this@JocActorsActivity, MenuPrincipal::class.java)
                                    startActivity(intent)
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                        }

                        colors.add(color)
                        adapter.notifyDataSetChanged()

                    } else {
                        Toast.makeText(applicationContext, "No s'ha trobat l'actor", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        autoCompleteTextView = findViewById(R.id.tb_buscar)

        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedMovie = actorsAsociats[position]
            Toast.makeText(this, "Has seleccionat: ${selectedMovie.actorName}", Toast.LENGTH_SHORT)
                .show()
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getActorsAsociats(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showMovieSuggestions(actorsList: List<ActorMoviesDTO>) {
        val movieTitles = actorsList.map { it.actorName }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, movieTitles)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun getActorsAsociats(titulo: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val peliculas = movieAPI.getActorAsociats(titulo)
            peliculas?.let {
                actorsAsociats = it
                showMovieSuggestions(actorsAsociats)
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        GlobalScope.launch(Dispatchers.IO) {
            var actorAdivinar: ActorMoviesDTO? = null
            var idActor: Int

            while (actorAdivinar == null) {
                idActor = Random.nextInt(1, 5635)
                actorAdivinar = movieAPI.getActorsAdivinar(idActor)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Actor/actriu per adivinar carregat/da!!", Toast.LENGTH_SHORT).show()
                adapter = ActorsAdapter(actors, colors, actorAdivinar)
                recyclerView.adapter = adapter
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



package com.example.filmsdle

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.filmsdle.BD.Models.User
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.api.MovieApiCalls
import com.example.filmsdle.databinding.ActivityLoginBinding
import com.example.filmsdle.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val movieAPI = MovieAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)

        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)

        findViewById<ImageView>(R.id.imatgeFons).setImageBitmap(blurredBackground)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.butRegister.setOnClickListener {

            val nombre = binding.tbName.text.toString()
            val alias = binding.tbUsername.text.toString()
            val email = binding.tbEmail.text.toString()
            val password = binding.tbPassword.text.toString()

            GlobalScope.launch(Dispatchers.Main) {

                try {
                    movieAPI.postUser(nombre, alias, email, password)

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)

                } catch (e: IOException) {
                    Log.e("NetworkException", "Error de red: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("HttpException", "Error HTTP: ${e.code()}")
                } catch (e: Exception) {
                    Log.e("Exception", "Error inesperado: ${e.message}")
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
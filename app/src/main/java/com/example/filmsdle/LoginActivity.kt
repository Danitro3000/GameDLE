package com.example.filmsdle

import android.content.Context
import android.content.Intent
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.filmsdle.databinding.ActivityLoginBinding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.*
import android.widget.ImageView
import com.example.filmsdle.api.MovieAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.SharedPreferences


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val movieAPI = MovieAPI()
    var idUsuariLogged = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)
        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)
        findViewById<ImageView>(R.id.fons).setImageBitmap(blurredBackground)

        binding.Login.setOnClickListener {

            val password = binding.tbPassword.text.toString()
            val emailOrAlias = binding.tbUsername.text.toString()

            CoroutineScope(Dispatchers.Main).launch {

                val user = movieAPI.getUser(password, emailOrAlias)

                if (user != null) {
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("idUsuariLogged", user.id)
                    editor.apply()

                    val sharedPreferencesName = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editorName = sharedPreferencesName.edit()
                    editorName.putString("NomUsuariLogged", user.alias)
                    editorName.apply()

                    val intent = Intent(this@LoginActivity, MenuPrincipal::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: Usuari no trobat",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.buttonForgot.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val emailOrAlias = binding.tbUsername.text.toString()
                movieAPI.forgotPassword(emailOrAlias)

            }
        }

        binding.Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
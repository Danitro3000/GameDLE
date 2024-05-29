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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.filmsdle.databinding.ActivitySeleccioJocBinding

class SeleccioJoc : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccioJocBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeleccioJocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imatgeFons = BitmapFactory.decodeResource(resources, R.drawable.logo_fons)
        val blurredBackground = aplicarBorros(imatgeFons, 25f, applicationContext)
        findViewById<ImageView>(R.id.imatgeFonsSeleccio).setImageBitmap(blurredBackground)

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

        binding.modeClassic.setOnClickListener{
            val intent = Intent(this, JocActivity::class.java)
            startActivity(intent)
        }

        binding.modeActors.setOnClickListener {
            val intent = Intent(this, JocActorsActivity::class.java)
            startActivity(intent)
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
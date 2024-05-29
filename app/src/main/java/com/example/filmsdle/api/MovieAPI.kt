package com.example.filmsdle.api

import android.util.Log
import android.widget.AutoCompleteTextView
import com.example.filmsdle.BD.Models.Actor
import com.example.filmsdle.BD.Models.ActorMoviesDTO
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Invitation
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.BD.Models.Partida
import com.example.filmsdle.BD.Models.User
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.Duration

class MovieAPI {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://prepared-louse-modest.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val movieApiCalls = retrofit.create(MovieApiCalls::class.java)

    suspend fun getAllThingsAdivinar(pos: Int): AllThings? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getAllthingsAdivinar(pos).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getMovieByName(nom: String): AllThings? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getMovieByNom(nom).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getPeliculasAsociadas(titol: String): List<Movie>? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getPeliculasAsociadas(titol).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun assignarPuntuacio(userId: Int, segons: Int, haAcabat: Boolean): User? {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiCalls.assignarPuntuacio(userId, segons, haAcabat).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }


    suspend fun postUser(nombre: String, alias: String, email: String, password: String): User? {
        return try {
            val response = withContext(Dispatchers.IO) {
                movieApiCalls.postUser(nombre, alias, email, password).execute()
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUser(password: String, aliasorEmail: String): User? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getUser(password, aliasorEmail).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getActorsAdivinar(pos: Int): ActorMoviesDTO? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getActorAdivinar(pos).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getActorByName(nom: String): ActorMoviesDTO? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getActorByNom(nom).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getActorAsociats(titol: String): List<ActorMoviesDTO>? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getActorsAsociats(titol).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getUsuarisDesafiar(alias: String): User? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getUsarisPerJugar(alias).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun enviarInvitacio(senderId: Int, receiverId: Int, invSend: Boolean, invRec: Boolean, message: String): Invitation? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.enviarInvitacio(senderId, receiverId, invSend, invRec, message).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getInvitacions(): List<Invitation>? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getInvitacions().execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun acceptarInvitacio(id: Int, senderId: Int, receiverId: Int, invSend: Boolean, invRec: Boolean, message: String): Invitation? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.acceptarInvitacio(id, senderId, receiverId, invSend, invRec, message).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deleteInvitation(id: Int): Invitation? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.deleteInvitation(id).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getRankings(): List<User>? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getRankings().execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun comprobarGuanyadors(id: Int): User? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.comprobarGuanyador(id).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun crearPartida(idPartida: Int, idusuari1: Int, idusuari2: Int): Partida? {
        return try {
            val response = withContext(Dispatchers.IO) {
                movieApiCalls.crearPartida(idPartida, idusuari1, idusuari2).execute()
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun editarPartida(partidaId: Int, tiempoUsusario1: Int, tiempoUsusario2: Int): Partida? {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiCalls.modificarPartida(partidaId, tiempoUsusario1, tiempoUsusario2).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getPartidaById(id: Int): List<Partida>? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.getPartida(id).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun deletePartida(id: Int): Partida? {
        val response = withContext(Dispatchers.IO) {
            movieApiCalls.deletePartida(id).execute()
        }
        return if (response.isSuccessful) response.body() else null
    }
    suspend fun forgotPassword(emailOrAlias: String): User? {
        try {


        val response = withContext(Dispatchers.IO) {
            movieApiCalls.forgotPassword(emailOrAlias).execute()
        }
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null && responseBody.toString().trim().isNotEmpty()) {
                return responseBody
            } else {
                Log.e("MovieAPI", "Response body is empty")
            }
        } else {
            Log.e("MovieAPI", "Response was not successful: ${response.errorBody()?.string()}")
        }
        return null
        }
        catch (e: Exception) {
            Log.e("MovieAPI", "Error: ${e.message}")
            return null
        }
    }

}

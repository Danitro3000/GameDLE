package com.example.filmsdle.api
import android.widget.AutoCompleteTextView
import androidx.room.Query
import com.example.filmsdle.BD.Models.Actor
import com.example.filmsdle.BD.Models.ActorMoviesDTO
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Invitation
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.BD.Models.Partida
import com.example.filmsdle.BD.Models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MovieApiCalls {
    @GET("Movies/all-in/nom/{nom}")
    fun getMovieByNom(@Path("nom") nom: String): Call<AllThings>

    @GET("Movies/all-in/posiblepos/{position}")
    fun getAllthingsAdivinar(@Path("position") id: Int): Call<AllThings>

    @GET("/Movies/all-in/actor-nameDTOID/{actorId}")
    fun getActorAdivinar(@Path("actorId") id: Int): Call<ActorMoviesDTO>

    @GET("Movies/all-in/actor-nameDTO/{actorName}")
    fun getActorByNom(@Path("actorName") nom: String): Call<ActorMoviesDTO>

    @GET("Movies/all-in/actor-posiblenameDTO/{actorName}")
    fun getActorsAsociats(@Path("actorName") nom: String): Call<List<ActorMoviesDTO>>

    @PUT("/Movies/players/score/{userId}/{timeInSeconds}/{hasFinished}")
    fun assignarPuntuacio(@Path("userId") userId: Int, @Path("timeInSeconds") segons: Int, @Path("hasFinished") hasFinished: Boolean): Call<User>

    @GET("Movies/movies/moviestart/{name}")
    fun getPeliculasAsociadas(@Path("name") nom: String): Call<List<Movie>>

    @GET("Users/Users/usuarios")
    fun getRankings(): Call<List<User>>

    @POST("Users/Users/MakeUser/{name}/{alias}/{email}/{password}")
    fun postUser(@Path("name") nom: String, @Path("alias") alias: String, @Path("email") email: String, @Path("password") password: String): Call<User>

    @GET("Users/Users/password/{password}/{emailorAlias}")
    fun getUser(@Path("password") password: String, @Path("emailorAlias") emailorAlias: String): Call<User>

    @GET("/Users/Users/alias/{alias}")
    fun getUsarisPerJugar(@Path("alias") alias: String): Call<User>

    @POST("/Users/Users/usuarios/invitations/sendinvitation/{senderId}/{receiverId}/{invSend}/{invRec}/{message}")
    fun enviarInvitacio(@Path("senderId") senderId: Int, @Path("receiverId") receiverId: Int, @Path("invSend") invSend: Boolean, @Path("invRec") invRec: Boolean, @Path("message") message: String): Call<Invitation>

    @GET("/Users/Users/usuarios/invitations")
    fun getInvitacions(): Call<List<Invitation>>

    @PUT("/Users/Users/usuarios/invitations/editinvitation/{id}/{senderId}/{receiverId}/{invSend}/{invRec}/{message}")
    fun acceptarInvitacio(@Path("id") id: Int, @Path("senderId") senderId: Int, @Path("receiverId") receiverId: Int, @Path("invSend") invSend: Boolean, @Path("invRec") invRec: Boolean, @Path("message") message: String): Call<Invitation>

    @DELETE("/Users/Users/invitations/{id}")
    fun deleteInvitation(@Path("id") id: Int): Call<Invitation>

    @POST("/Partida/ruta/postpartida/{peliculaId}/{usuario1Id}/{usuario2Id}")
    fun crearPartida(@Path("peliculaId") peliculaId: Int, @Path("usuario1Id") usuario1Id: Int, @Path("usuario2Id") usuario2Id: Int): Call<Partida>

    @PUT("/Partida/ruta/putpartida/{partidaId}/{tiempoUsuario1}/{tiempoUsuario2}")
    fun modificarPartida(@Path("partidaId") partidaId: Int, @Path("tiempoUsuario1") tiempo1: Int, @Path("tiempoUsuario2") tiempo2: Int): Call<Partida>

    @GET("/Partida/ruta/ganador/{id}")
    fun comprobarGuanyador(@Path("id") id: Int): Call<User>

    @GET("/Partida/ruta/Usuario/{position}")
    fun getPartida(@Path("position") id: Int): Call<List<Partida>>

    @DELETE("/Partida/ruta/{id}")
    fun deletePartida(@Path("id") id: Int): Call<Partida>

    @POST("/Users/Users/forgot-password")
    fun forgotPassword(@Body emailOrAlias: String): Call<User>

}
package com.example.filmsdle.BD.Models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.filmsdle.BD.Models.Actor

class Converters {
    @TypeConverter
    fun fromActorList(value: List<Actor>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Actor>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toActorList(value: String): List<Actor> {
        val gson = Gson()
        val type = object : TypeToken<List<Actor>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromCompanyList(value: List<Company>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Company>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCompanyList(value: String): List<Company> {
        val gson = Gson()
        val type = object : TypeToken<List<Company>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromCountryList(value: List<Country>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountryList(value: String): List<Country> {
        val gson = Gson()
        val type = object : TypeToken<List<Country>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        val gson = Gson()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromLanguageList(value: List<Language>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Language>>() {}.type
        return gson.toJson(value, type)
    }
    @TypeConverter
    fun toLanguageList(value: String): List<Language> {
        val gson = Gson()
        val type = object : TypeToken<List<Language>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromPapelList(value: List<Papel>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Papel>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPapelList(value: String): List<Papel> {
        val gson = Gson()
        val type = object : TypeToken<List<Papel>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromMovie(value: Movie): String {
        val gson = Gson()
        val type = object : TypeToken<Movie>() {}.type
        return gson.toJson(value, type)
    }
    @TypeConverter
    fun toMovie(value: String): Movie {
        val gson = Gson()
        val type = object : TypeToken<Movie>() {}.type
        return gson.fromJson(value, type)
    }

}
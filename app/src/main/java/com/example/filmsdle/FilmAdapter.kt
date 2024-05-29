import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.BD.Models.Company
import com.example.filmsdle.BD.Models.Movie
import com.example.filmsdle.api.MovieAPI
import com.example.filmsdle.databinding.CardfilmsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FilmAdapter(private val films: List<AllThings>, private val colors: List<Int>, private val peliculaAdivinar: AllThings?) : RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(private val binding: CardfilmsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(film: AllThings, color: Int, peliculaAdivinar: AllThings?) {

            // TITOL
            binding.filmTitle.text = film.movie.title
            binding.filmTitle.setBackgroundColor(color)

            binding.filmTitle.setOnClickListener {
                if (binding.llExpandable.visibility == android.view.View.GONE) {
                    binding.llExpandable.visibility = android.view.View.VISIBLE
                    binding.filmTitle.text = film.movie.title
                } else {
                    binding.llExpandable.visibility = android.view.View.GONE
                    binding.filmTitle.text = film.movie.title
                }
            }
            // NOMCOMPANY
            val nomCompany = film.company.joinToString { it.company_Name }

            val printCompany = SpannableStringBuilder("Productora -> $nomCompany")
            printCompany.setSpan(StyleSpan(Typeface.BOLD), 0, "Productora -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.companyName.text = printCompany

            if (peliculaAdivinar != null && film.company.all { company ->
                    peliculaAdivinar.company.any { it.company_Name == company.company_Name }
                }) {
                binding.companyName.setBackgroundColor(Color.GREEN)
            } else if (peliculaAdivinar != null && film.company.any { company ->
                    peliculaAdivinar.company.any { it.company_Name == company.company_Name }
                }) {
                binding.companyName.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.companyName.setBackgroundColor(Color.RED)
            }
            // NOMGENERE
            val nomGenere = film.genre.joinToString { it.genre_Name }

            val printGenere = SpannableStringBuilder("Genere -> $nomGenere")
            printGenere.setSpan(StyleSpan(Typeface.BOLD), 0, "Genere -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.genreName.text = printGenere

            if (peliculaAdivinar != null && film.genre.all { genre ->
                    peliculaAdivinar.genre.any { it.genre_Name == genre.genre_Name }
                }) {
                binding.genreName.setBackgroundColor(Color.GREEN)
            } else if (peliculaAdivinar != null && film.genre.any { genre ->
                    peliculaAdivinar.genre.any { it.genre_Name == genre.genre_Name }
                }) {
                binding.genreName.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.genreName.setBackgroundColor(Color.RED)
            }
            // PAISPELI
            val countryName = film.country.joinToString { it.country_Name }

            val printCountry = SpannableStringBuilder("Paisos grabació -> $countryName")
            printCountry.setSpan(StyleSpan(Typeface.BOLD), 0, "Paisos grabació -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.paisPeli.text = printCountry

            if (peliculaAdivinar != null && film.country.all { country ->
                    peliculaAdivinar.country.any { it.country_Name == country.country_Name }
                }) {
                binding.paisPeli.setBackgroundColor(Color.GREEN)
            } else if (peliculaAdivinar != null && film.country.any { country ->
                    peliculaAdivinar.country.any { it.country_Name == country.country_Name }
                }) {
                binding.paisPeli.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.paisPeli.setBackgroundColor(Color.RED)
            }
            // IDIOMAPELI
            val idiomaPeli = film.language.joinToString { it.language_Name }

            val printIdioma = SpannableStringBuilder("Idiomes disponibles -> $idiomaPeli")
            printIdioma.setSpan(StyleSpan(Typeface.BOLD), 0, "Idiomes disponibles -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.language.text = printIdioma

            if (peliculaAdivinar != null && film.language.all { language ->
                    peliculaAdivinar.language.any { it.language_Name == language.language_Name }
                }) {
                binding.language.setBackgroundColor(Color.GREEN)
            } else if (peliculaAdivinar != null && film.language.any { language ->
                    peliculaAdivinar.language.any { it.language_Name == language.language_Name }
                }) {
                binding.language.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.language.setBackgroundColor(Color.RED)
            }
            // ACTORS
            val actors = film.actor.joinToString { it.person_Name }

            val printActors = SpannableStringBuilder("Actors -> $actors")
            printActors.setSpan(StyleSpan(Typeface.BOLD), 0, "Actors -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.actores.text = printActors

            if (peliculaAdivinar != null && film.actor.all { actor ->
                    peliculaAdivinar.actor.any { it.person_Name == actor.person_Name }
                }) {
                binding.actores.setBackgroundColor(Color.GREEN)
            } else if (peliculaAdivinar != null && film.actor.any { actor ->
                    peliculaAdivinar.actor.any { it.person_Name == actor.person_Name }
                }) {
                binding.actores.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.actores.setBackgroundColor(Color.RED)
            }
            // POPULARITAT
            val popularityDiff = film.movie.vote_average - (peliculaAdivinar?.movie?.vote_average ?: 0.0)
            val popularityText = when {
                popularityDiff > 0.0 -> "${film.movie.vote_average} ↓"
                popularityDiff < 0.0 -> "${film.movie.vote_average} ↑"
                else -> {
                    binding.filmPopularity.setBackgroundColor(Color.GREEN)
                    "${film.movie.vote_average}"
                }
            }
            val printNota = SpannableStringBuilder("Nota -> $popularityText")
            printNota.setSpan(StyleSpan(Typeface.BOLD), 0, "Nota -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.filmPopularity.text = printNota
            // DATA DE LLANÇAMENT
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val releaseDate = dateFormat.parse(film.movie.release_Date)
            val pelAdivinarReleaseDate = dateFormat.parse(peliculaAdivinar?.movie?.release_Date ?: "")

            val filmReleaseDiff = releaseDate?.time?.minus(pelAdivinarReleaseDate?.time ?: 0) ?: 0
            val filmReleaseText = when {
                filmReleaseDiff > 0 -> "${film.movie.release_Date} ↓"
                filmReleaseDiff < 0 -> "${film.movie.release_Date} ↑"
                else -> {
                    binding.filmReleaseDate.setBackgroundColor(Color.GREEN)
                    film.movie.release_Date
                }
            }
            val printData = SpannableStringBuilder("Data d'estrena -> $filmReleaseText")
            printData.setSpan(StyleSpan(Typeface.BOLD), 0, "Data d'estrena -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.filmReleaseDate.text = printData
            // DURADA
            val runtimeDiff = film.movie.runtime - (peliculaAdivinar?.movie?.runtime ?: 0)
            val runtimeText = when {
                runtimeDiff > 0 -> "${film.movie.runtime} ↓"
                runtimeDiff < 0 -> "${film.movie.runtime} ↑"
                else -> {
                    binding.filmRuntime.setBackgroundColor(Color.GREEN)
                    "${film.movie.runtime}"
                }
            }
            val printDurada = SpannableStringBuilder("Durada(mins) -> $runtimeText")
            printDurada.setSpan(StyleSpan(Typeface.BOLD), 0, "Durada(mins) -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.filmRuntime.text = printDurada
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = CardfilmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val movie = films[position]
        val color = colors[position]
        holder.bind(movie,color, peliculaAdivinar)
    }

    override fun getItemCount(): Int = films.size
}

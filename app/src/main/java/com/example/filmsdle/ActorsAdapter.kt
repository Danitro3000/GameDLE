import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsdle.BD.Models.ActorMoviesDTO
import com.example.filmsdle.BD.Models.AllThings
import com.example.filmsdle.databinding.CardactorsBinding

class ActorsAdapter(private val actors: List<ActorMoviesDTO>, private val colors: List<Int>, private val actorAdivinar: ActorMoviesDTO?) : RecyclerView.Adapter<ActorsAdapter.ActorViewHolder>() {

     class ActorViewHolder(private val binding: CardactorsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: ActorMoviesDTO, color: Int, actorAdivinar: ActorMoviesDTO?) {

            binding.actorName.text = actor.actorName
            binding.actorName.setBackgroundColor(color)

            binding.actorName.setOnClickListener {
                if (binding.llExpandable.visibility == View.GONE) {
                    binding.llExpandable.visibility = View.VISIBLE
                    binding.actorName.text = actor.actorName
                } else {
                    binding.llExpandable.visibility = View.GONE
                    binding.actorName.text = actor.actorName
                }
            }

            val nomPersonatges = actor.moviesAndCharacters.joinToString { it.characterName }

            val printPj = SpannableStringBuilder("Personatges Interpretats -> $nomPersonatges")
            printPj.setSpan(StyleSpan(Typeface.BOLD), 0, "Personatges Interpretats -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.characterName.text = printPj

            if (actorAdivinar != null && actor.moviesAndCharacters.all { pjs ->
                    actorAdivinar.moviesAndCharacters.any { it.characterName == pjs.characterName }
                }) {
                binding.characterName.setBackgroundColor(Color.GREEN)
            } else if (actorAdivinar != null && actor.moviesAndCharacters.any { pjs ->
                    actorAdivinar.moviesAndCharacters.any { it.characterName == pjs.characterName }
                }) {
                binding.characterName.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.characterName.setBackgroundColor(Color.RED)
            }

            val pelis = actor.moviesAndCharacters.joinToString { it.movieTitle }

            val printPelis = SpannableStringBuilder("Participació Pelicules -> $pelis")
            printPelis.setSpan(StyleSpan(Typeface.BOLD), 0, "Participació Pelicules -> ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.pelisQueHaParticipat.text = printPelis

            if (actorAdivinar != null && actor.moviesAndCharacters.all { pelicules ->
                    actorAdivinar.moviesAndCharacters.any { it.movieTitle == pelicules.movieTitle }
                }) {
                binding.pelisQueHaParticipat.setBackgroundColor(Color.GREEN)
            } else if (actorAdivinar != null && actor.moviesAndCharacters.any { pelicules ->
                    actorAdivinar.moviesAndCharacters.any { it.movieTitle == pelicules.movieTitle }
                }) {
                binding.pelisQueHaParticipat.setBackgroundColor(Color.parseColor("#FFA500"))
            } else {
                binding.pelisQueHaParticipat.setBackgroundColor(Color.RED)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = CardactorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = actors[position]
        val color = colors[position]
        holder.bind(actor, color, actorAdivinar)
    }

    override fun getItemCount(): Int = actors.size

}
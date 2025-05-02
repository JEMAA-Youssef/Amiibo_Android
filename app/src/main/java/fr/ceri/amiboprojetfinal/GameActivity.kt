package fr.ceri.amiboprojetfinal

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import fr.ceri.amiboprojetfinal.databinding.ActivityGameBinding
import fr.ceri.amiboprojetfinal.model.Amiibo
import fr.ceri.amiboprojetfinal.utils.OnSwipeTouchListener
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var realm: Realm
    private var score = 0
    private var scoreMenuItem: MenuItem? = null
    private var forcedQuestionType: Boolean? = null
    private var correctAnswer = ""
    private var isNameQuestion = true
    private var questionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApp).stopBackgroundMusic()
        binding = ActivityGameBinding.inflate(layoutInflater)

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


        setContentView(binding.root)

        realm = (application as MyApp).realm

        binding.root.setOnTouchListener(
            OnSwipeTouchListener(this, this)
        )

        listOf(binding.btnChoice1, binding.btnChoice2, binding.btnChoice3).forEach { btn ->
            btn.setOnClickListener {
                val userAnswer = btn.text.toString()
                val isCorrect = userAnswer == correctAnswer

                btn.setBackgroundColor(if (isCorrect) Color.GREEN else Color.RED)
                playSoundEffect(isCorrect)

                score += if (isCorrect) 2 else -2
                updateScore()
                questionIndex++

                Handler(Looper.getMainLooper()).postDelayed({
                    btn.setBackgroundColor(Color.LTGRAY)
                    generateQuestion()
                }, 1200)
            }

        }

        generateQuestion()
    }

    private fun generateQuestion() {
        CoroutineScope(Dispatchers.IO).launch {
            val amiibos = realm.query<Amiibo>().find()

            if (amiibos.size < 3) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GameActivity, "Pas assez d'amiibos dans Realm !", Toast.LENGTH_LONG).show()
                }
                return@launch
            }

            val copied = amiibos.shuffled().take(20)
            val correct = copied.random()
            val imageUrl = correct.image

            isNameQuestion = if (forcedQuestionType != null) {
                forcedQuestionType!!
            } else {
                questionIndex % 2 == 0
            }

            forcedQuestionType = null

            correctAnswer = if (isNameQuestion) correct.name else correct.gameSeries

            val wrongAnswers = copied
                .filter { (if (isNameQuestion) it.name else it.gameSeries) != correctAnswer }
                .map { if (isNameQuestion) it.name else it.gameSeries }
                .distinct()
                .shuffled()
                .take(2)

            if (wrongAnswers.size < 2) return@launch

            val allChoices = (wrongAnswers + correctAnswer).shuffled()

            withContext(Dispatchers.Main) {
                Picasso.get().load(imageUrl).into(binding.imageAmiibo)

                binding.questionText.text = if (isNameQuestion)
                    "Quel est ce personnage ?"
                else
                    "Quel est son jeu ?"


                binding.btnChoice1.text = allChoices[0]
                binding.btnChoice2.text = allChoices[1]
                binding.btnChoice3.text = allChoices[2]


                listOf(binding.btnChoice1, binding.btnChoice2, binding.btnChoice3).forEach {
                    it.setBackgroundColor(Color.LTGRAY)
                }
            }
        }
    }

    private fun updateScore() {
        scoreMenuItem?.apply {
            title = "Score : $score"
            val color = when {
                score > 0 -> Color.GREEN
                score < 0 -> Color.RED
                else -> Color.BLACK
            }
            setTitleColor(color)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        scoreMenuItem = menu?.findItem(R.id.action_score)
        updateScore()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset_score -> {
                score = 0
                updateScore()
                Toast.makeText(this, "Score réinitialisé", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun setQuestionType(isName: Boolean) {
        forcedQuestionType = isName
        score -= 1
        updateScore()
        generateQuestion()
    }

    private fun playSoundEffect(isCorrect: Boolean) {
        val soundRes = if (isCorrect) R.raw.correct else R.raw.wrong
        val player = MediaPlayer.create(this, soundRes)
        player.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        }, 2000)
    }


    private fun MenuItem.setTitleColor(color: Int) {
        val hexColor = Integer.toHexString(color).uppercase().substring(2)
        val html = "<font color='#$hexColor'>$title</font>"
        this.title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        else
            Html.fromHtml(html)
    }
}

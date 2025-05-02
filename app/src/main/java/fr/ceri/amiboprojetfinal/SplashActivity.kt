package fr.ceri.amiboprojetfinal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import fr.ceri.amiboprojetfinal.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lancer l'animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.titleText.startAnimation(fadeIn)
        binding.titleText.alpha = 1f
        // Attendre 2 secondes puis lancer MainActivity
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
}

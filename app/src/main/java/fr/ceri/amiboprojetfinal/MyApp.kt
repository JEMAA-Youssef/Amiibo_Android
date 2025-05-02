package fr.ceri.amiboprojetfinal

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import fr.ceri.amiboprojetfinal.model.Amiibo

class MyApp : Application() {

    lateinit var realm: Realm
        private set
    private var backgroundMusic: MediaPlayer? = null
    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.Builder(
            schema = setOf(Amiibo::class)
        )
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        realm = Realm.open(config)
    }
    fun startBackgroundMusic(context: Context) {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music)
            backgroundMusic?.isLooping = true
        } else {
            backgroundMusic?.seekTo(0)
        }
        backgroundMusic?.start()
    }


    fun stopBackgroundMusic() {
        backgroundMusic?.pause()
    }

    fun releaseBackgroundMusic() {
        backgroundMusic?.release()
        backgroundMusic = null
    }
}

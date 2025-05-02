package fr.ceri.amiboprojetfinal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.ceri.amiboprojetfinal.databinding.ActivityMainBinding
import fr.ceri.amiboprojetfinal.model.Amiibo
import fr.ceri.amiboprojetfinal.webService.ApiClient
import fr.ceri.amiboprojetfinal.webService.AmiiboApi
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var realm: Realm
    private val selectedSeries = mutableSetOf<String>()
    private var allSeries: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = (application as MyApp).realm

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getGameSeries()
                val seriesNames = response.amiibo.map { it.name }.distinct().sorted()
                allSeries = seriesNames

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_multiple_choice,
                        seriesNames
                    )
                    binding.listViewSeries.adapter = adapter

                    selectedSeries.clear()
                    seriesNames.shuffled().take(4).forEachIndexed { i, name ->
                        selectedSeries.add(name)
                        binding.listViewSeries.setItemChecked(i, true)
                    }
                    binding.listViewSeries.setOnItemClickListener { _, _, pos, _ ->
                        val selected = seriesNames[pos]
                        if (selectedSeries.contains(selected)) {
                            selectedSeries.remove(selected)
                        } else {
                            selectedSeries.add(selected)
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur API : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_getamiibos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_select_all -> {
                selectedSeries.clear()
                for (i in allSeries.indices) {
                    binding.listViewSeries.setItemChecked(i, true)
                    selectedSeries.add(allSeries[i])
                }
                return true
            }

            R.id.action_deselect_all -> {
                selectedSeries.clear()
                for (i in allSeries.indices) {
                    binding.listViewSeries.setItemChecked(i, false)
                }
                return true
            }

            R.id.action_validate -> {
                if (selectedSeries.size < 4) {
                    Toast.makeText(this, "Veuillez sélectionner au moins 4 séries", Toast.LENGTH_SHORT).show()
                } else {
                    // Lancer animation sur l'icône du menu valider
                    val validateView = findViewById<View>(R.id.action_validate)
                    val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
                    validateView?.startAnimation(rotate)

                    // Ensuite on récupère et sauvegarde les Amiibos
                    fetchAndSaveAmiibos()

                    // Stopper l'animation après 2,5 secondes
                    Handler(Looper.getMainLooper()).postDelayed({
                        validateView?.clearAnimation()
                    }, 2500)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun fetchAndSaveAmiibos() {
        CoroutineScope(Dispatchers.IO).launch {
            val allAmiibos = mutableListOf<Amiibo>()
            realm.writeBlocking {
                deleteAll()
                allAmiibos.forEach { copyToRealm(it) }
            }
            selectedSeries.forEach { series ->
                try {
                    //val response = ApiClient.apiService.getAmiibos(series)
                    val response = ApiClient.apiService.getAmiibosBySeries(series)
                    allAmiibos.addAll(response.amiibo.map { apiAmiibo ->
                        Amiibo().apply {
                            id = apiAmiibo.image
                            name = apiAmiibo.name
                            gameSeries = apiAmiibo.gameSeries
                            image = apiAmiibo.image
                            type = apiAmiibo.type
                        }


                    })
                } catch (_: Exception) {}
            }

            realm.writeBlocking {
                deleteAll()
                allAmiibos.forEach { copyToRealm(it) }

                // Ajout de log pour vérifier
                val saved = query<Amiibo>().find()
                Log.d("RealmCheck", "Nombre d'amiibos sauvegardés : ${saved.size}")
                saved.forEach { Log.d("RealmCheck", "-> ${it.name} | ${it.image}") }
            }


            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Données sauvegardées !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                intent.putStringArrayListExtra("selectedSeries", ArrayList(selectedSeries))
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (application as MyApp).startBackgroundMusic(this)
    }

    override fun onPause() {
        super.onPause()
        (application as MyApp).stopBackgroundMusic()
    }




}

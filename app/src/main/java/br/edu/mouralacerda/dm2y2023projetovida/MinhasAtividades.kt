package br.edu.mouralacerda.dm2y2023projetovida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MinhasAtividades : AppCompatActivity() {

    var lstMinhasAtividades: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_atividades)

        lstMinhasAtividades = findViewById(R.id.lstMinhasAtividades)

        updateList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.home){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        if (item.itemId == R.id.minhasAtividade){
            val intent = Intent(this, MinhasAtividades::class.java)
            startActivity(intent)
        }

        if (item.itemId == R.id.novaAtividade){
            val intent = Intent(this, NovaAtividade::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateList() {
        var locationData: List<Movimento>

        CoroutineScope(Dispatchers.IO).launch {
            locationData = MovimentoDatabase.getInstance(this@MinhasAtividades).MovimentoDataDao().getAllLocationsData()

            withContext(Dispatchers.Main) {
                lstMinhasAtividades!!.adapter = ArrayAdapter(
                    this@MinhasAtividades,
                    android.R.layout.simple_list_item_1,
                    locationData
                )
            }
        }
    }
}
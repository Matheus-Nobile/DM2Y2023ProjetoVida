package br.edu.mouralacerda.dm2y2023projetovida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
}

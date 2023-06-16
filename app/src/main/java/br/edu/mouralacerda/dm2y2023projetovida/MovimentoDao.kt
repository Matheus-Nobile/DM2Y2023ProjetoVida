package br.edu.mouralacerda.dm2y2023projetovida

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovimentoDao {

    @Insert
    fun save(movimento: Movimento)

    @Query("SELECT * FROM Movimento ORDER BY id DESC")
    fun getAllLocationsData(): List<Movimento>
}
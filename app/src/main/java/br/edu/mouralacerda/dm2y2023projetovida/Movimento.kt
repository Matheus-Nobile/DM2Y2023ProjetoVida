package br.edu.mouralacerda.dm2y2023projetovida

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val timestamp: String,
    val accelX: Double,
    val accelY: Double,
    val accelZ: Double,
    val latitude: Double,
    val longitude: Double,
    val atleta: String,
    val atividade : String,

) {
    override fun toString(): String {
        return "$id | $atleta | $atividade | $timestamp | $accelX | $accelY | $accelZ | $latitude | $longitude"
    }
}
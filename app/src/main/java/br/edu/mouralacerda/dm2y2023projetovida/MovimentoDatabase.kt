package br.edu.mouralacerda.dm2y2023projetovida

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movimento::class], version = 1)
abstract class MovimentoDatabase: RoomDatabase() {

    abstract fun MovimentoDataDao(): MovimentoDao

    companion object {
        private var database: MovimentoDatabase? = null
        private val DATABASE = "MovimentoDataDB"

        fun getInstance(context: Context): MovimentoDatabase {
            if(database == null)
                    database = criaBanco(context)
            return database!!
        }

        private fun criaBanco(context: Context): MovimentoDatabase {
            return Room.databaseBuilder(context, MovimentoDatabase::class.java, DATABASE)
                .allowMainThreadQueries()
                .build()
        }
    }
}


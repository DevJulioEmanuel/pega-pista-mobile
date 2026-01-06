package com.example.pegapista.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.database.entities.CorridaEntity
import com.example.pegapista.database.entities.UserEntity
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarUser(user: UserEntity)

    @Query("SELECT * FROM  users WHERE userSincronizado = 0")
    suspend fun getUsernaoSincronizados(): List<UserEntity>

    @Update
    suspend fun atualizarUser(user: UserEntity)
}
fun UserEntity.toModel(): Usuario {
    return Usuario (
        id = this.id,
        nickname = this.nickname,
        email = this.email,
        fotoPerfilUrl = this.fotoPerfilUrl,
        distanciaTotalKm = this.distanciaTotalKm,
        tempoTotalSegundos = this.tempoTotalSegundos,
        caloriasQueimadas = this.caloriasQueimadas,
        diasSeguidos = this.diasSeguidos,
        recordeDiasSeguidos = this.recordeDiasSeguidos
    )
}
//fun User.toEntity(): UserEntity{
//    return UserEntity (
//        id = this.id,
//        nickname = this.nickname,
//        email = this.email,
//        fotoPerfilUrl = this.fotoPerfilUrl,
//        distanciaTotalKm = this.distanciaTotalKm,
//        tempoTotalSegundos = this.tempoTotalSegundos,
//        caloriasQueimadas = this.caloriasQueimadas,
//        diasSeguidos = this.diasSeguidos,
//        recordeDiasSeguidos = this.recordeDiasSeguidos,
//        userSincronizado = this.userSincronizado
//    )
//}

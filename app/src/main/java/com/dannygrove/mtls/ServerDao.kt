package com.dannygrove.mtls

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
  @Query("SELECT * FROM servers")
  fun getServers(): Flow<List<Server>>

  @Query("SELECT * FROM servers WHERE id = :id LIMIT 1")
  suspend fun getServerById(id: Int): Server?

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun addServer(server: Server)

  @Delete
  suspend fun deleteServer(server: Server)

  @Update
  fun updateServer(server: Server)
}

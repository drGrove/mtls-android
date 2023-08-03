package com.dannygrove.mtls

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [Server::class],
  version = 1
)
abstract class MTLSDatabase : RoomDatabase() {
  abstract val serverDao: ServerDao

  companion object {
    const val DATABASE_NAME = "mtls_db"
  }
}

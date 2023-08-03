package com.dannygrove.mtls

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
  @PrimaryKey val id: Int,
  val name: String,
  val email: String,
  val fingerprint: String,
  val country: String,
  val state: String,
  val locality: String,
  val organization_name: String,
  val url: String,
  val issuer: String,
  val lifetime: Long,
) {
}

class InvalidServerException(message: String) : Exception(message)

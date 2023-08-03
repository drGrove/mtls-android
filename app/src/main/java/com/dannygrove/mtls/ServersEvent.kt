package com.dannygrove.mtls

sealed class ServersEvent {
  data class DeleteServer(val server: Server) : ServersEvent()
  object RestoreServer : ServersEvent()
}

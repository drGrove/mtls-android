package com.dannygrove.mtls

data class ServerUseCases(
  val getServer: GetServersUseCase,
  val deleteServer: DeleteServerUseCase,
  val addServer: AddServerUseCase
)

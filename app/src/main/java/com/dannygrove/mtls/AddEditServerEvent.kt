package com.dannygrove.mtls

sealed class AddEditServerEvent {
  object SaveServer : AddEditServerEvent()
}

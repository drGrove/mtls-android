package com.dannygrove.mtls.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dannygrove.mtls.Server

@Composable
fun ServerListCard(
  server: Server,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(10.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
    shape = MaterialTheme.shapes.extraLarge,
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = server.name,
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = server.url,
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}

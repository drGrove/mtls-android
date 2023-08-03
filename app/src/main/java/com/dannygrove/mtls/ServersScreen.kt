package com.dannygrove.mtls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dannygrove.mtls.ui.theme.ServerListCard

@ExperimentalMaterial3Api
@Composable
fun ServersScreen(
  navController: NavController,
  viewModel: ServerViewModel = hiltViewModel()
) {
  val state = viewModel.state.value
  val snackbarState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  // A surface container using the 'background' color from the theme
  Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Scaffold(
      floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      },
      topBar = {
        TopAppBar(
          title = {
            Text(text = "MTLS")
          },
          actions = {
            IconButton(onClick = { /*TODO*/ }) {
              Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        )
      },
    )
    { values ->
      LazyColumn(contentPadding = values) {
        items(state.servers) { server ->
          ServerListCard(
            server = server,
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
              },
          )
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

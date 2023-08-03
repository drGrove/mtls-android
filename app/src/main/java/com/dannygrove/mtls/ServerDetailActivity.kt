package com.dannygrove.mtls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import com.dannygrove.mtls.ui.theme.MtlsTheme

@OptIn(ExperimentalMaterial3Api::class)
class ServerDetailActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MtlsTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Scaffold(
            topBar = {
              TopAppBar(
                title = {
                  Text(text = "MTLS")
                },
              )
            },
          ) { values ->
            LazyColumn(contentPadding = values) {

            }
          }
        }
      }
    }
  }
}

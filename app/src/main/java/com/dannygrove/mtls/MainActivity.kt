package com.dannygrove.mtls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.dannygrove.mtls.ui.theme.MtlsTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MtlsTheme {}
    }
  }
}

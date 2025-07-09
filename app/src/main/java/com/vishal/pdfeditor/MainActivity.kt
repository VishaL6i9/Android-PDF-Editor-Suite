package com.vishal.pdfeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishal.pdfeditor.feature.file_manager.FileManagerScreen
import com.vishal.pdfeditor.feature.viewer.ViewerScreen
import com.vishal.pdfeditor.ui.theme.PDFEditorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFEditorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "file_manager") {
        composable("file_manager") {
            FileManagerScreen(navController)
        }
        composable("viewer/{pdfUri}") { backStackEntry ->
            val pdfUri = backStackEntry.arguments?.getString("pdfUri")?.toUri()
            pdfUri?.let {
                ViewerScreen(it)
            }
        }
    }
}

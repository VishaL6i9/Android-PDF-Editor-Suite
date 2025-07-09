package com.vishal.pdfeditor.feature.viewer

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
//@file:Suppress("UNUSED_IMPORTS")
//import androidx.pdf.viewer.PdfViewer

@Composable
fun ViewerScreen(pdfUri: Uri) {
    /*
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PdfViewer(context).apply {
                load(pdfUri)
            }
        }
    )
    */
}

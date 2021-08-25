package com.example.pdf_scanner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.pdf_scanner.BuildConfig

fun shareApp(context: Context){
    val shareIntent: Intent = Intent(Intent.ACTION_SEND)
    shareIntent.setType("text/plain")
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Minecraft Mod")
    var shareMessage = "\nLet me recommend you Flash VPN Application\n\n"
    shareMessage =
        """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                """.trimIndent()
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    context.startActivity(Intent.createChooser(shareIntent, "Choose one"))
}

fun rateApp(context: Context){
    context.startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        )
    )
}

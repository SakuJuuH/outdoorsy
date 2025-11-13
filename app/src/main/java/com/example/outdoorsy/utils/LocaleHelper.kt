package com.example.outdoorsy.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    val supportedLanguages = mapOf<String, Locale>(
        "en" to Locale.ENGLISH,
        "fi" to Locale.of("fi")
    )

    fun setLocale(languageCode: String) {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag = if (!currentLocales.isEmpty) currentLocales[0]?.language else "en"

        if (currentTag == languageCode) return

        val locale = supportedLanguages[languageCode] ?: Locale.getDefault()
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}

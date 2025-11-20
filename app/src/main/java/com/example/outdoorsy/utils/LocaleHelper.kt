package com.example.outdoorsy.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    fun getLanguageName(code: String): String {
        val locale = if (code == AppLanguage.FINNISH.code) Locale.of("fi") else Locale.ENGLISH
        return locale.displayLanguage.replaceFirstChar { it.uppercase() }
    }

    fun setLocale(languageCode: String) {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag =
            if (!currentLocales.isEmpty) currentLocales[0]?.language else AppLanguage.ENGLISH.code

        if (currentTag == languageCode) return

        val locale =
            if (languageCode == AppLanguage.FINNISH.code) Locale.of("fi") else Locale.ENGLISH
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}

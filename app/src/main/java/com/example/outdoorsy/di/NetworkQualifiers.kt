package com.example.outdoorsy.di

import javax.inject.Qualifier

/**
 * Qualifier annotation for dependencies related to eBay OAuth/Authentication.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EbayAuth

/**
 * Qualifier annotation for dependencies related to the main eBay API.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EbayApi

/**
 * Qualifier annotation for dependencies related to the OpenWeatherMap API.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeather

/**
 * Qualifier annotation for dependencies related to the Currency API.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrencyApi

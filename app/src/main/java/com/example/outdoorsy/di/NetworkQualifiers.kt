package com.example.outdoorsy.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EbayAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EbayApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeather

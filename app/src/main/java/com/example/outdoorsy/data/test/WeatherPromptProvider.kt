package com.example.outdoorsy.data.test

object WeatherPromptProvider {
    fun buildPrompt(
        activity: String,
        location: String,
        date: String,
        startTime: String,
        endTime: String
    ): String = """
        You are an assistant providing weather-based activity recommendations. The user has selected:

        - Activity: $activity
        - Location: $location
        - Date: $date
        - Start time: $startTime
        - End time: $endTime

        You already have access to the weather data for this location and time window. Based on that, provide:

        1. A short summary of the weather conditions (without repeating temperature, humidity, or wind speed).
        2. Activity-specific recommendations, including tips on what to wear and how to prepare.

        Return the output in the following structured JSON format:

        {
          "location": "$location",
          "date": "$date",
          "start_time": "$startTime",
          "end_time": "$endTime",
          "activity": "$activity",
          "weather_summary": "...",
          "recommendations": {
            "clothing": "...",
            "tips": "..."
          }
        }

        Do not include any extra commentary or formatting outside the JSON block.
    """.trimIndent()
}

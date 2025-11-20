package com.example.outdoorsy.data.test

object WeatherPromptProvider {
    fun buildPrompt(
        activity: String,
        location: String,
        date: String,
        startTime: String,
        endTime: String,
        forecast: String,
        unit: String,
        language: String
    ): String = """
        You are an assistant providing weather-based activity tips and recommendations.
        You must take into account the chosen activity, location, date, start and end times.
        The user has selected the following parameters:

        - Activity: $activity
        - Location: $location
        - Date: $date
        - Start time: $startTime
        - End time: $endTime
        
        Here is the weather forecast for the city: $forecast
        Here is the unit system that you must use in the answer: $unit. If metric, use Celsius
        The answer must be generated in the following language: $language 

        Based on the given information, do the following:

        1. Analyze the weather forecast for the location, taking into account the specified activity, date, start time and end time.
        2. Fill and return an answer in the following JSON format, completed with the requested information.
        
        Do not include any extra commentary, formatting or extended information beyond the JSON.

        {
          "unit": "$unit",
          "language": "$language",
          "location": "$location",
          "date": "$date",
          "start_time": "$startTime",
          "end_time": "$endTime",
          "activity": "$activity",
          "suitability_score": "A string of how suitable the activity is according to the weather. Value must be 'Very Good', 'Good', 'Fair', 'Bad', 'Very Bad'",
          "suitability_info": "An array of string with 2-4 items explaining why the suitability_score was chosen",
          "clothing_tips": "An array of strings with 2-4 clothing tips for the specific weather",
          "clothing_items": "An array of strings with just the clothing items recommended in the clothing_tips",
          "weather_tips": "An array of string with 2-4 weather tips regarding the temperature, wind, humidity, etc"
        }
    """.trimIndent()
}

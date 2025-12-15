# How to Get a CurrencyAPI.com API Key

This guide will walk you through the process of obtaining a free API key
from [currencyapi.com](https://currencyapi.com), which is required for the currency conversion
feature in this application.

## Step 1: Sign Up for a Free Account

1. Navigate to the [currencyapi.com](https://currencyapi.com) website.
2. Click on the **"Get Free API Key"** button.
3. Fill out the registration form with your email, choose the free subscription package, and enter a
   password and register.

## Step 2: Verify Your Email

1. Check your email inbox for a verification message from CurrencyAPI.
2. Click the verification link in the email to activate your account.

## Step 3: Find Your API Key

### Option 1: Use a Default API Key

1. Once your account is verified, you will be taken to your account dashboard.
2. Your API key is clearly displayed on the dashboard in a field labeled **"Default Key"**.

### Option 2: Create a New API Key

1. Once your account is verified, you will be taken to your account dashboard.
2. Navigate To **"API Keys"**.
3. Delete the **"Default Key"**.
4. After deleting the default key, name you new API Key and click on the **"Create New Key"**
   button.
5. Your new API key will be displayed immediately.

## Step 4: Add the API Key to the Project

To use the key in the Outdoorsy project, you need to add it to a `local.properties` file. This file
is intentionally not checked into version control to keep your secret keys safe.

1. In Android Studio, navigate to the root directory of the project.
2. Create a file named `local.properties` if it doesn't already exist.
3. Add the following line to your `local.properties` file, pasting your API key after the equals
   sign: `CURRENCY_API_KEY=YOUR_API_KEY_HERE`
4. Sync your project with Gradle.
    
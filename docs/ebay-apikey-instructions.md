# How to Generate an eBay API Key (Client Credentials)

This project uses the **eBay Client Credentials Grant** flow to authenticate. You will need to
generate an `EBAY_BASIC_KEY` which is a Base64 encoded string of the Client ID and Client Secret.

## Step 1: Create an eBay Developer Account

1. Go to the [eBay Developer Program](https://developer.ebay.com/).
2. Click **Register now** to create a new account or login to an existing one from the user icon in
   the hamburger menu or clicking the **Register now** button and navigating to the **Sign In**
   section.

   > [!IMPORTANT]
   >
   > After registering a new account, it will take a couple of days for eBay to verify your account
   before you can start using the API.

## Step 2: Generate Application Keys

1. Once logged in, navigate to **Hi [User] > Application Keysets** in the top navigation bar.
2. You will see two environments: **Sandbox** (for testing) and **Production** (for running the
   application).

   You should create a **Production Keyset** for this project.

   > [!IMPORTANT]
   >
   > Sandbox environment is recommended for development but does not contain real eBay
   listings, therefore its better to use the production one for developing this project.

3. Click **Create a Keyset** for your chosen environment.
4. Fill in the required application details if prompted.
5. Once created, you will see the **App ID (Client ID)** and **Cert ID (Client Secret)**.

## Step 3: Create the Basic Auth Key

The `EBAY_BASIC_KEY` required in `local.properties` is a Base64 encoded concatenation of the App ID
and Cert ID.

1. Format the keys as a single string separated by a colon:
   ```text
   YOUR_APP_ID:YOUR_CERT_ID
   ```

2. Base64 encode the string:
    - Mac/Linux Terminal:

        ```shell
        echo -n "YOUR_APP_ID:YOUR_CERT_ID" | base64
        ```

    - Windows PowerShell:

        ```powershell
        [Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("YOUR_APP_ID:YOUR_CERT_ID"))
        ```

    - Website: https://www.base64encode.org/

## Step 4: Configure the Project

1. Open (or create from the example file) the local.properties file in the root of the project.

2. Add the generated Base64 string to the EBAY_BASIC_KEY field:

    ```.properties
    EBAY_BASIC_KEY=YourBase64EncodedStringHere
    ```

3. Sync your project with Gradle.

> [!IMPORTANT]
>
> Do not commit the `local.properties` file to version control.

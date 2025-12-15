# OpenWeatherMap API key instructions

This guide is divided into two sections, as there are two types of keys that can be used for the OpenWeatherMap API:

- Free key (simpler to setup but only contains basic features).
- One Call API 3.0 (needed for accessing the AI features of the app, but requires inputting a credit/debit card).

> [!NOTE]
> Even though the guide is separated into two sections, the same API key can be used for both if One Call API 3.0 is activated in the account afterwards.

## Free API key

To get a free OpenWeatherMap API key, follow these steps:

1. Navigate to the [OpenWeatherMap sign-in page](https://home.openweathermap.org/users/sign_in).
2. If you have an account, sign in to it. Otherwise, click `Create an Account`, and follow the steps on the screen.
3. Once logged in, select your username on the top right corner, next to `Support centre` and click on `My API keys`. Or you can follow [this link](https://home.openweathermap.org/api_keys).
4. In the area named `Create key`, write a name of your choosing for the API key and click `Generate`.
5. The API key will now appear on screen. You can simply copy it, include it in the `local.properties` and use it.

Remember that this key will NOT grant you access to the AI features. For that you will need the [One Call API 3.0](#one-call-api-30).

## One Call API 3.0

To get a One Call API 3.0 key, follow these steps:

1. Follow all steps included in [the previous section](#free-api-key), as the same key will be valid afterwards.
2. Navigate to the [Billing plans](https://home.openweathermap.org/subscriptions) section of the OpenWeather Home.
3. Under `One Call API 3.0` look for `Base plan` and click `Subscribe`.
4. Fill your credit/debit card data, continue to payment and follow the instructions on your screen.
5. Afterwards, you should be redirected to the same page. If not, open the link on step.
6. Ensure that the `Base plan` of the `One Call API 3.0` says `Activated`.

> [!TIP]
> Since the One Call API 3.0 gives you 1,000 free API calls per day, you can limit the number of `Calls per day` to ensure that you never pay anything.
> You can do this from the `Billing plans` page (same mentioned in the previous steps).

After following the instructions, all API keys now on your account will be eligible to make calls under the One Call API 3.0.
This means that you should now be able to use the AI features of the app with any of your API keys.
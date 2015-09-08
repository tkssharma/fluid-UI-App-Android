# App based on Login Using facebook and fetch profile data 

The navigation drawer is a panel that displays the app’s main navigation options on the left edge of the screen. It is hidden most of the time, but is revealed when the user swipes a finger from the left edge of the screen or, while at the top level of the app, the user touches the app icon in the action bar.

# Android Studio App 

> Material Design Specifications[Navigation Drawer](http://blog.teamtreehouse.com/add-navigation-drawer-android) 
> Creating Apps with Material Design[Material Design](http://developer.android.com/training/material/index.html) 

## Running Locally
Make sure you have [Android studio/Eclipse ADB](http://developer.android.com/tools/studio/index.html) 

```sh
    compile 'com.facebook.rebound:rebound:0.3.6'
    compile 'com.squareup.picasso:picasso:2.5.0'
    compile 'com.facebook.android:facebook-android-sdk:4.0.0'
    compile 'com.jakewharton:butterknife:6.1.0'
    compile 'com.loopj.android:android-async-http:1.4.8'
```

## Initilize Facebook SDK first and add tracker 
```sh
 FacebookSdk.sdkInitialize(mContext);
        setContentView(com.desmond.materialdesigndemo.R.layout.activity_login);
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken1) {
                updateWithToken(accessToken1);
            }
        };

        mprofileTraker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile old, Profile newprofi) {

            }
        };
        mprofileTraker.startTracking();
        mAccessTokenTracker.startTracking();
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), activity_signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


```

## Code fetching data from facebook Graph API 


```sh
    loginButton = (LoginButton) findViewById(R.id.fb_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Bundle params = new Bundle();
                params.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "me",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                            fbUser.imageUri = profilePicUrl;
                                            Log.v("Name:", data.get("name").toString());
                                            Toast.makeText(getApplicationContext(), "Welcome " + data.get("name").toString() +
                                                            "\n" + data.get("email").toString(),
                                                    Toast.LENGTH_LONG).show();
                                            fbUser.email = (data.get("email").toString());
                                            fbUser.name = (data.get("name").toString());
                                            fbUser.gender = (data.get("gender").toString());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();
                startActivity(new Intent(ActivityLogin.this, MainActivity.class));

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(mContext, "Cancel!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(mContext, "Facebook error!", Toast.LENGTH_LONG).show();
            }
        });


   <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/app_id" />

 <provider
            android:name="com.facebook.FacebookContentProvider"
            android:authorities="com.facebook.app.FacebookContentProvider1234"
            android:exported="true" />


```
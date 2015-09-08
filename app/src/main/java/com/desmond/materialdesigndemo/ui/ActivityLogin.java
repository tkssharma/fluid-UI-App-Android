package com.desmond.materialdesigndemo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.materialdesigndemo.R;
import com.desmond.materialdesigndemo.ui.Handler.SessionManager;
import com.desmond.materialdesigndemo.ui.activity.MainActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityLogin extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final int REQUEST_SIGNUP = 0;
    @InjectView(com.desmond.materialdesigndemo.R.id.input_email)
    EditText _emailText;
    @InjectView(com.desmond.materialdesigndemo.R.id.input_password)
    EditText _passwordText;
    @InjectView(com.desmond.materialdesigndemo.R.id.btn_login)
    Button _loginButton;
    @InjectView(com.desmond.materialdesigndemo.R.id.link_signup)
    TextView _signupLink;
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SessionManager session;
    private Context mContext;
    private ProfileTracker mprofileTraker;
    private AccessTokenTracker mAccessTokenTracker;
    private String facebook_id, f_name, m_name, l_name, gender, profile_image, full_name, email_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        FacebookSdk.sdkInitialize(mContext);
        setContentView(com.desmond.materialdesigndemo.R.layout.activity_login);
        ButterKnife.inject(this);
        mContext = this;
        //  updateWithToken(AccessToken.getCurrentAccessToken());
        final ProgressDialog progressDialog = new ProgressDialog(ActivityLogin.this,
                R.style.Base_Theme_AppCompat_Dialog_Alert);
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


        //  info = (TextView) findViewById(R.id.info);
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


    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (AccessToken.getCurrentAccessToken() == null && com.facebook.Profile.getCurrentProfile() == null) {
            //Logged in so show the login button
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//log out
                    if (LoginManager.getInstance() != null) {
                        LoginManager.getInstance().logOut();
                    }
                    //gotoLogin();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(ActivityLogin.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 5000);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mprofileTraker.stopTracking();
        mAccessTokenTracker.stopTracking();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ActivityLogin.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        // Session manager
        session = new SessionManager(mContext);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            /*Intent intent = new Intent(ActivityLogin.this, activity_signup.class);
            startActivity(intent);
            finish();*/
        }
        checkLogin(email, password);

    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("tag", "login");
        HttpClient.get("", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                System.out.println(response.toString());
                try {
                    if (response != null) {
                        boolean error = response.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            // user successfully logged in
                            // Create login session
                            session.setLogin(true);
                            //  progressDialog.dismiss();
                            // Launch main activity
                        /*Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();*/
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = response.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Pull out the first event on the public timeline

                // progressDialog.dismiss();
                // Do something with the response
                System.out.println(response.toString());


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // progress.setVisibility(ProgressBar.GONE);
                //  progressDialog.dismiss();
            }
        });

        // Adding request to request queue
        //   AppController.getInstance().addToRequestQueue(strReq, tag_string_req, mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_refresh) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
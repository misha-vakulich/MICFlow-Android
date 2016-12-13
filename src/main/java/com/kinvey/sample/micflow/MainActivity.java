package com.kinvey.sample.micflow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.dto.User;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private Client kinveyClient;
    private static final String APP_KEY = "kid_ZyrG_IFcGe";
    private static final String APP_SECRET = "f554f49974d14962bf72814268c2a7ba";
    private static final String redirectURI = "kinveyAuthDemo://";

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";



    private TextView loginStatus;
    private TextView errorView;
    private Button loginWithPage;
    private Button kinveyLogin;
    private Button loginAutomated;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create the Kinvey Client with an AppKey and AppSecret
        kinveyClient = new Client.Builder(APP_KEY, APP_SECRET, this)

                .setSSOEnabled(true)
                .setAccountType("com.kinvey.myapplogin")
                .build();


        bindViews();

        updateStatus();

    }

    protected void onResume(){
        super.onResume();
        updateStatus();
    }


    private void bindViews(){
        loginStatus = (TextView) findViewById(R.id.login_status);
        loginWithPage = (Button) findViewById(R.id.loginpage);
        loginAutomated = (Button) findViewById(R.id.automated);
        kinveyLogin = (Button) findViewById(R.id.kinvey_login);
        logout = (Button) findViewById(R.id.logout);
        errorView = (TextView) findViewById(R.id.login_errors);

        loginWithPage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginWithLoginPage();

            }
        });

        kinveyLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    kinveyLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                    errorView.setText(e.getMessage());
                }

            }
        });

        loginAutomated.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginAutomated();

            }
        });

        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();

            }
        });
    }


    private void loginWithLoginPage(){
        loading();
        UserStore.presentMICLoginActivity(kinveyClient, redirectURI, new KinveyUserCallback<User>(){

            @Override
            public void onSuccess(User user) {
                updateStatus();
            }

            @Override
            public void onFailure(Throwable error) {
                errorView.setText(error.getMessage());
            }

        });
    }

    private void kinveyLogin() throws IOException {
        UserStore.login("test", "test", kinveyClient, new KinveyUserCallback<User>() {
            @Override
            public void onSuccess(User user) {
                updateStatus();
            }

            @Override
            public void onFailure(Throwable throwable) {
                errorView.setText(throwable.getMessage());
            }

        });
    }

    private void loginAutomated(){
        loading();
        UserStore.loginWithAuthorizationCodeAPI(kinveyClient, USERNAME, PASSWORD, redirectURI, new KinveyUserCallback<User>() {

            @Override
            public void onSuccess(com.kinvey.java.dto.User user) {
                updateStatus();

            }

            @Override
            public void onFailure(Throwable error) {
                errorView.setText(error.getMessage());
            }
        });

    }

    private void logout(){
        loading();
        try{
//            if(kinveyClient.isUserLoggedIn()) {
                UserStore.logout(kinveyClient);
                updateStatus();
//            }
        } catch(Exception e){
            Log.d("MainActivity", e.toString());
            errorView.setText(e.toString());
        }
    }

    private void loading(){
        loginStatus.setText("loading...");
    }

    private void updateStatus(){
        if (kinveyClient.isUserLoggedIn()){
            loginStatus.setText("User is logged in!");
        }else{
            loginStatus.setText("Not logged in yet!");
        }
        errorView.setText("No Errors!");
    }
}

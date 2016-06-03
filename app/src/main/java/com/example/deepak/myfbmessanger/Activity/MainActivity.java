package com.example.deepak.myfbmessanger.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.deepak.myfbmessanger.AuthFailed;
import com.example.deepak.myfbmessanger.MyService;
import com.example.deepak.myfbmessanger.R;
import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.UserProfile;
import com.squareup.otto.Subscribe;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_activity);
        DBHandler.startIfNotStarted(this);

            UserProfile userProfile = DBHandler.dbHandler.getuserProfileList();
            if(userProfile != null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new UserFragment())
                        .commit();

            }else
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();

            }
        }



    @Subscribe
    public void onAuthFailed(AuthFailed authFailed){
        DBHandler.dbHandler.delete();
        stopService(new Intent(this,MyService.class));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();


    }
  /*  @Subscribe
    public void onConnectioFail(String s){
        if(s == "No response from the server"){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFragment())
                    .commit();
            Toast.makeText(this,"connectionLost",Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onPause() {
        MyService.BUS.unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MyService.BUS.register(this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    }
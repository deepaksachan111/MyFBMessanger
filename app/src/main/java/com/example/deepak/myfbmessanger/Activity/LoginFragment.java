package com.example.deepak.myfbmessanger.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deepak.myfbmessanger.CheckNetwork;
import com.example.deepak.myfbmessanger.MyService;
import com.example.deepak.myfbmessanger.R;
import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.UserProfile;

/**
 * Created by deepak on 31/3/15.
 */
public class LoginFragment extends Fragment {
   public  static final String TAG = LoginFragment.class.getSimpleName();
    Button button ;

    private   EditText email;
    private   EditText password;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_login,container,false);
       button = (Button) view.findViewById(R.id.login_button);
        email = (EditText) view.findViewById(R.id.etemail);
        password= (EditText)view.findViewById(R.id.etpass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String useremail = email.getText().toString();
                final String userpassword = password.getText().toString();

                if (useremail.length() == 0 || userpassword.length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(),"Enter Username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                DBHandler.startIfNotStarted(getActivity());
                DBHandler.dbHandler.saveUserProfile(new UserProfile(useremail, userpassword));

                if (!MyService.isRunning) {
                    Intent serviceIntent = new Intent(getActivity(), MyService.class);
                    getActivity().startService(serviceIntent);
                }
                if (CheckNetwork.isInternetAvailable(getActivity().getApplication())) //returns true if internet available
                {
                    Fragment fragment = new UserFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container,fragment );
                    transaction.commit();
                    //do something. loadwebview.

                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }
}

package com.example.deepak.myfbmessanger.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.deepak.myfbmessanger.Adapter.MyAdapter;
import com.example.deepak.myfbmessanger.MyService;
import com.example.deepak.myfbmessanger.R;
import com.example.deepak.myfbmessanger.db.DBHandler;
import com.example.deepak.myfbmessanger.db.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by deepak on 11/6/15.
 */
public class UserFragment extends Fragment {
    private static final String TAG = UserFragment.class.getSimpleName();
    Context context;
    private ListView listviewonline;
    private ProgressDialog progressDialog;
    // private Handler handler = new Handler();
    ArrayList<Data> strings = new ArrayList<Data>();
    MyAdapter myAdapter;

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

       /* ActionBar actionBar =getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_main, container, false);


        if (!MyService.isRunning) {
            Intent serviceIntent = new Intent(getActivity(), MyService.class);
            getActivity().startService(serviceIntent);
        }

        listviewonline = (ListView) view.findViewById(R.id.listuseronline);
        // View header = (View) getLayoutInflater().inflate(R.layout.header_row, null);
        // listviewonline.addHeaderView(header);
        // listviewonline.setVisibility(View.INVISIBLE);

        myAdapter = new MyAdapter(getActivity(), R.layout.adaper_user_list, strings);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);

        listviewonline.setAdapter(myAdapter);
        listviewonline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data s = myAdapter.getItem(position);
                // String s = ((TextView) view).getText().toString();
                Intent intent = new Intent(getActivity(), ChatMessageActvity.class);
                intent.putExtra("USER_NAME", s.getUsername());
                intent.putExtra("USER_ID", s.getId());
                startActivity(intent);
            }
        });
       /* ImageView iv = (ImageView)findViewById(R.id.ib);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listviewonline.setVisibility(View.VISIBLE);
            }
        });*/

        if (MyService.userIdDataHashMap.size() == 0) {
            Log.e(TAG, "size zero --------------------------------- ");
            new MyAsyncTask().execute();
        } else {
            Log.e(TAG, "size non zero ----------------------------- ");
            showView();
        }
        return view;
    }
//........................................................................................................

    private void showView() {
        Log.e(TAG, "show view called ------------------------------- ");
        Collection<Data> datas = MyService.userIdDataHashMap.values();
        myAdapter.addAll(datas);
        myAdapter.sort(new Comparator<Data>() {
            public int compare(Data object1, Data object2) {
                return object1.getUsername().compareToIgnoreCase(object2.getUsername());
            }

            ;
        });
        myAdapter.notifyDataSetChanged();

    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Connecting ...", true);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (MyService.userIdDataHashMap.size() == 0) {
                try {
                    synchronized (MyService.userIdDataHashMap) {
                        MyService.userIdDataHashMap.wait();
                    }
                } catch (Exception e) {

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showView();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        setHasOptionsMenu(true);
    }

    /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            DBHandler.dbHandler.delete();



            getActivity().stopService(new Intent(getActivity(),MyService.class));

           /* UserProfile userProfile  = DBHandler.dbHandler.getuserProfileList();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFragment())
                    .commit();*/
             startActivity(new Intent(getActivity(),MainActivity.class));
             getActivity().finish();
             closefragment();
            MyService.userIdDataHashMap = new ConcurrentHashMap<>();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void closefragment() {
        getActivity().getFragmentManager().popBackStack();
    }

    @Override
    public void onStop() {
    super.onStop();
}


}


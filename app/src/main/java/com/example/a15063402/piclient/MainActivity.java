package com.example.a15063402.piclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.io.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Clock;
import java.util.ArrayList;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends Activity {

    private ListView mList;
    private ArrayList<String> arrayList;
    private ClientListAdapter mAdapter;
    private TcpClient mTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();


        //relate the listView from java to the one created in xml
        mList = (ListView) findViewById(R.id.list);
        mAdapter = new ClientListAdapter(this, arrayList);
        mList.setAdapter(mAdapter);



        //new Thread(new ClientThread()).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        final Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                //move to next activity
                try {
//                    Intent k = new Intent(MainActivity.this, ControlPanel.class);
//                    startActivity(k);
                    new ConnectTask().execute("");
                } catch(Exception e) {
                }

            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(this.getClass().getName(), "Insdie onResume");
        // disconnect
        mTcpClient.stopClient();
        mTcpClient = null;
    }

    public void sendMessageToServer(String message){


        //add the text in the arrayList
        arrayList.add("c: " + message);

        //sends the message to the server
        if (mTcpClient != null) {
            System.out.println(message);
            mTcpClient.sendMessage(message);
        }

        //refresh the list
        mAdapter.notifyDataSetChanged();
    }


    public void sendMessageUp(View view) {
        // Do something in response to button click
        sendMessageToServer("UP");
    }

    public void sendMessageDown(View view) {
        // Do something in response to button click
        sendMessageToServer("Down");
    }

    public void sendMessageRight(View view) {
        // Do something in response to button click
    }

    public void sendMessageLeft(View view) {
        // Do something in response to button click
    }

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            System.out.println("in doInBackground");
            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            System.out.println("in run");
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            System.out.println("in onProgressUpdate");
            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }


}

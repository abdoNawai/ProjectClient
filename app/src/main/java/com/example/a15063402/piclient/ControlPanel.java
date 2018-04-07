package com.example.a15063402.piclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ControlPanel extends Activity {

    private ListView mList;
    private ArrayList<String> arrayList;
    private ClientListAdapter mAdapter;
    private TcpClient mTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_control_panel);
        arrayList = new ArrayList<String>();


        //relate the listView from java to the one created in xml
        mList = (ListView) findViewById(R.id.list);
        mAdapter = new ClientListAdapter(this, arrayList);
        mList.setAdapter(mAdapter);

        new ConnectTask().execute("");

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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
//
//        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
//        Uri vidUri = Uri.parse(vidAddress);
//        vidView.setVideoURI(vidUri);
//        vidView.start();

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

            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }

}

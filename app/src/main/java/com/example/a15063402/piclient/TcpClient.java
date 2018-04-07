package com.example.a15063402.piclient;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class TcpClient {

    public static final String SERVER_IP = "192.168.0.101"; //your computer IP address
    public static final int SERVER_PORT = 12345;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        boolean x = mBufferOut.checkError();
        if (mBufferOut != null && !mBufferOut.checkError()) {
            System.out.println("from buffer"  + message);
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        // send mesage that we are closing the connection
        sendMessage(Constants.CLOSED_CONNECTION);

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;
        System.out.println("in run run");
        try {
            System.out.println("in try1 run");
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {
                System.out.println("in try2 run");
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // send login name
                sendMessage("Down 0");
                sendMessage("Down 0.5");
                sendMessage("Down 1");

                System.out.println("before loop: " + mRun);
                //in this while the client listens for the messages sent by the server

                int counter = 0;
                while (mRun) {
                    System.out.println("Loop: " + counter++);
                    //System.out.println("in loop: " + mRun);
                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        System.out.println("receiving ");
                        mMessageListener.messageReceived(mServerMessage);
                        System.out.println("msg: "+mServerMessage);

                    }
//                    else{
//                        mRun = false;
//                    }
                    //System.out.println("bottom loop: " + mRun);
                    //mRun=false;
                }
                System.out.println("out loop");
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                sendMessage("Down 1");
                socket.close();
                sendMessage("Down 2");
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}

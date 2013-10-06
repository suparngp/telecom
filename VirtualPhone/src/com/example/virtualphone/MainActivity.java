package com.example.virtualphone;

import java.net.MalformedURLException;

import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final Button startButton = (Button) this.findViewById(R.id.start);
    startButton.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v) 
      {
        startButton.setText("Starting Socket");
        try
        {
          Log.d("SOCKET", "Creating Socket");
          SocketIO socket = new SocketIO("http://127.0.0.1:3001/");
          Log.d("SOCKET", "Connecting Socket");
          socket.connect(new IOCallback()
          {

            @Override
            public void on(String arg0, IOAcknowledge arg1, Object... arg2)
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onConnect()
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onDisconnect()
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onError(SocketIOException arg0)
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onMessage(String arg0, IOAcknowledge arg1)
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onMessage(JSONObject arg0, IOAcknowledge arg1)
            {
              // TODO Auto-generated method stub
              
            }
            
          });
          Log.d("SOCKET", "Sending message to the server");
          socket.send("Hello Server!");
        } 
        catch (MalformedURLException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        };
      }
    });
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}

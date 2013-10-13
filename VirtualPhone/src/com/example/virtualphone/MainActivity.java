package com.example.virtualphone;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.example.messages.Contact;
import com.example.messages.ContactMessage;
import com.google.gson.Gson;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.telephony.SmsManager;
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
        ArrayList<Contact> contactList;
        ContactMessage contactMsg;
        Gson gson = new Gson();

        startButton.setText("Started Socket");
        contactList = getContacts();
        
        contactMsg = new ContactMessage();
        contactMsg.setContactList(contactList);
        contactMsg.setMsgType("Contact List");
        
        String gsonString = gson.toJson(contactMsg);
        Log.e("GSON", gsonString);

        //sendTextMessage("15555215554", "om");
        //readTextMessage();
        
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

  private void sendTextMessage(String sNum, String sMsg)
  {
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(sNum, null, sMsg, null, null);
    Log.d("SEND_SMS", "Sent SMS");
  }
  
  private void readTextMessage()
  {
    ContentResolver contResolver = getContentResolver();
    final String[] projection = new String[]{"*"};
    Uri uri = Uri.parse("content://sms/");
    Cursor query = contResolver.query(uri, projection, null, null, null);
    
    String[] columns = new String[] { "address", "person", "date", "body","type" };
    
    if(query.getCount() > 0)
    {
      String count = Integer.toString(query.getCount());
      System.out.println(count + "\n");
      while(query.moveToNext())
      {
        String address = query.getString(query.getColumnIndex(columns[0]));
        String name = query.getString(query.getColumnIndex(columns[1]));
        String date = query.getString(query.getColumnIndex(columns[2]));
        String msg = query.getString(query.getColumnIndex(columns[3]));
        String type = query.getString(query.getColumnIndex(columns[4]));
        
        Log.e("READ_SMS", address);
        if(name != null)
        {
          Log.e("READ_SMS", name);
        }
        Log.e("READ_SMS", date);
        Log.e("READ_SMS", msg);
        Log.e("READ_SMS", type);
        Log.e("READ_SMS", count);
      }
    }
  }
  
  private ArrayList<Contact> getContacts()
  {
    ArrayList<Contact> retList = new ArrayList<Contact>();
    Contact tmpContact;
    
    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null, null);
    String[] columns = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER };

    if(cursor.getCount() > 0)
    {
      String count = Integer.toString(cursor.getCount());
      System.out.println(count + "\n");
      while(cursor.moveToNext())
      {
        String name = cursor.getString(cursor.getColumnIndex(columns[0]));
        String pNum = (cursor.getString(cursor.getColumnIndex(columns[1]))).replaceAll("[\\-\\s]", "");

        tmpContact = new Contact();
        tmpContact.setContactName(name);
        tmpContact.setContactNum(pNum);
        retList.add(tmpContact);

        Log.e("GET_CONTACT", tmpContact.getContactName());
        Log.e("GET_CONTACT", tmpContact.getContactNum());
      }
    }
    
    return retList;
  }
}

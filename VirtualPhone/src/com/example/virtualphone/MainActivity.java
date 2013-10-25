package com.example.virtualphone;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.example.messages.Contact;
import com.example.messages.ContactMessage;
import com.google.gson.Gson;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
  public SipManager mSipManager = null;
  public SipProfile sipProf = null;
  public SipAudioCall call = null;
  public IncomingCallReceiver callReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final Button startButton = (Button) this.findViewById(R.id.start);
    final Button bSip1 = (Button) this.findViewById(R.id.sipPhone1);
    final Button bSip2 = (Button) this.findViewById(R.id.sipPhone2);
    
    
    //Don't really need this if you add it in your manifest!!
    IntentFilter filter = new IntentFilter();
    filter.addAction("android.SipDemo.INCOMING_CALL");
    callReceiver = new IncomingCallReceiver();
    this.registerReceiver(callReceiver, filter);
    
    
    //This will create and start web-socket
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
    
    //this will register client 1 with getonsip.com server
    bSip1.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View arg0)
      {
        bSip1.setText("Registered/Started SIP 1");
        inititializeSip(getString(R.string.sipUsername1), 
            getString(R.string.sipPswd1));
      }
      
    });
    
    //this will register client 2 with getonsip.com server
    bSip2.setOnClickListener(new OnClickListener()
    {

      @Override
      public void onClick(View arg0)
      {
        // TODO Register Sip 2 Profile
        bSip2.setText("Registered/Started SIP 2");
      }
      
    });
    
  }

  //This creates the menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  //Proof of Concept to send text message (VERIFIED)
  private void sendTextMessage(String sNum, String sMsg)
  {
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(sNum, null, sMsg, null, null);
    Log.d("SEND_SMS", "Sent SMS");
  }
  
  //Proof of Concept to read text message from inbox (VERIFIED)
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
  
  //This will get contacts from the contact list (VERIFIED)
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
  
  //This registers SIP profile. Nutshell - Tells SIP server your current location
  private void inititializeSip(String uname, String pswd)
  {
    if(mSipManager == null) 
    {
      mSipManager = SipManager.newInstance(this);
    }
    
    if(mSipManager == null)
    {
      //Good programming - send error
      return;
    }
    if(sipProf != null)
    {
      closeLocalProfile();
    }
    
    try
    {
      //Builds the profile
      SipProfile.Builder builder = new SipProfile.Builder(uname, getString(R.string.sipDomain));
      builder.setPassword(pswd);
      sipProf = builder.build();
      
      /*
       * Creates Intent to receive incoming calls.
       * When the SIP call is received, it is broadcasted. 
       * Broadcast Receiver - IncominCallReceiver will catch the proadcast and take action
       */
      Intent i = new Intent();
      i.setAction("android.SipDemo.INCOMING_CALL");
      PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
      mSipManager.open(sipProf, pi, null);
      
      Log.e("SIP_DEV", sipProf.getUriString());
      
      //Tries to Register the profile with getonsip.com server
      mSipManager.setRegistrationListener(sipProf.getUriString(), new SipRegistrationListener() 
      {
        public void onRegistering(String localProfileUri) 
        {
            Log.e("SIP_DEV", "Registering with SIP Server...");
        }

        public void onRegistrationDone(String localProfileUri, long expiryTime) {
            Log.e("SIP_DEV", "READY");
        }

        public void onRegistrationFailed(String localProfileUri, int errorCode,
                String errorMessage) 
        {
            Log.e("SIP_DEV", "Registration failed.  Please check settings.");
        }
      });
    }
    catch (java.text.ParseException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SipException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  //Close the profile when done with it
  public void closeLocalProfile()
  {
    if(mSipManager == null)
    {
      return;
    }
    try
    {
      if(sipProf != null)
      {
        mSipManager.close(sipProf.getUriString());
      }
    }
    catch (Exception ee) 
    {
      Log.d("SIP_DEV", "Failed to close local profile.", ee);
    }
  }
  
  
  //Call this when you want to initiate the call
  public void sipCall(String address)
  {
    try
    {
      SipAudioCall.Listener listener = new SipAudioCall.Listener()
      {
        @Override
        public void onCallEstablished(SipAudioCall call) 
        {
          Log.d("SIP_DEV", "SIP Call Established");
          call.startAudio();
          call.setSpeakerMode(true);
          call.toggleMute();
        }

        @Override
        public void onCallEnded(SipAudioCall call) 
        {
          Log.d("SIP_DEV", "SIP Call Ended");
        }
      };
      call = mSipManager.makeAudioCall(sipProf.getUriString(), address, listener, 30);
    }
    catch (Exception e)
    {
      if(sipProf != null)
      {
        try
        {
          mSipManager.close(sipProf.getUriString());
        }
        catch(Exception ee)
        {
          ee.printStackTrace();
        }
      }
      if(call != null)
      {
        call.close();
      }
    }
  }
}

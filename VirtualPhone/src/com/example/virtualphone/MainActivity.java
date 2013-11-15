package com.example.virtualphone;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
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
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity
{
  public SipManager mSipManager = null;
  public SipProfile sipProf = null;
  public SipAudioCall call = null;
  public IncomingCallReceiver callReceiver;
  
  private String serverAdd = "http://ec2-54-201-27-106.us-west-2.compute.amazonaws.com:3000/";
  
  private TextView tSocStatus;
  private TextView tSipStatus;
  private TextView tLogs;
  private EditText eSipAdd;
  
  private Button bStartSocket;
  private Button bSipReg;
  
  private Handler myHandler;

  private OnClickListener socketStartListener = new OnClickListener()
  {
    @Override
    public void onClick(View v) 
    {
      myHandler = new Handler();
      
      ArrayList<Contact> contactList;
      ContactMessage contactMsg;
      Gson gson = new Gson();
      
      contactList = getContacts();
      
      contactMsg = new ContactMessage();
      contactMsg.setContactList(contactList);
      contactMsg.setMsgType("Contact List");
      
      String gsonString = gson.toJson(contactMsg);
      Log.e("GSON", gsonString);

      try
      {
        Log.d("SOCKET", "Creating Socket");
        SocketIO socket = new SocketIO(serverAdd);
        Log.d("SOCKET", "Connecting Socket: " + serverAdd);
        socket.connect(new IOCallback()
        {

          @Override
          public void on(String arg0, IOAcknowledge arg1, Object... arg2)
          {
            Log.e("SOCKET", "In the ON function " + arg0 + " ** " + arg1.toString());
            
          }

          @Override
          public void onConnect()
          {
            Log.e("SOCKET", "In the onConnect function ");
            
            myHandler.post(new Runnable()
            {

              @Override
              public void run()
              {
                tSocStatus.setText("Connected to: " + serverAdd);
                tSocStatus.setTextColor(Color.GREEN);
                tSocStatus.setTypeface(null, Typeface.BOLD);
              }
              
            });
          }

          @Override
          public void onDisconnect()
          {
            Log.e("SOCKET", "In the onDisconnect function ");
            
          }

          @Override
          public void onError(SocketIOException arg0)
          {
            final String exception = arg0.toString();
            Log.e("SOCKET", "In the onError function " + exception);

            myHandler.post(new Runnable()
            {

              @Override
              public void run()
              {
                tSocStatus.setText("Server Error: " + exception);
                tSocStatus.setTextColor(Color.RED);
                tSocStatus.setTypeface(null, Typeface.BOLD);
              }
              
            });
          }

          @Override
          public void onMessage(String arg0, IOAcknowledge arg1)
          {
            Log.e("SOCKET", "In the onMessage function " + arg0 + " ** " + arg1.toString());
            
          }

          @Override
          public void onMessage(JSONObject arg0, IOAcknowledge arg1)
          {
            try
            {
              Log.e("SOCKET", "In the onMessage JSON function " + arg0.toString(2) + " ** " + arg1.toString());
            } catch (JSONException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
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
  };
  
  private OnClickListener sipRegListener = new OnClickListener()
  {

    @Override
    public void onClick(View arg0)
    {
      String add = eSipAdd.getText().toString();
      
      if(add == null || !add.contains("@"))
      {
        tSipStatus.setText("Invalid Address");
        tSipStatus.setTextColor(Color.RED);
        tSipStatus.setTypeface(null, Typeface.BOLD);
        return;
      }
      String splitr [] = add.split("\\@");
      Log.e("SIP_REG_BUT", "uname="+splitr[0]+" domain="+splitr[1]);
      inititializeSip(splitr[0], splitr[1], getString(R.string.sipPswd1));
    }
    
  };

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
    String[] columns = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER ,ContactsContract.Contacts.DISPLAY_NAME, CommonDataKinds.Email.DATA};
    
    if(cursor.getCount() > 0)
    {
      String count = Integer.toString(cursor.getCount());
      System.out.println(count + "\n");
      while(cursor.moveToNext())
      {      
        String pName = cursor.getString(cursor.getColumnIndex(columns[0]));
        Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[] {CommonDataKinds.Email._ID, ContactsContract.Contacts.DISPLAY_NAME, CommonDataKinds.Email.DATA}, null, null, null);
        
        if(cursorEmail.getCount() > 0) 
        {
          while(cursorEmail.moveToNext()) 
          {
            String eName = cursorEmail.getString(cursorEmail.getColumnIndex(columns[2]));

            if(pName.equals(eName)) 
            {
              String name = pName;
              String pNum = (cursor.getString(cursor.getColumnIndex(columns[1]))).replaceAll("[\\-\\s\\(\\)]", "");
              String email = cursorEmail.getString(cursorEmail.getColumnIndex(columns[3]));
                          
              tmpContact = new Contact();
              tmpContact.setContactName(name);
              tmpContact.setContactNum(pNum);
              tmpContact.setContactEmail(email);
              
              retList.add(tmpContact);
              
              Log.e("GET_CONTACT", tmpContact.getContactName());
              Log.e("GET_CONTACT", tmpContact.getContactNum());
              Log.e("GET_CONTACT", tmpContact.getContactEmail());
            }
          }
          cursorEmail.close();
        }
      }
    }
    return retList;
  }
  
  //This registers SIP profile. Nutshell - Tells SIP server your current location
  private void inititializeSip(String uname, String domain, String pswd)
  {
    myHandler = new Handler();
    
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
      SipProfile.Builder builder = new SipProfile.Builder(uname, domain);
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
          
          myHandler.post(new Runnable()
          {

            @Override
            public void run()
            {
              tSipStatus.setText("Registering with the SIP Server...");
              tSipStatus.setTextColor(Color.BLACK);
              tSipStatus.setTypeface(null, Typeface.BOLD);
              
            }
            
          });
        }

        public void onRegistrationDone(String localProfileUri, long expiryTime) 
        {
          final String profile = localProfileUri;
          Log.e("SIP_DEV", "SIP Registration SUCCESSFUL");
            
          myHandler.post(new Runnable()
          {

            @Override
            public void run()
            {
              tSipStatus.setText("Registering SUCCESSFUL - " + profile);
              tSipStatus.setTextColor(Color.GREEN);
              tSipStatus.setTypeface(null, Typeface.BOLD);
            }
            
          });
        }

        public void onRegistrationFailed(String localProfileUri, int errorCode,
                String errorMessage) 
        {
          final String profile = localProfileUri;
          final String errMsg = errorMessage;
          
          Log.e("SIP_DEV", "Registration failed.  Please check settings.");
          
          myHandler.post(new Runnable()
          {

            @Override
            public void run()
            {
              tSipStatus.setText("Registering FAILED - " + profile + " " + errMsg);
              tSipStatus.setTextColor(Color.RED);
              tSipStatus.setTypeface(null, Typeface.BOLD);
            }
            
          });
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
      Log.e("SIP_DEV", "Failed to close local profile.", ee);
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
          Log.e("SIP_DEV", "SIP Call Established");
          call.startAudio();
          call.setSpeakerMode(true);
          call.toggleMute();
        }

        @Override
        public void onCallEnded(SipAudioCall call) 
        {
          Log.e("SIP_DEV", "SIP Call Ended");
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
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    bStartSocket = (Button) this.findViewById(R.id.startSocket);
    bSipReg = (Button) this.findViewById(R.id.sipReg);
    
    tSocStatus = (TextView) this.findViewById(R.id.socketStatus);
    tSipStatus = (TextView) this.findViewById(R.id.sipStatus);
    tLogs = (TextView) this.findViewById(R.id.logs);
    
    eSipAdd = (EditText) this.findViewById(R.id.sipAdd);
    
    //Don't really need this if you add it in your manifest!!
    IntentFilter filter = new IntentFilter();
    filter.addAction("android.SipDemo.INCOMING_CALL");
    callReceiver = new IncomingCallReceiver();
    this.registerReceiver(callReceiver, filter);
    
    
    //This will create and start web-socket
    bStartSocket.setOnClickListener(socketStartListener);
    
    //this will register client 1 with getonsip.com server
    bSipReg.setOnClickListener(sipRegListener);
  }

  //This creates the menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}

package com.example.virtualphone;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.messages.Contact;
import com.example.messages.ContactMessage;
import com.example.messages.SMS;
import com.example.messages.SMSMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
  
  private String serverAdd = "http://ec2-54-201-60-222.us-west-2.compute.amazonaws.com:3000";
  
  private TextView tSocStatus;
  private TextView tSipStatus;
  private TextView tLogs;
  private EditText eSipAdd;
  
  private Button bStartSocket;
  private Button bSipReg;
  private Button bSipEnd;
  
  private Handler myHandler;

  private OnClickListener socketStartListener = new OnClickListener()
  {
    @Override
    public void onClick(View v) 
    {
      myHandler = new Handler();
      
      Gson gson = new Gson();
      
      try
      {
        Log.d("SOCKET", "Creating Socket");
        final SocketIO socket = new SocketIO(serverAdd);
        Log.d("SOCKET", "Connecting Socket: " + serverAdd);
        socket.connect(new IOCallback()
        {

          @Override
          public void on(String arg0, IOAcknowledge arg1, Object... arg2)
          {
            Log.e("SOCKET", "In the ON function " + arg0 );
            if(arg0.equals("contact_req"))
            {
              ArrayList<Contact> contactList = getContacts();
              final ContactMessage contactMsg = new ContactMessage();
              contactMsg.setContactList(contactList);
              contactMsg.setMsgType("Contact List");
              
              String gsonString = new Gson().toJson(contactMsg);
              socket.emit("contact_res", gsonString);
              Log.e("GSON", gsonString);
            }
            else if(arg0.equals("sms_req"))
            {
              ArrayList<SMS> smsList = readTextMessage();
              HashMap<String, ArrayList<SMS>> tmpGroup = new HashMap<String, ArrayList<SMS>>();
              final SMSMessage txtMessage = new SMSMessage();
              
              for(int i=0; i<smsList.size(); i++)
              {
                SMS tmpSms = smsList.get(i);
                if(tmpGroup.containsKey(tmpSms.getContactNum()))
                {
                  tmpGroup.get(smsList.get(i).getContactNum()).add(tmpSms);
                }
                else
                {
                  ArrayList<SMS> tmpList = new ArrayList<SMS>();
                  tmpList.add(tmpSms);
                  tmpGroup.put(tmpSms.getContactNum(), tmpList);
                }
              }
              
              txtMessage.setSmsList(smsList);
              txtMessage.setGroupedList(tmpGroup);
              txtMessage.setMsgType("Text Message List");
              
              String gsonString = new Gson().toJson(txtMessage);
              socket.emit("sms_res", gsonString);
              Log.e("GSON", gsonString);
            }
            else if(arg0.equals("send_sms_req"))
            {
              JsonElement elem = new JsonParser().parse(String.valueOf(arg2[0]));
              String num = elem.getAsJsonObject().get("number").getAsString();
              String msg = elem.getAsJsonObject().get("message").getAsString();
              
              Log.e("SOCKET", "send_sms_req:num=" + num );
              Log.e("SOCKET", "send_sms_req:msg=" + msg );
              
              sendTextMessage(num,msg);
              socket.emit("send_sms_res", "SMS Sent");
            }
            else if(arg0.equals("send_sip_req"))
            {
              JsonElement elem = new JsonParser().parse(String.valueOf(arg2[0]));
              String sipAddress = elem.getAsJsonObject().get("address").getAsString();
              
              Log.e("SOCKET", "send_sip_req=" + sipAddress );
              
              sipCall(sipAddress);
              socket.emit("send_sip_res", "SIP Initiated");
            }
            else if(arg0.equals("end_sip_req"))
            {
              if(call != null)
              {
                try
                {
                  call.endCall();
                } catch (SipException e)
                {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              socket.emit("end_sip_res", "SIP Call Ended");
            }
          }

          @Override
          public void onConnect()
          {
            Log.e("SOCKET", "In the onConnect function ");
            socket.send("Hello Server!");
            
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
            try{
              Log.e("SOCKET", "In the onMessage function " + arg0);
              
            }
            
            catch(Exception e){
              e.printStackTrace();
            }
            
          }

          @Override
          public void onMessage(JSONObject arg0, IOAcknowledge arg1)
          {
            try
            {
              Log.e("SOCKET", "In the onMessage JSON function " + arg0.toString(2));
            } catch (JSONException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
          }
          
        });
//        Log.d("SOCKET", "Sending message to the server");
//        socket.send("Hello Server!");
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
  
  private OnClickListener sipEndListener = new OnClickListener()
  {

    @Override
    public void onClick(View arg0)
    {
      if(call != null)
      {
        try
        {
          call.endCall();
        } catch (SipException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      
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
  private ArrayList<SMS> readTextMessage()
  {
    ArrayList<SMS> smsList = new ArrayList<SMS>();
    SMS tmpSMS;
    
    ContentResolver contResolver = getContentResolver();
    final String[] projection = new String[]{"*"};
    Uri uri = Uri.parse("content://sms/");
    Cursor query = contResolver.query(uri, projection, null, null, null);
    
    String[] columns = new String[] { "address", "person", "date", "body","type", "read" };
    
    if(query.getCount() > 0)
    {
      while(query.moveToNext())
      {
        //Log.e("READ_SMS", new Gson().toJson(query));
        String address = "";
        String name = "";
        String date = "";
        String msg = "";
        String type = "";
        String read = "";
        if(query.getString(query.getColumnIndex(columns[0])) != null)
        {
          address = query.getString(query.getColumnIndex(columns[0])).replaceAll("[\\+\\-\\s\\(\\)]", "");
        }
        if(query.getString(query.getColumnIndex(columns[1])) != null)
        {
          name = query.getString(query.getColumnIndex(columns[1]));
        }
        if(query.getString(query.getColumnIndex(columns[2])) != null)
        {
          date = query.getString(query.getColumnIndex(columns[2]));
        }
        if(query.getString(query.getColumnIndex(columns[3])) != null)
        {
          msg = query.getString(query.getColumnIndex(columns[3]));
        }
        if(query.getString(query.getColumnIndex(columns[4])) != null)
        {
        type = query.getString(query.getColumnIndex(columns[4]));
        }
        if(query.getString(query.getColumnIndex(columns[5])) != null)
        {
          read = query.getString(query.getColumnIndex(columns[5]));
        }
        tmpSMS = new SMS();
        
        tmpSMS.setContactNum(address);
        tmpSMS.setContactName(name);
        tmpSMS.setDate(date);
        tmpSMS.setMessage(msg);
        tmpSMS.setMsgType(type);
        tmpSMS.setMsgRead(read);
        
        smsList.add(tmpSMS);
        
        Log.e("READ_SMS address=", address);
        if(name != null)
        {
          Log.e("READ_SMS name=", name);
        }
        Log.e("READ_SMS date=", date);
        Log.e("READ_SMS msg=", msg);
        Log.e("READ_SMS type=", type);
        Log.e("READ_SMS count=", read);
      }
    }
    return smsList;
  }
  
  //This will get contacts from the contact list (VERIFIED)
  private ArrayList<Contact> getContacts()
  {
    ArrayList<Contact> retList = new ArrayList<Contact>();
    Contact tmpContact;
    
    ContentResolver cr = getContentResolver();
    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
    
    //Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {Phone._ID, Phone.DISPLAY_NAME, Phone.NUMBER}, null, null, null);
    String[] columns = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER ,ContactsContract.Contacts.DISPLAY_NAME, CommonDataKinds.Email.DATA};
    
    if(cursor.getCount() > 0)
    {
      String count = Integer.toString(cursor.getCount());
      System.out.println(count + "\n");
      while(cursor.moveToNext())
      {      
        String pName = cursor.getString(cursor.getColumnIndex(columns[0]));
        String pNum = "";
        //String pNum = (cursor.getString(cursor.getColumnIndex(columns[1]))).replaceAll("[\\-\\s\\(\\)]", "");
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String email = "";
        
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
        {
          Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
          while (pCur.moveToNext()) 
          {
            pNum = pCur.getString(pCur.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[\\+\\-\\s\\(\\)]", "");
          }
          pCur.close();
          
          Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
              null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
          
          //Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[] {CommonDataKinds.Email._ID, ContactsContract.Contacts.DISPLAY_NAME, CommonDataKinds.Email.DATA}, null, null, null);
          
          if(cursorEmail.getCount() > 0) 
          {
            while(cursorEmail.moveToNext()) 
            {
              email = cursorEmail.getString(cursorEmail.getColumnIndex(columns[3]));
            }
            cursorEmail.close();
          }
        }
        tmpContact = new Contact();
        tmpContact.setContactName(pName);
        tmpContact.setContactNum(pNum);
        tmpContact.setContactEmail(email);
        
        retList.add(tmpContact);
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
    } 
    catch (SipException e)
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
    bSipEnd = (Button) this.findViewById(R.id.sipEnd);
    
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
    
    bSipEnd.setOnClickListener(sipEndListener);
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

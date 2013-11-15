package com.example.virtualphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver 
{

  @Override
  public void onReceive(Context arg0, Intent arg1)
  {
    SipAudioCall incomingCall = null;
    
    try
    {
      SipAudioCall.Listener listener = new SipAudioCall.Listener() 
      {
        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) 
        {
            try 
            {
              call.answerCall(30);
            } 
            catch (Exception e) 
            {
              Log.e("SIP_DEV", "Call Connection Failed " + e.toString());
              e.printStackTrace();
            }
        }
      };
      MainActivity mainAct = (MainActivity) arg0;
      incomingCall = mainAct.mSipManager.takeAudioCall(arg1, listener);
      incomingCall.answerCall(30);
      incomingCall.startAudio();
      incomingCall.setSpeakerMode(true);
      
      Log.e("SIP_DEV", "SIP Call State is "+ incomingCall.getState());
      
      if(incomingCall.isMuted()) 
      {
        Log.e("SIP_DEV", "Incoming Call is muted. Toggling mute");
        incomingCall.toggleMute();
      }
      mainAct.call = incomingCall;
    }
    catch(Exception ee)
    {
      if(incomingCall != null)
      {
        incomingCall.close();
      }
    }
    
  }

}

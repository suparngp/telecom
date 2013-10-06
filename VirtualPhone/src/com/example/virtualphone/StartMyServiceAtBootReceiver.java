package com.example.virtualphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver 
{

  @Override
  public void onReceive(Context arg0, Intent arg1)
  {
    if("android.intent.action.BOOT_COMPLETED".equals(arg1.getAction()))
    {
      Toast.makeText(arg0, "DEBUG: Boot Completed", Toast.LENGTH_LONG).show();
      Log.i("DEBUG", "Starting Intent");
      Intent serviceIntent = new Intent(arg0, MainActivity.class);
      serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      arg0.startActivity(serviceIntent);
    }
    
  }

}

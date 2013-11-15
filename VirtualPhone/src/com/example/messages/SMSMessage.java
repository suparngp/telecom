/**
 * 
 */
package com.example.messages;

import java.util.ArrayList;

/**
 * @author Aditya
 *
 */
public class SMSMessage
{
  String msgType;
  ArrayList<SMS> smsList;

  /**
   * @return the msgType
   */
  public String getMsgType()
  {
    return msgType;
  }
  /**
   * @param msgType the msgType to set
   */
  public void setMsgType(String msgType)
  {
    this.msgType = msgType;
  }
  /**
   * @return the smsList
   */
  public ArrayList<SMS> getSmsList()
  {
    return smsList;
  }
  /**
   * @param smsList the smsList to set
   */
  public void setSmsList(ArrayList<SMS> smsList)
  {
    this.smsList = smsList;
  }

}

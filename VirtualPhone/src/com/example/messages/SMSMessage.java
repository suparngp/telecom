/**
 * 
 */
package com.example.messages;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Aditya
 *
 */
public class SMSMessage
{
  private String msgType;
  private ArrayList<SMS> smsList;
  private HashMap<String, ArrayList<SMS>> groupedList;

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
  /**
   * @return the groupedList
   */
  public HashMap<String, ArrayList<SMS>> getGroupedList()
  {
    return groupedList;
  }
  /**
   * @param groupedList the groupedList to set
   */
  public void setGroupedList(HashMap<String, ArrayList<SMS>> groupedList)
  {
    this.groupedList = groupedList;
  }

}

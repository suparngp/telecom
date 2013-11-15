/**
 * 
 */
package com.example.messages;

/**
 * @author Aditya
 *
 */
public class SMS
{
  private String contactName;
  private String contactNum;
  private String date;
  private String message;
  private String msgType;
  private String msgRead;
  
  /**
   * @return the contactName
   */
  public String getContactName()
  {
    return contactName;
  }
  /**
   * @param contactName the contactName to set
   */
  public void setContactName(String contactName)
  {
    this.contactName = contactName;
  }
  /**
   * @return the contactNum
   */
  public String getContactNum()
  {
    return contactNum;
  }
  /**
   * @param contactNum the contactNum to set
   */
  public void setContactNum(String contactNum)
  {
    this.contactNum = contactNum;
  }
  /**
   * @return the date
   */
  public String getDate()
  {
    return date;
  }
  /**
   * @param date the date to set
   */
  public void setDate(String date)
  {
    this.date = date;
  }
  /**
   * @return the message
   */
  public String getMessage()
  {
    return message;
  }
  /**
   * @param message the message to set
   */
  public void setMessage(String message)
  {
    this.message = message;
  }
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
   * @return the msgRead
   */
  public String getMsgRead()
  {
    return msgRead;
  }
  /**
   * @param msgRead the msgRead to set
   */
  public void setMsgRead(String msgRead)
  {
    this.msgRead = msgRead;
  }

}

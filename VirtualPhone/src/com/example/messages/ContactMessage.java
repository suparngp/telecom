package com.example.messages;

import java.util.ArrayList;

public class ContactMessage
{
  String msgType;
  ArrayList<Contact> contactList;

  public ArrayList<Contact> getContactList()
  {
    return contactList;
  }
  public void setContactList(ArrayList<Contact> contactList)
  {
    this.contactList = contactList;
  }
  public String getMsgType()
  {
    return msgType;
  }
  public void setMsgType(String msgType)
  {
    this.msgType = msgType;
  }
}

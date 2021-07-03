package com.example.regain.Comperators;

import com.example.regain.Classes.Contact;
import com.example.regain.Classes.Message;

public class CompareByDate_contact implements java.util.Comparator<Contact> {
       @Override
    public int compare(Contact o1, Contact o2) {
            if(o2.getTime() < o1.getTime())
                return -1;
            return 1;
    }
}

package com.example.regain.Comperators;

import com.example.regain.Classes.Message;

public class CompareByDate_message implements java.util.Comparator<Message> {
       @Override
    public int compare(Message o1, Message o2) {
            if(o2.getTime() > o1.getTime())
                return -1;
            return 1;
    }
}

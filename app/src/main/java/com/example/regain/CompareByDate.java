package com.example.regain;

public class CompareByDate implements java.util.Comparator<Message> {
       @Override
    public int compare(Message o1, Message o2) {
            if(o2.getTime() > o1.getTime())
                return -1;
            return 1;
    }
}

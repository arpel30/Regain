package com.example.regain.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.regain.Classes.Contact;
import com.example.regain.Classes.MyUtils;
import com.example.regain.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter_Contacts extends RecyclerView.Adapter<Adapter_Contacts.MyViewHolder> {

//    private List<String> contacts;
    private List<Contact> contacts;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public Adapter_Contacts(Context context, List<Contact> _contacts) {
        this.mInflater = LayoutInflater.from(context);
        this.contacts = _contacts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.contact_item, parent, false);
        return new MyViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        String name = contact.getName();
        holder.contact_LBL_name.setText(name);
        holder.contact_LBL_date.setText(getTime(contact.getTime()));
        MyUtils.setProfilePicture(MyUtils.getUserName(context), holder.contact_IMG_profile, name);
//        holder.contact_IMG_profile.setImageBitmap();
    }

    private String getTime(long time){
        String newTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM  HH:mm");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM  HH:mm");
        Date resultdate = new Date(time);
        return (dateFormat.format(resultdate));
//        return newTime;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return contacts.get(id).getName();
    }

    // allows clicks events to be caught
    public void setClickListener(MyItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }


    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contact_LBL_name;
        TextView contact_LBL_date;
        ImageView contact_IMG_profile;

        MyViewHolder(View itemView) {
            super(itemView);
            contact_LBL_name = itemView.findViewById(R.id.contact_LBL_name);
            contact_LBL_date = itemView.findViewById(R.id.contact_LBL_date);
            contact_IMG_profile = itemView.findViewById(R.id.contact_IMG_profile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }
}

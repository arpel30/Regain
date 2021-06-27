package com.example.regain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_Contacts extends RecyclerView.Adapter<Adapter_Contacts.MyViewHolder> {

    private List<String> contacts;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    Adapter_Contacts(Context context, List<String> _contacts) {
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
        String name = contacts.get(position);
        holder.contact_LBL_name.setText(name);
        MyUtils.setProfilePicture(MyUtils.getUserName(context), holder.contact_IMG_profile, name);
//        holder.contact_IMG_profile.setImageBitmap();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return contacts.get(id);
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
        ImageView contact_IMG_profile;

        MyViewHolder(View itemView) {
            super(itemView);
            contact_LBL_name = itemView.findViewById(R.id.contact_LBL_name);
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

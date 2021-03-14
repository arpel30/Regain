package com.example.regain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter_Messages extends RecyclerView.Adapter<Adapter_Messages.MyViewHolder> {

    private List<Message> messages;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;

    // data is passed into the constructor
    Adapter_Messages(Context context, List<Message> _messages) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = _messages;
    }

    // inflates the row layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_item, parent, false);
        return new MyViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.message_LBL_text.setText(message.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM  HH:mm");
        Date resultdate = new Date(message.getTime());
        holder.message_LBL_date.setText(sdf.format(resultdate));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // convenience method for getting data at click position
    Message getItem(int id) {
        return messages.get(id);
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

        TextView message_LBL_date;
        TextView message_LBL_text;

        MyViewHolder(View itemView) {
            super(itemView);
            message_LBL_date = itemView.findViewById(R.id.message_LBL_date);
            message_LBL_text = itemView.findViewById(R.id.message_LBL_text);

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


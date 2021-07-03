package com.example.regain.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.regain.Classes.Constants;
import com.example.regain.Classes.Message;
import com.example.regain.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Adapter_Messages extends RecyclerView.Adapter<Adapter_Messages.MyViewHolder> {

    private List<Message> messages;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;

    // data is passed into the constructor
    public Adapter_Messages(Context context, List<Message> _messages) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = _messages;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public boolean isImage(String content){
        if(content == null)
            return false;
        if(content.length() <= Constants.IMAGE_KEY.length())
            return false;
        for(int i=0; i<Constants.IMAGE_KEY.length(); i++){
            if(Constants.IMAGE_KEY.charAt(i) != content.charAt(i))
                return false;
        }
        return true;
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
        String content = message.getText();
        if(!isImage(content)) {
            holder.message_LBL_text.setText(content);
            holder.message_LBL_text.setVisibility(View.VISIBLE);
            holder.message_LBL_image.setVisibility(View.GONE);

        }else{
            Bitmap btm = decodeBase64(content.substring(Constants.IMAGE_KEY.length()-1, content.length()-1));
//            Bitmap btm = decodeBase64(content);
            holder.message_LBL_image.setImageBitmap(btm);
            holder.message_LBL_image.setVisibility(View.VISIBLE);
            holder.message_LBL_text.setVisibility(View.GONE);
        }

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
        ImageView message_LBL_image;

        MyViewHolder(View itemView) {
            super(itemView);
            message_LBL_date = itemView.findViewById(R.id.message_LBL_date);
            message_LBL_text = itemView.findViewById(R.id.message_LBL_text);
            message_LBL_image = itemView.findViewById(R.id.message_LBL_image);

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



package com.steve.safecontacts;
/*
    Author: Steve Bao
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class callListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Person> itemList;

    public callListAdapter(Context context, ArrayList<Person> personList) {
        this.context = context;
        this.itemList = personList;
    }
    @Override
    public int getCount() {
        return itemList.size();
    }
    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.call_list_item, null);
            TextView tvName = (TextView) convertView.findViewById(R.id.txtName);
            TextView tvPhone = (TextView) convertView.findViewById(R.id.txtPhone);
            ImageView imgDial = (ImageView) convertView.findViewById(R.id.img_call);
            Person m = itemList.get(position);
            tvName.setText(m.getName());
            tvPhone.setText(m.getPhone());
            // click listiner for remove button
            imgDial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean call_out = MainActivity.phone_call_ok;

                    Person p = itemList.get(position);

                    Intent intent;
                    if (call_out) {
                        Uri uri = Uri.parse("tel:" + p.getPhone());
                        intent = new Intent(Intent.ACTION_CALL, uri);
                       // intent.putExtra("EXTRA_PHONE_NUMBER", uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + p.getPhone()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        // If package resolves to an app, send intent.
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            Log.d("PhoneCall", "Can't resolve app for ACTION_DIAL failure");
                        }
                    }
                }
            });
        }
        return convertView;
    }
}

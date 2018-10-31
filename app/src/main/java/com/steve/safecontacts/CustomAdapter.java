package com.steve.safecontacts;
/*
    Author: Steve Bao
*/

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Person> itemList;
    EditText edName, edPhone;

    public CustomAdapter(Context context, EditText etphone, EditText etname, ArrayList<Person> personList) {
        this.context = context;
        this.itemList = personList;
        edName = (EditText) etname;
        edPhone = (EditText) etphone;
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
            convertView = mInflater.inflate(R.layout.item, null);
            TextView tvName = (TextView) convertView.findViewById(R.id.txtName);
            TextView tvPhone = (TextView) convertView.findViewById(R.id.txtPhone);
            ImageView imgRemove = (ImageView) convertView.findViewById(R.id.img_remove);
            Person m = itemList.get(position);
            tvName.setText(m.getName());
            tvPhone.setText(m.getPhone());
            // click listiner for remove button
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Person p = itemList.get(position);
                    edPhone.setText(p.getPhone());
                    edName.setText(p.getName());
                    itemList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}

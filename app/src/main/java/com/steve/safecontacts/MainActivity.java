package com.steve.safecontacts;
/*
    Author: Steve Bao
*/

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    public static boolean phone_call_ok = false;
    ArrayList<Person> newlist = new ArrayList<>();
    callListAdapter callAdapter;
    ListView listview;
    private static final int PERMISSION_REQUEST_PHONE_CALL = 555;
    File file;
    String contacts = "contacts.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        request_phone_call_previlige();
        getSupportActionBar().setTitle(R.string.app_name);
        listview = (ListView)findViewById(R.id.listview);
    }

    @Override
    public void onResume() {
        super.onResume();
        readContacts();
    }

    @Override
    public void onPause() {
        super.onPause();
       // newlist.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.gosetting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent = new Intent(this, ImportContacts.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void request_phone_call_previlige() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_PHONE_CALL);
            } else
                phone_call_ok = true;
        }
    }
    protected boolean duplicatedPhoneInNewList(Person p) {
        for (Person person : newlist) {
            if ( person.phone.compareTo(p.phone) == 0)
                return true;
        }
        return false;
    }
    protected void readContacts() {
        file = new File(getBaseContext().getApplicationInfo().dataDir + "/" + contacts);
        if (!file.exists())
            return;
        try {
            BufferedReader bfin = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();

            while ( true ) {
                sb.append(bfin.readLine());
                if (sb.length() < 2)
                    break;
                String[] arr = sb.toString().split(",");
                Person p = new Person (arr[0].trim(), arr[1].trim());
                if (!duplicatedPhoneInNewList(p))
                    newlist.add(p);
                sb.setLength(0);
            }
            bfin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(newlist, new Comparator<Person>() {

            @Override
            public int compare(Person p1, Person p2) {
                return p1.compareTo(p2);
            }
        });
        callAdapter = new callListAdapter(getApplicationContext(), newlist);
        listview.setAdapter(callAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    phone_call_ok = true;
                    Toast.makeText(this, "Call phone permission granted" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Call phone permission denided", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

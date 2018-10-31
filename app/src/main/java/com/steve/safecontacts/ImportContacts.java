package com.steve.safecontacts;
/*
    Author: Steve Bao
*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class ImportContacts extends AppCompatActivity {
    ArrayList<Person> newlist = new ArrayList<>();
    CustomAdapter customAdapter;
    ListView listview;
    boolean WRITE_EXTENAL = true, READ_EXTENAL=true, READ_CONTACTS=true;
    private static final int PARAM_READ_EXTERNAL_STORAGE = 222;
    private static final int PARAM_WRITE_EXTERNAL_STORAGE = 333;
    private static final int PARAM_READ_CONTACTS = 444;
    File file;
    String contacts = "contacts.txt";
    EditText edPhone, edName;
    ImageView imgAdd;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contacts);
        setupToolbar();

        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setOverflowIcon(getBaseContext().getDrawable(R.drawable.ic_menu_28)); // requires SDK 21. i.e. Android 5.0 and above.

        imgAdd = (ImageView) findViewById(R.id.imgAdd);
        edPhone = (EditText) findViewById(R.id.editPhone);
        edName = (EditText) findViewById(R.id.editName);
        listview = (ListView) findViewById(R.id.listview);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addValue(v);
            }
        });
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_bg));
        toolbar.setTitle(getResources().getString(R.string.importcontact));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.readContacts:
                readContacts();
                return true;
            case R.id.save:
                saveContacts();
                return true;
            case R.id.load:
                readInternalFile();
                return true;
            case R.id.exportfile:
                //getExternalFilesDir()
                saveExFile();
                return true;
            case R.id.importfile:
                readExFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addValue(View v) {
        String phone = edPhone.getText().toString();
        String name = edName.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter phone number",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            Person p = new Person(name, phone);
            if (!duplicatedPhoneInNewList(p)) {
                newlist.add(p);
                customAdapter.notifyDataSetChanged();
                edName.setText("");
                edPhone.setText("");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        customAdapter = new CustomAdapter(this, edPhone, edName, newlist);
        listview.setAdapter(customAdapter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        newlist.clear();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PARAM_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    WRITE_EXTENAL = false;
                    Toast.makeText(this, "Write external storage permission denided", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case PARAM_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    READ_EXTENAL = false;
                    Toast.makeText(this, "Read external storage permission denided", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case PARAM_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    READ_CONTACTS = false;
                    Toast.makeText(this, "Read contacts permission denided", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkAndRequestPermission(String requestPermission, int permission_param) {

        if(android.os.Build.VERSION.SDK_INT >=android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(requestPermission) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {requestPermission};
                requestPermissions(permissions, permission_param);
            }
        }
    }
    // public static final int REQUEST_WRITE_STORAGE = 112;
    private void saveExFile() {
        checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PARAM_WRITE_EXTERNAL_STORAGE);

        if (!WRITE_EXTENAL) {
            Toast.makeText(getBaseContext(), "Write external storage permission is not granded!", Toast.LENGTH_SHORT).show();
            return;
        }
        // You are allowed to write external storage:
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //String downloadDir = Environment.DIRECTORY_DOWNLOADS;
        File file = new File(downloadDir, contacts);
        saveToFile(file);
    }
    // open *.csv file.
    /*
    private void openExFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*.txt"); // *.csv
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent chooserIntent;
        if (getPackageManager().resolveActivity(intent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(intent, "Open file");
           // chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        try {
            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }
    */
    private void readExFile() {
        checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PARAM_READ_EXTERNAL_STORAGE);

        if (!READ_EXTENAL) {
            Toast.makeText(getBaseContext(), "Read external storage permission is not granded!", Toast.LENGTH_SHORT).show();
            return;
        }
       // openExFile();
        // You are allowed to write external storage:
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // String downloadDir = Environment.DIRECTORY_DOWNLOADS;
        File file = new File(downloadDir, contacts);
        if (file.exists())
            readFromFile(file);
        else
            Toast.makeText(getBaseContext(), "Can not find saved external file.", Toast.LENGTH_SHORT).show();
    }

    private void saveToFile(File file) {
        try {
            BufferedWriter bfout = new BufferedWriter(new FileWriter(file));
            bfout.write(getStringFromContactList());
            bfout.close();
            Toast.makeText(getBaseContext(), "Phone list saved to: " + file.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error on save phone list.", Toast.LENGTH_SHORT).show();
        }
    }
    protected String getStringFromContactList() {
        StringBuilder sb = new StringBuilder();
        for ( Person p : newlist)
            sb.append(p.name + "," + p.phone + "\n");
        sb.append("\n");
        return sb.toString();
    }
    protected void saveContacts() {
        file = new File(getBaseContext().getApplicationInfo().dataDir + "/" + contacts);
        saveToFile(file);
    }

    private void readFromFile(File file) {
        try {
            BufferedReader bfin = new BufferedReader(new FileReader(file));
            newlist.clear();
            while ( true ) {
                String s = bfin.readLine();
                if (s.length() < 2)
                    break;
                String[] arr = s.split(",");
                Person p = new Person (arr[0].trim(), arr[1].trim());
                if (!duplicatedPhoneInNewList(p))
                    newlist.add(p);
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

        customAdapter = new CustomAdapter(this, edPhone, edName, newlist);
        listview.setAdapter(customAdapter);

    }

    protected boolean duplicatedPhoneInNewList(Person p) {
        for (Person person : newlist) {
            if ( person.phone.compareTo(p.phone) == 0)
                return true;
        }
        return false;
    }
    protected void readInternalFile() {
        file = new File(getBaseContext().getApplicationInfo().dataDir + "/" + contacts);
        readFromFile(file);
    }

    protected void readContacts() {
        // check for permission first.
        checkAndRequestPermission(Manifest.permission.READ_CONTACTS, PARAM_READ_CONTACTS);

        if (!READ_CONTACTS) {
            Toast.makeText(getBaseContext(), "Read contacts permission is not granded!", Toast.LENGTH_SHORT).show();
            return;
        }
       // String[] exNums={"858", "936", "281", "713", "832"};
       // ArrayList<String> exList = new ArrayList<String>();
       // for (String s : exNums)
       //     exList.add(s);

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null, null);
        newlist.clear();
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[+ (\\-)]","").trim();

            newlist.add(new Person(name, phoneNumber));
        }
        phones.close();
        Collections.sort(newlist, new Comparator<Person>() {

            @Override
            public int compare(Person p1, Person p2) {
                return p1.compareTo(p2);
            }
        });
        customAdapter = new CustomAdapter(this, edPhone, edName, newlist);
        listview.setAdapter(customAdapter);
    }
}

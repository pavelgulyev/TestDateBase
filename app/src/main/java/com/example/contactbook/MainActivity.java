package com.example.contactbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ArrayList<Person> persons;
    ListView contacts;
    PersonAdapter personsAdapter;
    EditText search;
    DatabaseAdapter adapter;
    Context _context;
    static final int PICKFILE_RESULT_CODE = 1;
    static final int PICKDIR_RESULT_CODE = 2;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    public static final int FOLDERPICKER_PERMISSIONS = 1;
    private static final String LOG_TAG = "AndroidExample";
    String filePath = null;
    JSONHelper jsonHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, EditPerson.class);
        contacts = (ListView)findViewById(R.id.listContacts);
        jsonHelper = new JSONHelper();
        _context = this;
        search = findViewById(R.id.txtSearch);
        adapter = new DatabaseAdapter(this);
        AdapterView.OnItemClickListener itemListener = (parent, v, position, id) -> {
            Person selectedPerson = (Person) parent.getItemAtPosition(position);
            intent.putExtra("id", selectedPerson.getId());
            startActivity(intent);
        };
        contacts.setOnItemClickListener(itemListener);

        persons = new ArrayList<>();
        personsAdapter = new PersonAdapter(this, R.layout.list_item, persons);
        contacts.setAdapter(personsAdapter);
        // установка слушателя изменения текста
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.open();
                persons.clear();
                if(s == null || s.length() == 0)
                    persons.addAll(adapter.getPersons());
                else
                    persons.addAll(adapter.search(s.toString()));
                adapter.close();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.open();
        persons.clear();
        persons.addAll(adapter.getPersons());
        adapter.close();
        personsAdapter.notifyDataSetChanged();
    }
    public void add(View view) {
        Intent intent = new Intent(this, EditPerson.class);
        startActivity(intent);
    }
    public void ImportJson(View view) {

//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.setType("*/*");
//        chooseFile = Intent.createChooser(chooseFile, "Выберите список контактов");
//        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
//        persons.addAll(jsonHelper.importFromJSON(this,filePath));
          askPermissionAndBrowseFile();
//        personsAdapter.notifyDataSetChanged();
    }
    private void askPermissionAndBrowseFile()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23
            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(_context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }
        this.doBrowseFile();
    }

    private void doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, 9999);
    }
    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {
                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i( LOG_TAG,"Permission granted!");
                    Toast.makeText(_context, "Permission granted!", Toast.LENGTH_SHORT).show();
                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i(LOG_TAG,"Permission denied!");
                    Toast.makeText(_context, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        Uri fileUri = data.getData();
                        Log.i(LOG_TAG, "Uri: " + fileUri);


                        try {
                            filePath = JSONHelper.getPathFromUri(this,fileUri);
                        } catch (Exception e) {
                            Log.e(LOG_TAG,"Error: " + e);
                            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                        Log.i("FILE PATH", "PATH: " + filePath);
                        jsonHelper.exportToJSON(this,persons,filePath);
                    }
                }
                break;
            case 9999:
                Uri uri = data.getData();
                Log.i("URI: ", "File Uri: " + uri.toString());
                Uri fileUri = data.getData();

                if (data == null) {return;}
                filePath=uri.toString();
                filePath=filePath.replace(":","/");
                String [] partpath = filePath.split("//com.android.externalstorage.");
                filePath=partpath[1].replaceAll("%3A","/");
                filePath=filePath.replaceAll("documents/tree/primary","/storage/emulated/0");

                File file = new File (filePath, JSONHelper.FILE_NAME);
                Log.i("AbsolutePath", "getAbsolutePath " + file.getAbsolutePath());
                JSONHelper.exportToJSON(this, persons, file.getAbsolutePath());
//                jsonHelper.exportPersons(persons,file.getAbsolutePath());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Файл contacts.json сохранен!", Toast.LENGTH_SHORT);
                toast.show();
                break;

        }

    }

    public void SaveJson(View view) {

            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);



    }


}
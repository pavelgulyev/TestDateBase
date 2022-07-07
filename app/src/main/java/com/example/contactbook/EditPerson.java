package com.example.contactbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditPerson extends AppCompatActivity {
    static final int GALLERY_REQUEST = 1;
    int id =-1;
    Person person;
    byte [] image;
    ImageButton avatar;
    EditText name;
    EditText family;
    EditText patronymic;
    EditText phone;
    Bitmap bitmap = null;
    Button delete;
    DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        name = findViewById(R.id.editTextTextPersonName);
        family = findViewById(R.id.editTextTextPersonFamily);
        patronymic = findViewById(R.id.editTextTextPersonPatr);
        delete = findViewById(R.id.DeleteButton);
        phone = findViewById(R.id.editTextPhone);
        avatar = findViewById(R.id.imageButton2);
        adapter = new DatabaseAdapter(this);
        adapter.open();
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            id = arguments.getInt("id");
            Person person = adapter.getPerson(id);
            adapter.close();
            name.setText(person.getName());
            family.setText(person.getSurname());
            patronymic.setText(person.getPatronymic());
            phone.setText(person.getPhone());
//            avatar.setImageBitmap(person.getImage());
            if(getIntent().getByteArrayExtra("img") != null)
                avatar.setImageBitmap(convertCompressedByteArrayToBitmap(getIntent().getByteArrayExtra("img")));
        }
        if(id==-1) delete.setVisibility(View.INVISIBLE);
    }


    public void onAvatarClick(View view)
    {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mStartForResult.launch(photoPickerIntent);
    }
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() != RESULT_OK)
                return;
            Uri selectedImage = result.getData().getData();
            try {
                image = convertBitmapToByteArray(MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage));
                avatar.setImageBitmap(convertCompressedByteArrayToBitmap(image));
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    });
    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }finally {
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    //Log.e(BitmapUtils.class.getSimpleName(), "ByteArrayOutputStream was not closed");
                }
            }
        }
    }

    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }
    public void onDeleteClick(View view)
    {
        adapter.open();
        adapter.delete(id);
        adapter.close();
        goHome();
    }
    public void onSaveClick(View view) {
        if(!name.getText().toString().equals("") && !family.getText().toString().equals("") &&
                !patronymic.getText().toString().equals("") && !phone.getText().toString().equals("")) {
            person = new Person(id, name.getText().toString(), family.getText().toString(),
                    patronymic.getText().toString(), phone.getText().toString(), image);
            adapter.open();
            if(id!=-1) adapter.update(person);
            else adapter.insert(person);
            adapter.close();
            goHome();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Предупреждение!")
                    .setMessage("Вы не заполнили данные!")
                    .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create();
            builder.show();
        }
    }
    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void onBackClick(View view) {
        goHome();
    }

    private final int REQUEST_CODE_PERMISSION_CALLS=100;
    public void onCallClick(View view)
    {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            String toDial="tel:+7"+phone.getText().toString();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(toDial)));
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_PERMISSION_CALLS);
        }

    }
    // вызывается после ответа пользователя на запрос разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CALLS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String toDial = "tel:+7" + phone.getText().toString();
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(toDial)));
                }
                return;
        }
    }
}
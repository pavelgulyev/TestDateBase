package com.example.contactbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONHelper {
    public static final String FILE_NAME = "data.jsn";
    public static boolean exportToJSON(Context context, List<Person> dataList, String fileName) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setPerson(dataList);
        String jsonString = gson.toJson(dataItems);
        Log.i("Path=",fileName);
        Log.i("Log",jsonString);
        fileName=fileName.replace(":","");
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName)))
        {
            oos.writeObject(dataList);
            System.out.println("File has been written");
        }
        catch(Exception ex) {

            Log.i("error",ex.getMessage());
        }
//        try(FileOutputStream fileOutputStream =
//                    openFileOutput(fileName, Context.MODE_PRIVATE)) {
//            fileOutputStream.write(jsonString.getBytes());
//            Log.i("Log",jsonString);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }

    public static List<Person> importFromJSON(Context context, String fileName) {


//        try(FileInputStream fileInputStream = context.openFileInput(fileName);
//            InputStreamReader streamReader = new InputStreamReader(fileInputStream)){
//            Gson gson = new Gson();
//            DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
//            return  dataItems.getPerson();
//        }
//        catch (IOException ex){
//            ex.printStackTrace();
//        }
        return null;
    }

}

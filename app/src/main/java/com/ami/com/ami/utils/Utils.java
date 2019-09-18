package com.ami.com.ami.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import sim.ami.com.myapplication.R;

/**
 * Created by hi on 5/11/16.
 */
public class Utils {

    public static ArrayList<File> getAllFile(File dir,String fileType){
        Log.e("getAllFile","getAllFile");
        ArrayList<File> fileList = new ArrayList<File>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0)
        {
            for (int i = 0; i < listFile.length; i++)
            {

                if (listFile[i].isDirectory())
                {
                    getAllFile(listFile[i],fileType);

                }
                else
                {
                    if("doc".equals(fileType))
                    {
                        if(listFile[i].getName().endsWith(".pdf") || listFile[i].getName().endsWith(".txt") ||
                                listFile[i].getName().endsWith(".xml") || listFile[i].getName().endsWith(".doc") ||
                                listFile[i].getName().endsWith(".xls") || listFile[i].getName().endsWith(".xlsx"))
                        {
                            fileList.add(listFile[i]);
                        }
                    }
                    else if("music".equals(fileType))
                    {
                        if(listFile[i].getName().endsWith(".mp3"))
                        {
                            fileList.add(listFile[i]);
                        }
                    }
                    else if("video".equals(fileType))
                    {
                        if(listFile[i].getName().endsWith(".mp4"))
                        {
                            Log.e("Utils","listFile"+listFile[i]);
                            fileList.add(listFile[i]);
                        }
                    }
                    else if("image".equals(fileType))
                    {
                        if(listFile[i].getName().endsWith(".png") || listFile[i].getName().endsWith(".jpg")
                                || listFile[i].getName().endsWith(".jpeg") || listFile[i].getName().endsWith(".gif"))
                        {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }
    public static String convertDate(String dateInMilliseconds, String dateFormat) {

        return DateFormat
                .format(dateFormat, Long.parseLong(dateInMilliseconds))
                .toString();
    }
    public static String genFileName(){

        String dateTime = ""+ Calendar.getInstance().getTimeInMillis();
        String dateString = Utils.convertDate(dateTime,"dd-MM-yyyy hh:mm:ss");
        String fileName = dateString.replace(" ","-");
        String _fileName = fileName.replace(":","-");
        return _fileName;
    }
    public static AlertDialog showMessageBox(Context context,String message, DialogInterface.OnClickListener listener) {

        if (message == null) {
            message = "";
        }

        if (listener == null) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            };
        }

        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setMessage(message);
        b.setPositiveButton("OK", listener);
        AlertDialog d = b.show();

        ((TextView) d.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);

        return d;
    }

}

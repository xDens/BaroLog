package com.example.barolog.serviceTools;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;
import android.widget.Toast;

import com.example.barolog.MainActivity;

/**
 * Created by xDens on 8/30/15.
 */
public class FileOperations {
    private FileOperations() {}

    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public static void SaveFile(File filePath, String filename, Context context, String fileContent) {
        File dir = new File(filePath, "Barolog");

        try {
            if (!dir.exists()) dir.mkdirs();

            // fix
            //dir.setExecutable(true);
            dir.setReadable(true);
            dir.setWritable(true);
            MediaScannerConnection.scanFile(context, new String[]{dir.toString()}, null, null);

            File file = new File(dir, filename);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(fileContent);
            myOutWriter.close();
            fOut.close();

            //MediaScannerConnection.scanFile(context, new String[]{dir.toString() + "/*"}, null, null);


        }
        catch (IOException e) {
            Log.e(e.toString(), "SaveFile");
        }
    }
}

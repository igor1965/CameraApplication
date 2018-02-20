package com.igor.cameraapplication;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    ImageView image;
    static final int CAMERA_REQUEST = 1;
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String [] permissionsRequired = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String [] dialogOptions = new String[]{"CAMERA ","STORAGE"};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = (Button)findViewById(R.id.btnCapture);
        image = (ImageView)findViewById(R.id.imageViewCapture);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if ((checkSelfPermission(permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) || checkSelfPermission(permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, REQUEST_CODE);
                    }else{
                        startCamera();
                    }
                        if (shouldShowRequestPermissionRationale(permissionsRequired[0]) || shouldShowRequestPermissionRationale(permissionsRequired[1])) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need multiple permissions 1");
                            builder.setMessage("This app needs camera and storage permissions");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, REQUEST_CODE);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            builder.show();
                        }else if (!shouldShowRequestPermissionRationale(permissionsRequired[0]) || !shouldShowRequestPermissionRationale(permissionsRequired[1])) {
                            //Previously Permission Request was cancelled with 'Don't Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Need multiple permissions 2");
                            builder.setMessage("This app needs camera and storage permissions");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    sentToSettings = true;
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",getPackageName(),null);
                                    intent.setData(uri);
                                    startActivityForResult(intent,REQUEST_PERMISSION_SETTING);
                                    Toast.makeText(getBaseContext(),"Go to permissions to grant camera and storage",Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            builder.show();
                        }else {
                            //just request the permissions

                           // ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, REQUEST_CODE);
                           // Toast.makeText(getBaseContext(),"test else root",Toast.LENGTH_LONG).show();
                        }
                       // ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, REQUEST_CODE);

                }else{
                    //startCamera();
                }


            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, " Permission granted", Toast.LENGTH_LONG).show();

                startCamera();
            }

            else {
           /*     //not granted permissions will be added to permission list for request  again
             List<String> permissionList = new ArrayList<>();

            for (int i = 0;i < permissions.length;i ++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        permissionList.add(permissions[i]);

                }
                if (permissionList.size() > 0){

                ActivityCompat.requestPermissions(MainActivity.this,permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE);*//*

                }*/
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0;i < permissions.length; i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        stringBuilder.append(dialogOptions[i] + "\n");
                }
                if (stringBuilder.length() > 0){
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Need multiple permissions 3");
                    builder.setMessage("Tap SETTINGS,the allow the following permission and try again: \n" + stringBuilder.toString());
                    builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                           // sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getPackageName(),null);
                            intent.setData(uri);
                            startActivityForResult(intent,REQUEST_PERMISSION_SETTING);
                            //Toast.makeText(getBaseContext(),"Go to permissions to grant camera and storage",Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    builder.show();
                }
                }


            }

    public void startCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

       // String pictureName = getPictureName();
       // File imageFile = new File(pictureDirectory,pictureName);
       // Uri pictureUri = Uri.fromFile(imageFile);
       // cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageFile);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

    }

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return "Just picture " + timestamp + ".jpg";
    }

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    String path = "";
;
    if (resultCode == RESULT_OK){
        if (requestCode == CAMERA_REQUEST){

            Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(cameraImage);
             path = saveToInternalStorage(cameraImage);
             Toast.makeText(getApplicationContext(),path,Toast.LENGTH_SHORT).show();
        }
    }
}
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("myImageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"test.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(cw,directory.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
        return directory.getAbsolutePath();

    }




}

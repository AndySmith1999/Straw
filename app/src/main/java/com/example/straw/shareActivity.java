package com.example.straw;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class shareActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharelayout);
        ImageButton btn = findViewById(R.id.imageButton);
        ImageButton btn1 = findViewById(R.id.imageButton2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path=MyApplication.getContextObject().getExternalFilesDir("").toString();
                File file=new File(path);

                File finalImageFile = new File(file, System.currentTimeMillis() + ".jpg");
                try {
                    finalImageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(finalImageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable)MyApplication.getContextObject().getResources().getDrawable(R.drawable.oneplus7pro);
                Bitmap bitmap=bitmapDrawable.getBitmap();
                if (bitmap == null) {
                    Toast.makeText(MyApplication.getContextObject(), "image is not exists",Toast.LENGTH_LONG).show();
                    return;
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                    fos.close();
                    Toast.makeText(MyApplication.getContextObject(), "image is exists"+ finalImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // share to wechat
                File file1 = new File(finalImageFile.getAbsolutePath());
                Uri uri= FileProvider.getUriForFile(shareActivity.this, "com.example.straw.fileProvider",file1);
                Intent intent = new Intent();
                ComponentName comp = new ComponentName("com.tencent.mm",
                        "com.tencent.mm.ui.tools.ShareImgUI");
                intent.setComponent(comp);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Choose the application you want to share"));
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path=MyApplication.getContextObject().getExternalFilesDir("").toString();
                File file=new File(path);

                File finalImageFile = new File(file, System.currentTimeMillis() + ".jpg");
                try {
                    finalImageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(finalImageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable)MyApplication.getContextObject().getResources().getDrawable(R.drawable.oneplus7pro);
                Bitmap bitmap=bitmapDrawable.getBitmap();
                if (bitmap == null) {
                    Toast.makeText(MyApplication.getContextObject(), "image is not exists",Toast.LENGTH_LONG).show();
                    return;
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                    fos.close();
                    Toast.makeText(MyApplication.getContextObject(), "image is exists"+ finalImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // share to wechat
                File file1 = new File(finalImageFile.getAbsolutePath());
                Uri uri= FileProvider.getUriForFile(shareActivity.this, "com.example.straw.fileProvider",file1);
                Intent intent = new Intent();
                ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                intent.setComponent(comp);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Choose the application you want to share"));
            }
        });

    }


}


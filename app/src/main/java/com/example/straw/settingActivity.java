package com.example.straw;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class settingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);
//        Drawable drawable=getResources().getDrawable(R.drawable.chinese);
//        drawable.setBounds(0, 0, 25, 25);
        final ImageButton actionA = (ImageButton) findViewById(R.id.Chinese);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAppLanguage(Locale.SIMPLIFIED_CHINESE);
                Toast.makeText(settingActivity.this, "语言已转换为中文", Toast.LENGTH_LONG).show();
            }
        });

        final ImageButton actionB = (ImageButton) findViewById(R.id.English);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAppLanguage(Locale.US);
                Toast.makeText(settingActivity.this, "Language has been changes to English", Toast.LENGTH_LONG).show();
            }
        });
}

    public void changeAppLanguage(Locale locale) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        getResources().updateConfiguration(configuration, metrics);
        // Restart the activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

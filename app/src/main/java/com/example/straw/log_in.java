package com.example.straw;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class log_in extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView text = findViewById(R.id.forget);
        final SpannableStringBuilder style = new SpannableStringBuilder();

        //设置文字
//        style.append("关于本活动更多规则，请点我查看");

        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Intent i = new Intent(log_in.this, );

            }
        };
        style.setSpan(clickableSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(style);

    }
}

package com.xx.xxswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XXSwitch xxSwitch = (XXSwitch) findViewById(R.id.switchButton);
        xxSwitch.setOnSwitchStateUpdateListener(new XXSwitch.OnSwitchStateUpdateListener() {
            @Override
            public void onSwitchUpdate(boolean switchState) {
                if (switchState) {
                    //Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

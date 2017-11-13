package com.example.dengjx.openglvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dengjx.openglvideo.egl.EGLBackEnvActivity;
import com.example.dengjx.openglvideo.fbo.FBOActivity;

public class MainActivity extends AppCompatActivity {
    Button mBtnOne,mBtnTwo,mBtnThree,mBtnFour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnOne = (Button) findViewById(R.id.btn_one);
        mBtnTwo = (Button) findViewById(R.id.btn_two);
        mBtnThree = (Button) findViewById(R.id.btn_three);
        mBtnFour = (Button) findViewById(R.id.btn_four);

        mBtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ImageOpenGlActivity.class));
            }
        });
        mBtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MeadiaActivity.class));
            }
        });
        mBtnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EGLBackEnvActivity.class));
            }
        });
        mBtnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FBOActivity.class));
            }
        });
    }
}

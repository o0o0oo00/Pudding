package com.zcy.pudding.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.zcy.pudding.Choco;
import com.zcy.pudding.Pudding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author: zhaochunyu
 * @description: ${DESP}
 * @date: 2019-07-01
 */
public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View view){
        Pudding.create(this, new Function1<Choco, Unit>() {
            @Override
            public Unit invoke(Choco choco) {
                choco.setText("Java Call");
                return null;
            }
        }).show();
    }
}

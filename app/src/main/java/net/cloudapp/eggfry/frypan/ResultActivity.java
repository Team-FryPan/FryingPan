package net.cloudapp.eggfry.frypan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ResultActivity extends AppCompatActivity {

    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
    }

    public void onTouch(View view) {
        System.out.println(count++);
    }
}

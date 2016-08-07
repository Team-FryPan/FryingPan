package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPlayBtnClicked(View v) {
        Intent it = new Intent(this, MultiPlayActivity.class);

        startActivity(it);
    }

    public void onHowToBtnClicked(View v) {
        Intent it = new Intent(this, HowToActivity.class);

        startActivity(it);
    }

    public void onDevInfoBtnClicked(View v) {
        Intent it = new Intent(this, DevInfoActivity.class);

        startActivity(it);
    }

    public void onExitBtnClicked(View v) {
        finish();
    }
}

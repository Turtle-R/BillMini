package com.example.administrator.billmini;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Administrator on 2020/6/8 0008.
 */

public class AboutActivity extends Activity {

    private Button btn_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Button btn_1 = (Button)findViewById(R.id.button);
        btn_1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AboutActivity.this.finish();
            }
        });

    }




}

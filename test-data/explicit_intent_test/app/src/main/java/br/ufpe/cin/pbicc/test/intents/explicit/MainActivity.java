package br.ufpe.cin.pbicc.test.intents.explicit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        //Button b3 = (Button) findViewById(R.id.button3);
        //Button b4 = (Button) findViewById(R.id.button4);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                String pkg = Strings.PACKAGE_NAME;
                i.setClassName(pkg,Strings.CLASS_NAME);
                startActivity(i);
            }
        });


    }
}

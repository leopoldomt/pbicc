package br.ufpe.cin.androidintenttests;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Activity2 extends AppCompatActivity
{
    protected void func1()
    {

        Intent i = new Intent();
        i.setClass(this, Activity1.class);
        i.setData(Uri.parse("custom://base#specific"));
        startActivity(i);

        Intent i2 = new Intent();

        i2.setClass(this, Activity1.class);
        i2.setData(Uri.parse("custom://base#specific"));

        i2.putExtra("key_bool", false);
        i2.putExtra("key_string", "somevalue");
        i2.putExtra("key_int", 10);
        i2.putExtra("key_double", 12.5);

        startActivity(i2);
    }

    public void func2()
    {
        Intent i1 = new Intent(Intent.ACTION_VIEW);
        startActivity(i1);

        Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(i2);

        Intent i3 = new Intent(this, Activity1.class);
        startActivity(i3);

        Intent i4 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"), this, Activity1.class);
        startActivity(i4);

    }

    public void func3()
    {
        Intent i = new Intent();

        i.setPackage("br.ufpe.cin.androidintenttests");
        i.setClassName(this, "Activity1");
        i.setData(Uri.parse("https://www.google.com"));

        startActivity(i);

        Intent i2 = new Intent();

        i2.setClassName("br.ufpe.cin.androidintenttests", "Activity1");
        i2.setData(Uri.parse("https://www.google.com"));

        startActivity(i2);

        Intent i3 = new Intent();

        i3.setClassName(this, "Activity1");
        i3.setData(Uri.parse("https://www.google.com"));

        startActivity(i3);
    }
}

package br.ufpe.cin.androidintenttests;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

public class Activity1 extends AppCompatActivity
{
    protected void func1()
    {
        // intent declaration
        Intent toActivity2 = new Intent();

        toActivity2.setAction(Intent.ACTION_VIEW);
        toActivity2.setData(Uri.parse("custom://base#specific"));

        startActivity(toActivity2);

        Intent i = new Intent();
        i.setAction(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:123456789"));
        startActivity(i);
    }

    public void func2()
    {
        Intent i = new Intent();

        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.google.com"));

        startActivity(i);

        Intent i2 = new Intent();

        i2.setComponent(new ComponentName("br.ufpe.cin.androidintenttests", "Activity1"));
        i2.setData(Uri.parse("https://www.google.com"));

        startActivity(i2);

        Intent i3 = new Intent();

        i3.setAction(Intent.ACTION_VIEW);
        i3.setDataAndNormalize(Uri.parse("https://www.google.com"));

        startActivity(i3);

        Intent i4 = new Intent();

        i4.setAction(Intent.ACTION_VIEW);
        i4.setDataAndType(Uri.parse("https://www.google.com"), "text/plain");

        startActivity(i4);

        Intent i5 = new Intent();

        i5.setAction(Intent.ACTION_VIEW);
        i5.setDataAndTypeAndNormalize(Uri.parse("https://www.google.com"), "text/plain");

        startActivity(i5);

        Intent i6 = new Intent();

        i6.setComponent(new ComponentName("br.ufpe.cin.androidintenttests", "Activity1"));
        i6.setData(Uri.parse("https://www.google.com"));
        i6.setType("text/plain");

        startActivity(i6);

        //
        i.setData(Uri.parse("https://www.google.com.br"));
    }
}

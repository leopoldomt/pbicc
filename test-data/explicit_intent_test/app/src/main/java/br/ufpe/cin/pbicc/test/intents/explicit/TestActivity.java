package br.ufpe.cin.pbicc.test.intents.explicit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
    
    void test() {
    	startActivity(new Intent(Intent.ACTION_VIEW).putExtra("key","value"));
    }
    
    void testIntentDeclaration() {
    	Intent i;
    	i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("some string"));
        startActivity(i);
    }
    
    void testUriDeclaration() {
    	Intent i;
    	String t = "another string";
    	Uri uri = Uri.parse(t);
    	i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        startActivity(i);
    }
    
    void testComponent() {
    	Intent i = new Intent(that, MainActivity.class);
        i.setData(Uri.parse(this.imgCache.getImages().get(position).filesystemUri()));
        i.putExtra("currentImageIndex", 1);
        i.putExtra("cachedImageList", 2);
        i.putExtra("gallery", 3);
        that.startActivity(i);
    }
    
    void testCreateChooser() {
    	Intent send = new Intent();
    	send.setAction(Intent.ACTION_SENDTO);
    	String uriText = "mailto:george@georgewhiteside.net" + "?subject=Abstract Art";
    	Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send e-mail"));
    }
    

    
    

    

}

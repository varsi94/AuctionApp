package hu.bute.auctionapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) findViewById(R.id.testBtn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject obj = new ParseObject("Fileclass");
                ParseFile f = new ParseFile("valami.txt", new byte[]{1,2,3,4,5,6,7,8,9,0});
                obj.put("enfajlom", f);
                obj.put("nev", "kula");
                f.saveInBackground();
                obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}

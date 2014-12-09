package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import hu.bute.auctionapp.R;

public class UploadActivity extends Activity {

    static final String[] storeNames = new String[] { "Tesco", "Lidl","Aldi", "Auchan Csömör",
    "Árkád", "Aréna Pláza", "Mammut", "WestEnd" , "Campona", "Auchan Dunakeszi", "Auchan Budaörs",
    "Auchan Maglód", "Auchan Óbuda", "Auchan Fót"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        View uploadButton = findViewById(R.id.uploadbtn);
        uploadButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

            }
        });

        AutoCompleteTextView tv = (AutoCompleteTextView)
                findViewById(R.id.store);
        ArrayAdapter<String> storeAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.
                                simple_dropdown_item_1line, storeNames);
        tv.setAdapter(storeAdapter);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

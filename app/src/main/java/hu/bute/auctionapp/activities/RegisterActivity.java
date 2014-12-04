package hu.bute.auctionapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.UserData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class RegisterActivity extends Activity {
    private EditText emailText;
    private EditText password1Text;
    private EditText password2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailText = (EditText) findViewById(R.id.emailText);
        password1Text = (EditText) findViewById(R.id.password1Text);
        password2Text = (EditText) findViewById(R.id.password2Text);
        Button signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
    }

    private boolean check(String email, String pwd1, String pwd2) {
        emailText.setError(null);
        password1Text.setError(null);
        password2Text.setError(null);
        if (email.equals("")) {
            emailText.setError(getString(R.string.error_field_required));
            return false;
        } else if (pwd1.equals("")) {
            password1Text.setError(getString(R.string.error_field_required));
            return false;
        } else if (pwd2.equals("")) {
            password2Text.setError(getString(R.string.error_field_required));
            return false;
        } else if (email.length() <= 7) {
            emailText.setError(getString(R.string.error_invalid_accname));
            return false;
        } else if (!pwd1.equals(pwd2)) {
            password1Text.setError(getString(R.string.differentPwd));
            return false;
        } else if (pwd2.length() <= 4) {
            password1Text.setError(getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }

    private void attemptSignUp() {
        boolean b = check(emailText.getText().toString(),
                password1Text.getText().toString(),
                password2Text.getText().toString());
        if (!b) return;
        ((AuctionApplication) getApplication()).cloud.saveUser(
                emailText.getText().toString(),
                password1Text.getText().toString(),
                new CloudHandler.ResultCallback() {
                    @Override
                    public void onResult(Object result) {
                        if (result == null) {
                            Toast.makeText(RegisterActivity.this, R.string.usernameOccupied, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.successfulSignUp, Toast.LENGTH_LONG).show();
                            ((AuctionApplication) getApplication()).user = (UserData) result;
                            finish();
                        }
                    }
                }
        );
    }
}

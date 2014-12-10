package hu.bute.auctionapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hu.bute.auctionapp.AuctionApplication;
import hu.bute.auctionapp.R;
import hu.bute.auctionapp.data.UserData;
import hu.bute.auctionapp.parsewrapper.CloudHandler;

public class RegisterActivity extends Activity {
    private EditText accountNameText;
    private EditText password1Text;
    private EditText password2Text;
    private View mLoginFormView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountNameText = (EditText) findViewById(R.id.accountNameText);
        password1Text = (EditText) findViewById(R.id.password1Text);
        password2Text = (EditText) findViewById(R.id.password2Text);
        password2Text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });
        View signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        mLoginFormView = findViewById(R.id.signUpForm);
        mProgressView = findViewById(R.id.signupProgress);
    }

    private boolean check(String email, String pwd1, String pwd2) {
        accountNameText.setError(null);
        password1Text.setError(null);
        password2Text.setError(null);
        if (email.equals("")) {
            accountNameText.setError(getString(R.string.error_field_required));
            accountNameText.requestFocus();
            return false;
        } else if (pwd1.equals("")) {
            password1Text.setError(getString(R.string.error_field_required));
            password1Text.requestFocus();
            return false;
        } else if (pwd2.equals("")) {
            password2Text.setError(getString(R.string.error_field_required));
            password2Text.requestFocus();
            return false;
        } else if (email.length() <= 7) {
            accountNameText.setError(getString(R.string.error_invalid_accname));
            accountNameText.requestFocus();
            return false;
        } else if (!pwd1.equals(pwd2)) {
            password1Text.setError(getString(R.string.differentPwd));
            password1Text.requestFocus();
            return false;
        } else if (pwd2.length() <= 4) {
            password1Text.setError(getString(R.string.error_invalid_password));
            password1Text.requestFocus();
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptSignUp() {
        boolean b = check(accountNameText.getText().toString(),
                password1Text.getText().toString(),
                password2Text.getText().toString());
        if (!b) return;
        UserData userData = new UserData(accountNameText.getText().toString(), password1Text.getText().toString());
        showProgress(true);
        ((AuctionApplication) getApplication()).cloud.saveUser(
                userData,
                new CloudHandler.ResultCallback() {
                    @Override
                    public void onResult(Object result) {
                        if (result == null) {
                            showProgress(false);
                            Toast.makeText(RegisterActivity.this, R.string.usernameOccupied, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.successfulSignUp, Toast.LENGTH_LONG).show();
                            ((AuctionApplication) getApplication()).setUser((UserData) result);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
        );
    }
}

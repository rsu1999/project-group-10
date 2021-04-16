package ca.mcgill.ecse321.arms;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import ca.mcgill.ecse321.arms.HttpUtils;
import ca.mcgill.ecse321.arms.R;
import ca.mcgill.ecse321.arms.navigationdrawer.MainActivity;
import ca.mcgill.ecse321.arms.ui.login.LoginViewModel;
import ca.mcgill.ecse321.arms.ui.login.LoginViewModelFactory;
import cz.msebera.android.httpclient.Header;
import android.widget.ArrayAdapter;

public class my_account extends AppCompatActivity {

    private String name;
    private String email;
    private String phoneNum;
    private String password;
    private String error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        name = getCurrentCustomer();

        getInfo();
        refreshErrorMessage();
//        Button btn = (Button)findViewById(R.id.update);
//        //Button btn1 = (Button)findViewById(R.id.Back);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(my_account.this, account_update.class));
//            }
//        });
    }

    public String getCurrentCustomer() {
        return ARMS.getCurrentuser();
    }
    public void getInfo(){
        RequestParams rp = new RequestParams();
        rp.add("username",name);
        HttpUtils.get("/getCustomer", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for( int i = 0; i < response.length(); i++){
                    try {
                        //Log.d(TAG, "Restful GET call succesfull (" + i + ").");
                        JSONObject obj1 = response.getJSONObject(i);
                        email = obj1.getString("email");
                        phoneNum = obj1.getString("phoneNumber");
                    }catch (JSONException e) {
                        //Log.d(TAG, e.getMessage());
                        error += e.getMessage();
                    }}


                refreshErrorMessage();
                TextView tv2 = (TextView) findViewById(R.id.name);
                tv2.setText(name);
                TextView tv3 = (TextView) findViewById(R.id.email);
                tv3.setText(email);
                TextView tv4 = (TextView) findViewById(R.id.phoneNum);
                tv2.setText(phoneNum);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
        }


        public void goUpdate(View v){
            Intent intent = new Intent(my_account.this, account_update.class);
            intent.putExtra("USERNAME", name);
            startActivity(intent);
        }


//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void refreshErrorMessage() {
        // set the error message
        TextView tvError = (TextView) findViewById(R.id.error);
        tvError.setText(error);

        if (error == null || error.length() == 0) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
    }

    public void updateInfo(View v) {
        error = "";
        final TextView tvUsername = (TextView) findViewById(R.id.username);
        String username = tvUsername.getText().toString();
        final TextView tvPassword = (TextView) findViewById(R.id.password);
        String password = tvPassword.getText().toString();
        HttpUtils.put("loginCustomer/?username=" + username + "&password=" + password, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                refreshErrorMessage();
                tvUsername.setText("");
                tvPassword.setText("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });
    }
}
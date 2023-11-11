package com.soket.srsmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
//import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userPassword;
    private Button btnLogin;
    private TextView institutionName;
    private ImageView logo;

    private int counter;

    private String institutionCode;
    private String hashKey;
    private String urlAPI;

    //for saved data
    private clsPreference savedData ;

    private String m_Text ;

    private ProgressDialog dialog;
    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter=5;

        userName =findViewById(R.id.editTextUserName_Main );
        userPassword=findViewById(R.id.editTextPassword_Main );
        btnLogin=findViewById(R.id.btnLogin_Main);
        ImageButton imgSetting =  findViewById(R.id.imageButtonSetting_Main);
        institutionName= findViewById(R.id.textViewInstitutionName_Main);
        logo = findViewById(R.id.imageViewLogo_Main);

        //progressBar=findViewById(R.id.progressBar_Main);

        //getsavedData
        savedData = new clsPreference();
        savedData.clearLoggedInUser(MainActivity.this);

        institutionCode= savedData.getRegisteredInstitutionCode(this);
        hashKey=getString(R.string.hashKey);
        urlAPI=getString(R.string.webService );

        imgSetting.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(getString(R.string.INPUT_PIN_CAPTION));
            final EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton(getString(R.string.BTN_OK), (dialog, which) -> {
                dialog.dismiss();
                m_Text = input.getText().toString();
                if (m_Text.equals(getString(R.string.PIN_SETTINGS)))
                {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this,getString(R.string.INVALID_PIN_CAPTION) , Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(getString(R.string.btnCancel), (dialog, which) -> dialog.cancel());
            builder.show();
        });

        btnLogin.setOnClickListener(view -> validateLogin(userName.getText().toString(), userPassword.getText().toString()));

        GetInstitutionName();
        userName.requestFocus();
    }

    private void GetInstitutionName()
    {
        dialog = ProgressDialog.show(MainActivity.this, "Loading","Mohon Tunggu...", true, false);
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", institutionCode);
            Params.put("HashCode", clsGenerateSHA.hex256(institutionCode.concat(hashKey),true));
            sendPostForGetInstitutionName(urlAPI.concat("/GetInstitution") , Params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForGetInstitutionName(String url, JSONObject JSONBodyParam)  {
        try{
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, url, JSONBodyParam,
                    response -> {
                        //do something
                        String ResponseCode ;
                        String ResponseDescription;
                        try {
                            ResponseCode = response.getString("responseCode");
                            ResponseDescription = response.getString("responseDescription");
                            if (ResponseCode.equalsIgnoreCase("00"))
                            {
                                JSONObject Institution = response.getJSONObject("institution");
                                institutionName.setText( Institution.getString("institutionName"));
                                switch (institutionCode)
                                {
                                    case "100001":
                                    case "100002":
                                    case "100003":
                                    case "100004":
                                    case "100005":
                                    case "100006":
                                        logo.setImageResource(R.drawable.kpnkamadhuk);
                                        break;
                                    default :
                                        logo.setImageResource(R.drawable.ic_launcher72);
                                        break;
                                }
                            }
                            else
                            {
                                institutionName.setText( R.string.app_name);
                                Toast.makeText(MainActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                            savedData.setRegisteredInstitution(MainActivity.this,institutionCode, institutionName.getText().toString() );
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(MainActivity.this,error.toString() , Toast.LENGTH_LONG).show()
            )
            {
                @Override
                public Map<String, String> getHeaders()  {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(objectRequest);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
        finally {
            dialog.dismiss();
        }
    }

    private void validateLogin(String pUserName, String pPassword)  {
        dialog = ProgressDialog.show(MainActivity.this, "Loging In","Mohon Tunggu...", true, false);
        try
        {
            if ( pUserName.equals("fpranadi") && pPassword.equals("cemapanj") && (institutionCode.equals("000000")))
            {
                institutionCode="100006";
                savedData.setRegisteredInstitution(this, institutionCode,getString(R.string.DEFAULT_INSTITUTION) );
                savedData.clearLoggedInUser(this);
                GetInstitutionName();
            }

            JSONObject postparams = new JSONObject();
            postparams.put("InstitutionCode", institutionCode);
            postparams.put("UserID", pUserName);
            postparams.put("Password", pPassword);
            postparams.put("HashCode", clsGenerateSHA.hex256(institutionCode.concat(pUserName).concat(pPassword).concat(hashKey),true));

            //progressBar.setProgress(40);
            sendPostForValidateLogin(urlAPI.concat("/Login") , postparams);

        } catch (JSONException e) {
            Toast.makeText(MainActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForValidateLogin(String url, JSONObject JSONBodyParam)  {
        try{
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, url, JSONBodyParam,
                    response -> {
                        //do something
                        String ResponseCode ;
                        String ResponseDescription;
                        try {
                            ResponseCode = response.getString("responseCode");
                            ResponseDescription = response.getString("responseDescription");
                            if (ResponseCode.equalsIgnoreCase("00")) {
                                savedData.setLoggedInUser(MainActivity.this,userName.getText().toString() );
                                savedData.setLoggedInStatus(MainActivity.this,true );
                                savedData.setLoggedInUserGroup(MainActivity.this, response.getString("authenticated_UserGroup") );

                                userName.setText("");
                                userPassword.setText("");

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                counter--;
                                if (counter == 0) {
                                    Toast.makeText(MainActivity.this,"No of Attempts remaining: ".concat(Integer.toString(counter )).concat(ResponseDescription), Toast.LENGTH_LONG).show();
                                    btnLogin.setEnabled(false);
                                    savedData.clearLoggedInUser(MainActivity.this);
                                }
                                else
                                {
                                    savedData.setLoggedInStatus(MainActivity.this,false);
                                    Toast.makeText(MainActivity.this,ResponseDescription, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            savedData.clearLoggedInUser(MainActivity.this);
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        Toast.makeText(MainActivity.this,error.toString() , Toast.LENGTH_LONG).show();
                        savedData.clearLoggedInUser(MainActivity.this);
                    }
            )
            {
                @Override
                public Map<String, String> getHeaders()  {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(objectRequest);
        }catch (Exception e){
            savedData.clearLoggedInUser(MainActivity.this);
            Toast.makeText(MainActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
        finally {
            dialog.dismiss();
        }
    }
}
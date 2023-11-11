package com.soket.srsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingActivity extends AppCompatActivity {

    private EditText e_intitutionCode;

    //for saved data
    private clsPreference savedData ;

    private String hashKey;
    private String urlAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button btnSimpan =  findViewById(R.id.btnSimpan_Setting);
        Button btnCancel =  findViewById(R.id.btnCancel_Setting);
        e_intitutionCode=findViewById(R.id.editTextInstitutionCode_Setting);

        //getsavedData
        savedData = new clsPreference();
        e_intitutionCode.setText(savedData.getRegisteredInstitutionCode(this)) ;
        hashKey=getString(R.string.hashKey);
        urlAPI=getString(R.string.webService );


        btnSimpan.setOnClickListener(view -> SetRegisteredInstitution());

        btnCancel.setOnClickListener(v -> {
            savedData.clearLoggedInUser(SettingActivity.this );
            finish();
        });

    }

    private void SetRegisteredInstitution()
    {
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", e_intitutionCode.getText().toString());
            Params.put("HashCode", clsGenerateSHA.hex256(e_intitutionCode.getText().toString().concat(hashKey),true));
            sendPostForGetInstitutionName(urlAPI.concat("/GetInstitution") , Params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                savedData.setRegisteredInstitution(SettingActivity.this,e_intitutionCode.getText().toString(), Institution.getString("institutionName")) ;
                                savedData.clearLoggedInUser(SettingActivity.this );
                                Toast.makeText(SettingActivity.this, "Insitusi :".concat(Institution.getString("institutionName")).concat("Konfigurasi berhasil Tersimpan, buka kembali Aplikasi ini ...!!!") , Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SettingActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(SettingActivity.this,error.toString() , Toast.LENGTH_LONG).show()
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
            Toast.makeText(SettingActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
    }
}
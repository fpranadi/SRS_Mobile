package com.soket.srsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
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

public class HomeActivity extends AppCompatActivity {

    private clsPreference currPreference;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //assigmn control
        ImageView btnKeluar =  findViewById(R.id.ImageView_Keluar_Home);
        ImageView btnSale =  findViewById(R.id.ImageView_POS_Sale_Home);
        ImageView btnRefund =  findViewById(R.id.ImageView_POS_Refund_Home);
        ImageView btnInputOpname =  findViewById(R.id.ImageView_Input_Opname_Home);
        ImageView btnRekapOpname =  findViewById(R.id.ImageView_Rekap_Hasil_Opname_Home);
        ImageView btnDaftarProduk =  findViewById(R.id.ImageView_Daftar_Produk_Home);
        TextView txtinstitutionName =  findViewById(R.id.txtInstitutionName_Home);

        //cek apa keaddan login apa tidak
        currPreference = new clsPreference();
        boolean currLoggedInStatus = currPreference.getLoggedInStatus(this);

        if (currLoggedInStatus)
        {
            txtinstitutionName.setText(currPreference.getRegisteredInstitutionName(this) );
        }
        else
        {
            currPreference.clearLoggedInUser(this);
            finish();
        }

        btnKeluar.setOnClickListener(view -> {
            currPreference.clearLoggedInUser(HomeActivity.this );
            finish();
        });

        btnSale.setOnClickListener(view -> {
            Toast.makeText(HomeActivity.this, R.string.AVAILABLE_SOON, Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(HomeActivity.this, SaleActivity.class);
            //startActivity(intent);
        });

        btnRefund.setOnClickListener(view -> {
            Toast.makeText(HomeActivity.this, R.string.AVAILABLE_SOON, Toast.LENGTH_LONG).show();

            //Intent intent = new Intent(HomeActivity.this, RefundActivity.class);
            //startActivity(intent);
        });

        btnRekapOpname.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, OpnameReportActivity.class);
            startActivity(intent);
        });

        btnDaftarProduk.setOnClickListener(view -> {
            Toast.makeText(HomeActivity.this, R.string.AVAILABLE_SOON, Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            //startActivity(intent);
        });

        btnInputOpname.setOnClickListener(view -> {
            currPreference.clearRegisteredOpname(this);
            IsOpnameActivated();
        });
    }

    private void IsOpnameActivated()
    {
        dialog = ProgressDialog.show(HomeActivity.this, "Inquiring","Mohon Tunggu...", true, false);
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", currPreference.getRegisteredInstitutionCode(this));
            Params.put("HashCode", clsGenerateSHA.hex256(currPreference.getRegisteredInstitutionCode(this).concat(getString(R.string.hashKey)),true));
            sendPostForGetStatusOpname( getString(R.string.webService).concat("/StatusOpname") , Params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForGetStatusOpname(String url, JSONObject JSONBodyParam)  {
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
                                currPreference.setRegisteredOpnameAktif(this, response.getString("kodeOpnameAktif"), response.getString("lokasi"));
                                Intent intent = new Intent(HomeActivity.this, InputOpnameActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                currPreference.clearRegisteredOpname(this);
                                Toast.makeText(HomeActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(HomeActivity.this,error.toString() , Toast.LENGTH_LONG).show()
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
            Toast.makeText(HomeActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
        finally {
            dialog.dismiss();
        }
    }
}
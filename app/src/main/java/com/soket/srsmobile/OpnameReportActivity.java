package com.soket.srsmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpnameReportActivity extends AppCompatActivity {

    private clsPreference currPreference;

    //create adapter
    private AdapterOpnameReport adOpnameReport;
    private ArrayList<clsOpnameReportDetail> arrOpnameReport;
    private clsOpnameReportDetail opnameReportDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opname_report);

        TextView institutionName = findViewById(R.id.textViewInstitutionName_OpnameReport);
        ImageButton btnKembali =  findViewById(R.id.imageButtonKembali_OpnameReport);
        ImageButton btnShare = findViewById(R.id.imageButtonSave_OpnameReport);
        RecyclerView opnameDetail = findViewById(R.id.RC_Detail_OpnameReport);

        currPreference = new clsPreference();
        boolean currLoggedInStatus = currPreference.getLoggedInStatus(this);
        if (currLoggedInStatus)
        {
            institutionName.setText(currPreference.getRegisteredInstitutionName(this) );

            //set adapter dan cardview nya dulu
            arrOpnameReport= new ArrayList<>();
            adOpnameReport = new AdapterOpnameReport(arrOpnameReport);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OpnameReportActivity.this);
            opnameDetail.setLayoutManager(layoutManager);
            opnameDetail.setAdapter(adOpnameReport);

            bindDataOpnameReport();
        }
        else
        {
            currPreference.clearLoggedInUser(this);
            finish();
        }

        btnKembali.setOnClickListener(view -> finish());

        btnShare.setOnClickListener(view -> Toast.makeText(OpnameReportActivity.this, "Share", Toast.LENGTH_LONG).show());
    }

    private void bindDataOpnameReport()
    {
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", currPreference.getRegisteredInstitutionCode(this));
            Params.put("KodeOpname", currPreference.getRegisteredKodeOpnameAktif(this) );
            Params.put("NoBlanko", currPreference.getLoggedInUser(this) );
            Params.put("hashCode", clsGenerateSHA.hex256(currPreference.getRegisteredInstitutionCode(this).concat(currPreference.getRegisteredKodeOpnameAktif(this)).concat(currPreference.getLoggedInUser(this)).concat(getString(R.string.hashKey)),true));
            sendPostForBindDataOpnameReport(getString(R.string.webService).concat("/OpnameReport") , Params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(OpnameReportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForBindDataOpnameReport(String url, JSONObject JSONBodyParam)  {
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
                                // Getting JSON Array node
                                JSONArray Mutations = response.getJSONArray("hasil");
                                JSONObject Mutation;
                                for (int k = 0; k < Mutations.length(); k++)
                                {
                                    Mutation = Mutations.getJSONObject(k);
                                    opnameReportDetail = new clsOpnameReportDetail();
                                    opnameReportDetail.SetNamaBarang( Mutation.getString("namaProduk"));
                                    opnameReportDetail.SetKodeProduk( Mutation.getString("sku"));
                                    opnameReportDetail.SetOpnameToko(formatedAmount(Mutation.getString("opnameToko")));
                                    opnameReportDetail.SetOpnameGudang(formatedAmount(Mutation.getString("opnameGudang")));
                                    arrOpnameReport.add(opnameReportDetail);

                                }
                                adOpnameReport.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(OpnameReportActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OpnameReportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(OpnameReportActivity.this,error.toString() , Toast.LENGTH_LONG).show()
            )
            {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(objectRequest);
        }catch (Exception e){
            Toast.makeText(OpnameReportActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
    }

    private String formatedAmount(String amount)
    {
        //Locale localeID = new Locale("in", "ID");
        if (amount.length()>0)
        {
            NumberFormat formatRupiah = NumberFormat.getInstance();
            return formatRupiah.format( Double.parseDouble(amount));
        }
        else
        {
            return amount;
        }

    }
}
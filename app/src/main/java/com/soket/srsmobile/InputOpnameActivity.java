package com.soket.srsmobile;

import androidx.activity.result.ActivityResultLauncher;
//import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class InputOpnameActivity extends AppCompatActivity {

    private clsPreference currPreference;
    private String lokasi;
    private Boolean isEdit;

    private EditText edOpnameToko;
    private Double opnameToko;
    private EditText edOpnameGudang;
    private Double opnameGudang;

    private EditText sKUInputed;
    private TextView sKU;
    private TextView kodeOpname ;
    private TextView namaProduk ;
    private TextView harga ;

    private ProgressDialog dialog;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("InputOpnameActivity", "Cancelled scan");
                        Toast.makeText(InputOpnameActivity.this, "Cancelled scan", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("InputOpnameActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(InputOpnameActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("InputOpnameActivity", "Scanned");
                    sKUInputed.setText(result.getContents());
                    GetProductInfoForOpname();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_opname);

        TextView institutionName = findViewById(R.id.textViewInstitutionName_InputOpname);
        ImageButton btnScan = findViewById(R.id.imageButton_Scan_InputOpname);
        ImageButton btnScan1 = findViewById(R.id.imageButtonScanQR_InputOpname);
        sKUInputed = findViewById(R.id.editText_SKU_InputOpname);
        ImageButton btnLanjut = findViewById(R.id.imageButton_SKU_InputOpname);
        ImageButton btnKembali =  findViewById(R.id.imageButtonKembali_InputOpname);
        ImageButton btnSimpan = findViewById(R.id.imageButtonSave_InputOpname);
        sKU = findViewById(R.id.textView_SKU_InputOpname);
        kodeOpname = findViewById(R.id.textView_KodeOpname_InputOpname);
        namaProduk = findViewById(R.id.textView_Produk_InputOpname);
        harga = findViewById(R.id.textView_Harga_InputOpname);
        edOpnameToko = findViewById(R.id.editText_OpnameToko_InputOpname);
        edOpnameGudang =findViewById(R.id.editText_OpnameGudang_InputOpname);

        currPreference = new clsPreference();
        boolean currLoggedInStatus = currPreference.getLoggedInStatus(this);

        if (currLoggedInStatus)
        {
            institutionName.setText(currPreference.getRegisteredInstitutionName(this) );
        }
        else
        {
            currPreference.clearLoggedInUser(this);
            finish();
        }

        btnKembali.setOnClickListener(view -> {
            finish();
        });

        btnSimpan.setOnClickListener(view -> {
            try
            {
                if (edOpnameGudang.getText().toString().length()==0)
                {
                    opnameGudang = 0.0;
                }
                else
                {
                    opnameGudang = Double.parseDouble(edOpnameGudang.getText().toString());
                }
                if (edOpnameToko.getText().toString().length()==0)
                {
                    opnameToko = 0.0;
                }
                else
                {
                    opnameToko = Double.parseDouble(edOpnameToko.getText().toString());
                }

                if ((sKUInputed.length()>0) && (kodeOpname.length()>0))
                {
                    SaveOpname();
                }
                else
                {
                    Toast.makeText(InputOpnameActivity.this, getString(R.string.INVALID_INPUT_OPNAME), Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception err)
            {
                Toast.makeText(InputOpnameActivity.this, err.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        btnLanjut.setOnClickListener(view -> {
            if (sKUInputed.length()>0)
            {
                GetProductInfoForOpname();
            }
            else
            {
                Toast.makeText(InputOpnameActivity.this, getString(R.string.PRODUK_TIDAK_ADA), Toast.LENGTH_LONG).show();
            }
        });

        btnScan.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setCaptureActivity(Capture.class);
            //options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
            options.setPrompt("Scan QR/ Bar Code Produk !");
            options.setOrientationLocked(true);
            options.setBeepEnabled(true);
            barcodeLauncher.launch(options);
            //barcodeLauncher.launch(new ScanOptions());
        });

        btnScan1.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setCaptureActivity(Capture.class);
            //options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
            options.setPrompt("Scan QR/ Bar Code Produk !");
            options.setOrientationLocked(true);
            options.setBeepEnabled(true);
            barcodeLauncher.launch(options);
            //barcodeLauncher.launch(new ScanOptions());
        });
    }

    private void GetProductInfoForOpname()
    {
        dialog = ProgressDialog.show(InputOpnameActivity.this, "Loading","Mohon Tunggu...", true, false );
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", currPreference.getRegisteredInstitutionCode(this));
            Params.put("SKU", sKUInputed.getText().toString());
            Params.put("NoBlanko", currPreference.getLoggedInUser(this));
            Params.put("HashCode", clsGenerateSHA.hex256(currPreference.getRegisteredInstitutionCode(this).concat(sKUInputed.getText().toString()).concat(currPreference.getLoggedInUser(this)).concat(getString(R.string.hashKey)),true));
            sendPostForGetProductInfoForOpname( getString(R.string.webService).concat("/GetProdukInfoFromOpname") , Params);
        }
        catch (JSONException e)
        {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(InputOpnameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForGetProductInfoForOpname(String url, JSONObject JSONBodyParam)  {
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
                                //confim dulu kalau sudah pernah diinput sebeumnya
                                if (!ResponseDescription.equalsIgnoreCase("SUKSES"))
                                {
                                    new AlertDialog.Builder(InputOpnameActivity.this)
                                            .setTitle(R.string.INPUT_OPNAME)
                                            .setMessage(ResponseDescription)
                                            .setCancelable(false)
                                            .setPositiveButton("OK", (dialog, which) -> {
                                            })
                                            .show();
                                }

                                sKU.setText(response.getString("sku"));
                                namaProduk.setText(response.getString("namaProduk"));
                                harga.setText(formatedAmount(response.getDouble("hargaJual")));
                                kodeOpname.setText(response.getString("kodeOpname").trim());

                                opnameToko=response.getDouble("opnameToko");
                                edOpnameToko.setText( formatedAmount(opnameToko));
                                if (opnameToko==0.0)
                                {
                                    edOpnameToko.setText("");
                                }

                                opnameGudang=response.getDouble("opnameGudang");
                                edOpnameGudang.setText(formatedAmount(opnameGudang));
                                if (opnameGudang==0.0)
                                {
                                    edOpnameGudang.setText("");
                                }

                                lokasi =response.getString("lokasi").trim();
                                isEdit  = response.getBoolean("isEdit");

                                switch (lokasi.toLowerCase().trim())
                                {
                                    case "toko":
                                        edOpnameGudang.setEnabled(false);
                                        edOpnameGudang.setFocusable(false);
                                        edOpnameToko.requestFocus();
                                        break;
                                    case "gudang":
                                        edOpnameToko.setEnabled(false);
                                        edOpnameToko.setFocusable(false);
                                        edOpnameGudang.requestFocus();
                                        break;
                                    default:
                                        edOpnameGudang.setEnabled(true);
                                        edOpnameGudang.setFocusable(true);
                                        edOpnameToko.setEnabled(true);
                                        edOpnameToko.setFocusable(true);
                                        edOpnameToko.requestFocus();
                                        break;
                                }


                            }
                            else
                            {
                                Toast.makeText(InputOpnameActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InputOpnameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(InputOpnameActivity.this,error.toString() , Toast.LENGTH_LONG).show()
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
            Toast.makeText(InputOpnameActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
        finally {
            dialog.dismiss();
        }
    }

    private void SaveOpname()
    {
        dialog = ProgressDialog.show(InputOpnameActivity.this, "Saving","Mohon Tunggu...", true,false);
        try {
            JSONObject Params = new JSONObject();
            Params.put("InstitutionCode", currPreference.getRegisteredInstitutionCode(this));
            Params.put("NoBlanko", currPreference.getLoggedInUser(this));
            Params.put("KodeOpname", kodeOpname.getText().toString());
            Params.put("Lokasi", lokasi);
            Params.put("SKU", sKUInputed.getText().toString());
            Params.put("OpnameToko", opnameToko.toString());
            Params.put("OpnameGudang", opnameGudang.toString());
            Params.put("Operator", currPreference.getLoggedInUser(this));
            Params.put("IsEdit", isEdit);

            String tmpHashCode = currPreference.getRegisteredInstitutionCode(this).concat(currPreference.getLoggedInUser(this)).concat(kodeOpname.getText().toString());
            tmpHashCode= tmpHashCode.concat(lokasi).concat(sKUInputed.getText().toString());
            tmpHashCode=tmpHashCode.concat(opnameToko.toString()).concat(opnameGudang.toString());
            tmpHashCode=tmpHashCode.concat(currPreference.getLoggedInUser(this));
            tmpHashCode=tmpHashCode.concat(getString(R.string.hashKey));
            Params.put("HashCode", clsGenerateSHA.hex256(tmpHashCode,true));

            sendPostForSaveOpname( getString(R.string.webService).concat("/InputOpname") , Params);
        }
        catch (JSONException e)
        {
            dialog.dismiss();
            e.printStackTrace();
            Toast.makeText(InputOpnameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPostForSaveOpname(String url, JSONObject JSONBodyParam)  {
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
                                new AlertDialog.Builder(InputOpnameActivity.this)
                                        .setTitle(R.string.INPUT_OPNAME)
                                        .setMessage("Hasil opname SKU: ".concat(sKUInputed.getText().toString()).concat(" berhasil disimpan ..."))
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            clearFormInputOpname();
                                            sKUInputed.requestFocus();
                                        })
                                        .show();
                            }
                            else
                            {
                                Toast.makeText(InputOpnameActivity.this,"Error : ".concat(ResponseDescription), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(InputOpnameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(InputOpnameActivity.this,error.toString() , Toast.LENGTH_LONG).show()
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
            Toast.makeText(InputOpnameActivity.this,e.toString() , Toast.LENGTH_LONG).show();
        }
        finally {
            dialog.dismiss();
        }
    }

    private void clearFormInputOpname()
    {
        sKUInputed.setText("");
        sKU.setText("");
        namaProduk.setText("");
        harga.setText("");
        kodeOpname.setText("");
        edOpnameToko.setText("");
        edOpnameGudang.setText("");
        opnameToko=0.0;
        opnameGudang=0.0;
        lokasi ="";
        isEdit  = false;
        sKUInputed.requestFocus();
    }

    private String formatedAmount(Double amount)
    {
        NumberFormat formatRupiah = NumberFormat.getInstance();
        return formatRupiah.format( amount);
    }
}
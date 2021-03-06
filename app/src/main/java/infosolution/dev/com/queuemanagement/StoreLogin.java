package infosolution.dev.com.queuemanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoreLogin extends AppCompatActivity {

    private Button btnsignin;
    private TextView tvstorelogin;
    private String StoreId, Password;
    private EditText etstoreid, etpassword;
    private ProgressDialog pdLoading;
    private SharedPreferences sh_Prefff;
    private SharedPreferences.Editor editorrr;
    private static final String IS_LOGINN = "IsLoggedIn";
    private int PRIVATE_MODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_login);

        tvstorelogin = findViewById(R.id.tv_storelogin);
        etstoreid = findViewById(R.id.et_storecode);
        etpassword = findViewById(R.id.et_storepassword);

        sh_Prefff = getSharedPreferences("LoginStore", PRIVATE_MODE);
        boolean check = sh_Prefff.getBoolean(IS_LOGINN, false);
        if (check) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/AUDIOWIDE-REGULAR.TTF");
        tvstorelogin.setTypeface(typeface);
        btnsignin = findViewById(R.id.btn_signinstore);
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreId = etstoreid.getText().toString();
                Password = etpassword.getText().toString();

                if (StoreId.length() <= 0) {
                    etstoreid.setError("Please Enter Staff ID");
                } else if (Password.length() <= 0) {
                    etpassword.setError("Please enter Password");
                } else {

                    Login();
                }


            }
        });
    }

    private void Login() {


        pdLoading = new ProgressDialog(StoreLogin.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        String URL = "http://www.devhitech.com/salon-ms/api/store_login?store_code=";
        String URL2 = URL + StoreId + "&pin=";
        String Url3 = URL2 + Password;

        Log.i("urllstore", Url3);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("pppppppppp", response);
                        pdLoading.dismiss();
                        // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        try {

                            JSONObject jsono = new JSONObject(response);
                            JSONObject jsonObject = jsono.getJSONObject("data");
                            String Status = jsonObject.getString("status");

                            if (Status.equals("Success")) {

                                sharedPreferencess();
                                JSONArray jarray = jsonObject.getJSONArray("0");
                                Log.i("jarray", jarray.toString());

                                for (int i = 0; i < jarray.length(); i++) {

                                    JSONObject object = jarray.getJSONObject(i);
//                                    String Staffcode = object.getString("staff_code");
                                    String StoreId = object.getString("store_id");
//                                    String StaffName = object.getString("name");

                                    Log.i("store", "ID" + StoreId);

//                                    Log.i("Data", "code" + Staffcode + "ID" + StoreId + "NAme" + StaffName);


                                    SharedPreferences sharedPreferencesl = getApplicationContext().getSharedPreferences("l", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorl = sharedPreferencesl.edit();
                                    editorl.putString("store", StoreId);
                                    editorl.commit();

                                    Intent intent = new Intent(StoreLogin.this, MainActivity.class);
                                    //    intent.putExtra("staffcode", Staffcode);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(StoreLogin.this, "Please Check Your Login credential", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //     Toast.makeText(LoginActivity.this, ""+error.toString(), Toast.LENGTH_LONG).show();
                        pdLoading.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
               /* params.put("staff_code",StaffId);
                params.put("password",Password);
                Log.i("params",""+params);*/
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                80000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(StoreLogin.this);
        requestQueue.add(stringRequest);


    }


    public void sharedPreferencess() {

        sh_Prefff = getSharedPreferences("LoginStore", PRIVATE_MODE);
        editorrr = sh_Prefff.edit();
        editorrr.putBoolean(IS_LOGINN, true);
        editorrr.putString("Username", "email");
        editorrr.putString("Password", "password");
        editorrr.commit();
    }
}

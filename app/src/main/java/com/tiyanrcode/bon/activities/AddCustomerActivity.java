package com.tiyanrcode.bon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tiyanrcode.bon.dao.CustomerDAO;
import com.tiyanrcode.bon.model.Customer;
import com.tiyanrcode.bon.R;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class AddCustomerActivity extends ActionBarActivity implements View.OnClickListener{

    public static final String TAG = "AddCustomerActivity";

    private EditText txtName;
    private EditText txtAddress;
    private Button  btnAdd;

    private String name;
    private String alamat;
    private int c;
    private String customerId;

    private CustomerDAO mCustomerDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        getSupportActionBar().hide();

        mCustomerDAO = new CustomerDAO(this);
        // initialize views
        initViews();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey("c")){
            name = bundle.getString("name");
            alamat = bundle.getString("alamat");
            customerId = bundle.getString("id");
            c = bundle.getInt("c");
        }

        txtName.setText(name);
        txtAddress.setText(alamat);

    }

    private void initViews() {
        txtAddress = (EditText) findViewById(R.id.txt_address);
        txtName = (EditText) findViewById(R.id.txt_name);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Editable customerName = txtName.getText();
                Editable customerAddress = txtAddress.getText();
                if (!TextUtils.isEmpty(customerName) && !TextUtils.isEmpty(customerAddress)) {
                    if (c == 1) {
                        mCustomerDAO.updateCustomer(Long.parseLong(customerId), customerName.toString(), customerAddress.toString());
                        Intent intent = new Intent(AddCustomerActivity.this, ListCustomerActivity.class);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                        finish();
                        c= 0;
                    } else {
                        Customer createdCustumer = mCustomerDAO.createCustomer(customerName.toString(), customerAddress.toString());
                        Intent intent = new Intent();
                        intent.putExtra(ListCustomerActivity.EXTRA_ADDED_CUSTOMER, createdCustumer);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(this, "sukses", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                else {
                    Toast.makeText(this, "Data pelanggan kosong", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomerDAO.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddCustomerActivity.this, ListCustomerActivity.class);
        startActivity(intent);
        finish();
    }
}

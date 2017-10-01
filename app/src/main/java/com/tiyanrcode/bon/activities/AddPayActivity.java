package com.tiyanrcode.bon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tiyanrcode.bon.dao.TransactionDAO;
import com.tiyanrcode.bon.function.Money;
import com.tiyanrcode.bon.model.Transaction;
import com.tiyanrcode.bon.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class AddPayActivity extends ActionBarActivity implements View.OnClickListener{
    public static final String EXTRA_SELECTED_CUSTOMER_ID = "extra_key_selected_customer_id";

    private Button btnPay;
    private EditText dateOfPay;
    private EditText mPay;
    private TextView payName;
    private TextView payId;
    private int year;
    private int month;
    private int day;
    private int credit1;
    private int pay1;
    private int saldo1;
    private String customerId;
    private String customerName;

    final Calendar c = Calendar.getInstance();
    private List<Transaction> mListTransactions;
    private TransactionDAO mTransactionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pay);
        getSupportActionBar().hide();

        //get id and name customer
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey("name")) {
            customerName= bundle.getString("name");
            customerId = bundle.getString("id");
            Log.d("name", customerName);
        }

        initView();

        //set date day
        setDate();

        // fill the listView
        mTransactionDAO = new TransactionDAO(this);
        mListTransactions = mTransactionDAO.getTransactionOfTransaction(Long.parseLong(customerId));
        if(mListTransactions != null && !mListTransactions.isEmpty()) {
            Transaction tran = mTransactionDAO.getItemSaldoByPosition(Long.parseLong(customerId));
            saldo1 = tran.getSaldo();
            Log.d("saldo ", String.valueOf(saldo1));
            credit1 = 0;
            if (saldo1 == 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(AddPayActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle("Pesan Pemberitahuan!");
                // Setting Dialog Message
                alertDialog.setMessage("Pelanggan ini belum punya hutang");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.info20);
                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        // Write your code here to execute after dialog closed
                        finish();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(AddPayActivity.this).create();
            // Setting Dialog Title
            alertDialog.setTitle("Pesan Pemberitahuan!");
            // Setting Dialog Message
            alertDialog.setMessage("Pelanggan ini belum ada kegiatan transaksi");
            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.info20);
            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which)
                {
                    // Write your code here to execute after dialog closed
                    finish();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }

        mPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                /***
                 * No need to continue the function if there is nothing to
                 * format
                 ***/
                if (s.length() == 0){
                    return;
                }

                /*** Now the number of digits in price is limited to 8 ***/
                String value = s.toString().replaceAll(",", "");
                if (value.length() > 9) {
                    value = value.substring(0, 9);
                }
                String formattedPrice = getFormatedCurrency(value);
                if (!(formattedPrice.equalsIgnoreCase(s.toString()))) {
                    /***
                     * The below given line will call the function recursively
                     * and will ends at this if block condition
                     ***/
                    //mPay.setText(formattedPrice);
                    mPay.setSelection(mPay.length());
                }
            }
        });
    }

    private void setDate() {
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        dateOfPay.setText(new StringBuilder().append(day).append("/").append(month).append("/")
                .append(year).append(" "));
    }


    private void initView() {
        dateOfPay = (EditText) findViewById(R.id.date_pay);
        mPay = (EditText) findViewById(R.id.txt_pay);
        payName = (TextView) findViewById(R.id.pay_name);
        payId = (TextView) findViewById(R.id.pay_id);
        btnPay = (Button) findViewById(R.id.btn_pay);

        payName.setText(customerName);
        payId.setText(customerId);
        btnPay.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTransactionDAO.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay:
                    if (!mPay.getText().toString().isEmpty()) {
                        String mcre = mPay.getText().toString().replaceAll(",", "");
                        pay1 = Integer.parseInt(mcre);
                        Log.d("credit", mcre);
                        final int total = pay1 - saldo1;
                            Money m = new Money();
                        final Editable date = dateOfPay.getText();
                        if (total >= 1) {
                            AlertDialog alertDialog = new AlertDialog.Builder(AddPayActivity.this).create();
                            // Setting Dialog Title
                            alertDialog.setTitle("Pesan Pemberitahuan!");
                            // Setting Dialog Message
                            alertDialog.setMessage("Uang kembali pelanggan '" + customerName + "' adalah Rp. " + m.money(total) +" dan hutang LUNAS!");
                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.drawable.info20);
                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    if (!TextUtils.isEmpty("" + credit1) && !TextUtils.isEmpty(date)) {
                                        Transaction createdTransaction = mTransactionDAO.createTransaction(date.toString(), credit1, saldo1 , 0, Long.parseLong(customerId));
                                        finish();
                                    }
                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();
                        } else if (total <= -1) {
                            final int total2 = saldo1 - pay1;
                            AlertDialog alertDialog = new AlertDialog.Builder(AddPayActivity.this).create();
                            // Setting Dialog Title
                            alertDialog.setTitle("Pesan Pemberitahuan!");
                            // Setting Dialog Message
                            alertDialog.setMessage("Pelanggan '" + customerName + "' masih punya hutang Rp. " + m.money(total2));
                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.drawable.info20);
                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    Transaction createdTransaction = mTransactionDAO.createTransaction(date.toString(), credit1, pay1 , total2, Long.parseLong(customerId));
                                    finish();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(AddPayActivity.this).create();
                            // Setting Dialog Title
                            alertDialog.setTitle("Pesan Pemberitahuan!");
                            // Setting Dialog Message
                            alertDialog.setMessage("Hutang pelanggan '" + customerName + "' LUNAS!");
                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.drawable.info20);
                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which)
                                {
                                    Transaction createdTransaction = mTransactionDAO.createTransaction(date.toString(), credit1, pay1, total, Long.parseLong(customerId));
                                    finish();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Kolom bayar masih kosong", Toast.LENGTH_LONG).show();
                    }
                break;
            default:
                break;
        }
    }

    public static String getFormatedCurrency(String value) {
        try {
            NumberFormat formatter = new DecimalFormat("###,###,###,###");
            return formatter.format(Double.parseDouble(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

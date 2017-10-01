package com.tiyanrcode.bon.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
public class AddCreditActivity extends ActionBarActivity implements View.OnClickListener{

    public static final String EXTRA_SELECTED_CUSTOMER_ID = "extra_key_selected_customer_id";

    private EditText dateOfCredit;
    private EditText mCredit;
    private TextView cusName;
    private TextView cusId;
    private Button btnCredit;
    private Button btnDate;

    private int year;
    private int month;
    private int day;
    private int credit;
    private int pay;
    private int saldo;
    static final int DATE_PICKER_ID = 1111;
    private String customerId;
    private String customerName;

    final Calendar c = Calendar.getInstance();
    private List<Transaction> mListTransactions;
    private TransactionDAO mTransactionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit);
        getSupportActionBar().hide();


        //get id and name customer
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey("name")) {
            customerName = bundle.getString("name");
            customerId = bundle.getString("id");
        }

        initView();
        setDate();
        // fill the listView
        mTransactionDAO = new TransactionDAO(this);
        mListTransactions = mTransactionDAO.getTransactionOfTransaction(Long.parseLong(customerId));
        if(mListTransactions != null && !mListTransactions.isEmpty()) {
            Transaction tran = mTransactionDAO.getItemSaldoByPosition(Long.parseLong(customerId));
            saldo = tran.getSaldo();
            Log.d("saldo new", String.valueOf(saldo));
            pay = 0;
        }
        else {
            saldo = 0;
            pay = 0;
        }

        mCredit.addTextChangedListener(new TextWatcher() {
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
                  //  mCredit.setText(formattedPrice);
                    mCredit.setSelection(mCredit.length());
                }
            }
        });
    }

    private void setDate() {
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        dateOfCredit.setText(new StringBuilder().append(day).append("/").append(month).append("/")
                .append(year).append(" "));
    }

    private void initView() {
        dateOfCredit = (EditText) findViewById(R.id.date_credit);
        mCredit = (EditText) findViewById(R.id.txt_credit);
        cusName = (TextView) findViewById(R.id.cre_name);
        cusId = (TextView) findViewById(R.id.cre_id);
        btnCredit = (Button) findViewById(R.id.btn_credit);
        btnDate = (Button) findViewById(R.id.tgl);

        cusName.setText(customerName);
        cusId.setText(customerId);
        btnCredit.setOnClickListener(this);
        btnDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_credit:

                if (!mCredit.getText().toString().isEmpty()) {
                    String mcre = mCredit.getText().toString().replaceAll(",", "");
                    credit = Integer.parseInt(mcre);
                    Log.d("credit", mcre);
                    final Editable date = dateOfCredit.getText();
                    Money m = new Money();
                    AlertDialog alertDialog = new AlertDialog.Builder(AddCreditActivity.this).create();
                    // Setting Dialog Title
                    alertDialog.setTitle("Pesan Pemberitahuan!");
                    // Setting Dialog Message
                    alertDialog.setMessage("Total hutang pelanggan '" + customerName + "' adalah Rp. " + m.money(credit + saldo));
                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.info20);
                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which)
                        {
                            Transaction createdTransaction = mTransactionDAO.createTransaction(date.toString(), credit, pay, credit + saldo, Long.parseLong(customerId));
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
                else {
                    Toast.makeText(this, "Kolom hutang masih kosong", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tgl:
                showDialog(DATE_PICKER_ID);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTransactionDAO.close();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            //setMonth();
            dateOfCredit.setText(new StringBuilder().append(day).append("/").append(month).append("/")
                    .append(year).append(" "));
        }
    };

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

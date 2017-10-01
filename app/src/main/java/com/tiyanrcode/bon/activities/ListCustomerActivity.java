package com.tiyanrcode.bon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiyanrcode.bon.adapter.ListCustomerAdapter;
import com.tiyanrcode.bon.dao.CustomerDAO;
import com.tiyanrcode.bon.model.Customer;
import com.tiyanrcode.bon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class ListCustomerActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    public static final String TAG = "ListCustomerActivity";

    public static final int REQUEST_CODE_ADD_CUSTOMER = 40;
    public static final String EXTRA_ADDED_CUSTOMER = "extra_key_added_customer";

    private ListView mListviewCustomers;
    private TextView mTxtEmptyListCustomers;
    private ImageButton mBtnAddCustomers;

    private ListCustomerAdapter mAdapter;
    private List<Customer> mListCustomers;
    private CustomerDAO mCustomerDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_customer);
        getSupportActionBar().hide();

        // initialize views
        initViews();

        // fill the listView
        mCustomerDAO = new CustomerDAO(this);
        mListCustomers = mCustomerDAO.getAllCustomers();
        if(mListCustomers != null && !mListCustomers.isEmpty()) {
            mAdapter = new ListCustomerAdapter(this, mListCustomers);
            mListviewCustomers.setAdapter(mAdapter);
        }
        else {
            mTxtEmptyListCustomers.setVisibility(View.VISIBLE);
            mListviewCustomers.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        mListviewCustomers = (ListView) findViewById(R.id.list_customer);
        mTxtEmptyListCustomers = (TextView) findViewById(R.id.txt_empty_list_customers);
        mBtnAddCustomers = (ImageButton) findViewById(R.id.btn_add_customer);
        mBtnAddCustomers.setOnClickListener(this);
        mListviewCustomers.setOnItemClickListener(this);
        mListviewCustomers.setOnItemLongClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_CUSTOMER){
            if (resultCode == RESULT_OK) {
                // add the added customer to the listCompanies and refresh the listView
                if (data != null) {
                    Customer createCustomer = (Customer) data.getSerializableExtra(EXTRA_ADDED_CUSTOMER);
                    if (createCustomer != null) {
                        if (mListCustomers == null)
                            mListCustomers = new ArrayList<Customer>();
                        mListCustomers.add(createCustomer);

                        if (mListviewCustomers.getVisibility() != View.VISIBLE) {
                            mListviewCustomers.setVisibility(View.VISIBLE);
                            mTxtEmptyListCustomers.setVisibility(View.GONE);
                        }

                        if (mAdapter == null) {
                            mAdapter = new ListCustomerAdapter(this, mListCustomers);
                            mListviewCustomers.setAdapter(mAdapter);
                        }
                        else {
                            mAdapter.setItems(mListCustomers);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_customer:
                Intent intent = new Intent(this, AddCustomerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("c", 0);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_ADD_CUSTOMER);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Customer clickedCustomer = mAdapter.getItem(position);
        Log.d(TAG, "clickedItem : " + clickedCustomer.getName());
        Log.d(TAG, "clickedItemID : " + clickedCustomer.getId());

        String names[] ={"Bayar","Hutang","Detail Transaksi","Ubah"};

        final AlertDialog alertDialog = new AlertDialog.Builder(ListCustomerActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("List");
        final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        lv.setAdapter(adapter);
        alertDialog.show();
        final Bundle bundle = new Bundle();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                String str=(String)o;
                if (str.equals("Bayar")){
                    Intent intent = new Intent(ListCustomerActivity.this, AddPayActivity.class);
                    intent.putExtra(AddPayActivity.EXTRA_SELECTED_CUSTOMER_ID, clickedCustomer.getId());
                    bundle.putString("id", "" + clickedCustomer.getId());
                    bundle.putString("name", clickedCustomer.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    alertDialog.dismiss();
                } else if (str.equals("Hutang")){
                    Intent intent = new Intent(ListCustomerActivity.this, AddCreditActivity.class);
                    intent.putExtra(AddPayActivity.EXTRA_SELECTED_CUSTOMER_ID, clickedCustomer.getId());
                    bundle.putString("id", "" + clickedCustomer.getId());
                    bundle.putString("name", clickedCustomer.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    alertDialog.dismiss();
                } else if (str.equals("Ubah")){
                    Intent intent = new Intent(ListCustomerActivity.this, AddCustomerActivity.class);
                    bundle.putString("id", "" + clickedCustomer.getId());
                    bundle.putString("name", clickedCustomer.getName());
                    bundle.putString("alamat", clickedCustomer.getAddress());
                    bundle.putInt("c", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    alertDialog.dismiss();
                } else if (str.equals("Detail Transaksi")){
                    Intent intent = new Intent(ListCustomerActivity.this, ListTransactionActivity.class);
                    intent.putExtra(ListTransactionActivity.EXTRA_SELECTED_TRANSACTION_ID, clickedCustomer.getId());
                    bundle.putString("id", "" + clickedCustomer.getId());
                    bundle.putString("name", clickedCustomer.getName());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Customer clickedCustomer = mAdapter.getItem(position);
        Log.d(TAG, "longClickedItem : " + clickedCustomer.getName());
        showDeleteDialogConfirmation(clickedCustomer);
        return true;
    }

    private void showDeleteDialogConfirmation(final Customer clickedCustomer) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Hapus");
        alertDialogBuilder.setMessage("Apakah yakin ingin hapus pelanggan \""+ clickedCustomer.getName()+"\" ?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the company and refresh the list
                if(mCustomerDAO != null) {
                    mCustomerDAO.deleteCustomer(clickedCustomer);
                    mListCustomers.remove(clickedCustomer);

                    //refresh the listView
                    if(mListCustomers.isEmpty()) {
                        mListCustomers = null;
                        mListviewCustomers.setVisibility(View.GONE);
                        mTxtEmptyListCustomers.setVisibility(View.VISIBLE);
                    }
                    mAdapter.setItems(mListCustomers);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(ListCustomerActivity.this, "Sukses di hapus", Toast.LENGTH_SHORT).show();
            }
        });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}

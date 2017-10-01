package com.tiyanrcode.bon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tiyanrcode.bon.adapter.ListTransactionAdapter;
import com.tiyanrcode.bon.dao.TransactionDAO;
import com.tiyanrcode.bon.model.Transaction;
import com.tiyanrcode.bon.R;

import java.util.List;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class ListTransactionActivity extends ActionBarActivity implements AdapterView.OnItemLongClickListener{

    public static final int REQUEST_CODE_ADD_TRANSACTION = 40;
    public static final String EXTRA_ADDED_TRANSACTION = "extra_key_added_employee";
    public static final String EXTRA_SELECTED_TRANSACTION_ID = "extra_key_selected_company_id";

    private String customerId;
    private String month2;
    private String customerName;
    private long mTransactionId = -1;

    private ListView mListviewTransactions;
    private TextView mTxtEmptyListTransactions;

    private ListTransactionAdapter mAdapter;
    private List<Transaction> mListTransactions;
    private TransactionDAO mTransactionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaction);
        getSupportActionBar().hide();

        //get id and name customer
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey("name")) {
            customerName = bundle.getString("name");
            customerId = bundle.getString("id");
        }

        // initialize views
        initViews();

        mTransactionDAO = new TransactionDAO(this);
        Intent intent  = getIntent();
        if(intent != null) {
            this.mTransactionId = intent.getLongExtra(EXTRA_SELECTED_TRANSACTION_ID, -1);
            Log.d("tranId", "" + mTransactionId);
        }

        if(mTransactionId != -1) {
            // fill the listView
            mListTransactions = mTransactionDAO.getTransactionOfTransaction(Long.parseLong(customerId));
            if (mListTransactions != null && !mListTransactions.isEmpty()) {
                mAdapter = new ListTransactionAdapter(this, mListTransactions);
                mListviewTransactions.setAdapter(mAdapter);
            } else {
                mTxtEmptyListTransactions.setVisibility(View.VISIBLE);
                mListviewTransactions.setVisibility(View.GONE);
            }
        }
    }

    private void initViews() {
        mListviewTransactions = (ListView) findViewById(R.id.list_transaction);
        mTxtEmptyListTransactions = (TextView) findViewById(R.id.txt_empty_list_transactions);
        mListviewTransactions.setOnItemLongClickListener(this);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Transaction clickedTransaction = mAdapter.getItem(position);
        showDeleteDialogConfirmation(clickedTransaction);
        return true;
    }

    private void showDeleteDialogConfirmation(final Transaction clickedTransaction) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Hapus");
        alertDialogBuilder.setMessage("Apakah yakin ingin hapus transaksi ini ?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the company and refresh the list
                if(mTransactionDAO != null) {
                    mTransactionDAO.deleteTransaction(clickedTransaction);
                    mListTransactions.remove(clickedTransaction);

                    //refresh the listView
                    if(mListTransactions.isEmpty()) {
                        mListTransactions = null;
                        mListviewTransactions.setVisibility(View.GONE);
                        mTxtEmptyListTransactions.setVisibility(View.VISIBLE);
                    }
                    mAdapter.setItems(mListTransactions);
                    mAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(ListTransactionActivity.this, "Sukses di hapus", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTransactionDAO.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

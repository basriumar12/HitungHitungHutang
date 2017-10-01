package com.tiyanrcode.bon.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tiyanrcode.bon.model.Customer;
import com.tiyanrcode.bon.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class TransactionDAO {

    private static final String TAG = "TransactionDAO";
    private Context mContext;

    //Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private String[] mAllColumns = { DBHelper.COLUMN_TRANSACTION_ID, DBHelper.COLUMN_TRANSACTION_DATE,
            DBHelper.COLUMN_TRANSACTION_CREDIT, DBHelper.COLUMN_TRANSACTION_PAY,
            DBHelper.COLUMN_TRANSACTION_SALDO, DBHelper.COLUMN_TRANSACTION_CUSTOMER_ID};

    public TransactionDAO(Context context) {
        mDbHelper = new DBHelper(context);
        this.mContext = context;
        //open Database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException{
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public Transaction createTransaction(String date, int kredit, int pay, int saldo, long cutomerId){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TRANSACTION_DATE, date);
        values.put(DBHelper.COLUMN_TRANSACTION_CREDIT, kredit);
        values.put(DBHelper.COLUMN_TRANSACTION_PAY, pay);
        values.put(DBHelper.COLUMN_TRANSACTION_SALDO, saldo);
        values.put(DBHelper.COLUMN_TRANSACTION_CUSTOMER_ID, cutomerId);
        long insertId = mDatabase.insert(DBHelper.TABLE_TRANSACTIONS, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_TRANSACTIONS, mAllColumns,
                DBHelper.COLUMN_TRANSACTION_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Transaction newTransaction = cursorTransaction(cursor);
        cursor.close();
        return  newTransaction;
    }

    public List<Transaction> getTransactionOfTransaction(long transactionId) {
        List<Transaction> listTransactions = new ArrayList<Transaction>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_TRANSACTIONS, mAllColumns,
                DBHelper.COLUMN_TRANSACTION_CUSTOMER_ID + " = ? ",
                new String[] {String.valueOf(transactionId)}, null, null,  DBHelper.COLUMN_TRANSACTION_ID + " DESC" );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction transaction = cursorTransaction(cursor);
            listTransactions.add(transaction);
            cursor.moveToNext();
        }
        //make sure to cloe the cursor
        cursor.close();
        return listTransactions;
    }

    public Transaction getItemSaldoByPosition(long position) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_TRANSACTIONS, mAllColumns ,
                DBHelper.COLUMN_TRANSACTION_CUSTOMER_ID + " =? ",
                new String[] {String.valueOf(position)}, null, null, DBHelper.COLUMN_TRANSACTION_ID + " DESC ", " 1 ");
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Transaction company = cursorTransaction(cursor);
        return company;
    }

    public void deleteTransaction(Transaction transaction) {
        long id = transaction.getId();
        System.out.println("the deleted transaction has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_TRANSACTIONS, DBHelper.COLUMN_TRANSACTION_ID + " = " + id, null);
    }

    private Transaction cursorSaldo(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setSaldo(cursor.getInt(0));
        return transaction;
    }

    private Transaction cursorTransaction(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getLong(0));
        transaction.setDate(cursor.getString(1));
        transaction.setCredit(cursor.getInt(2));
        transaction.setPay(cursor.getInt(3));
        transaction.setSaldo(cursor.getInt(4));

        //get the customer by id
        long customerId = cursor.getLong(5);
        CustomerDAO dao = new CustomerDAO(mContext);
        Customer customer = dao.getCustomerById(customerId);
        if (customer != null) {
            transaction.setCustomer(customer);
        }
        return transaction;
    }
}

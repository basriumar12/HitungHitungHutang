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
public class CustomerDAO {

    private static final String TAG = "CustomerDAO";

    //Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { DBHelper.COLUMN_CUSTOMER_ID,
            DBHelper.COLUMN_CUSTOMER_NAME, DBHelper.COLUMN_CUSTOMER_ADDRESS };

    public CustomerDAO(Context context){
        this.mContext = context;
        mDbHelper = new DBHelper(context);
        //open the database
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

    public List<Customer> getAllCustomers(){
        List<Customer> listCustomers = new ArrayList<Customer>();
        Cursor cursor = mDatabase.query(DBHelper.TABLE_CUSTOMERS, mAllColumns,
                null, null, null, null,  DBHelper.COLUMN_CUSTOMER_NAME +" ASC");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Customer customer = cursorToCustomer(cursor);
                listCustomers.add(customer);
                cursor.moveToNext();
            }
            //make sure to close the cursor
            cursor.close();
        }
        return  listCustomers;
    }

    public Customer createCustomer(String name, String address){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CUSTOMER_NAME, name);
        values.put(DBHelper.COLUMN_CUSTOMER_ADDRESS, address);
        long insertId = mDatabase.insert(DBHelper.TABLE_CUSTOMERS, null, values);

        Cursor cursor = mDatabase.query(DBHelper.TABLE_CUSTOMERS, mAllColumns,
                DBHelper.COLUMN_CUSTOMER_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Customer newCustomer = cursorToCustomer(cursor);
        cursor.close();
        return newCustomer;
    }

    public void updateCustomer(long id, String name, String address){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CUSTOMER_NAME, name);
        values.put(DBHelper.COLUMN_CUSTOMER_ADDRESS, address);
        mDatabase.update(DBHelper.TABLE_CUSTOMERS, values, DBHelper.COLUMN_CUSTOMER_ID + " = ? "
                , new String[]{String.valueOf(id)});
    }

    public void deleteCustomer(Customer customer){
        long id = customer.getId();
        //delete all transaction of this customer
        TransactionDAO transactionDAO = new TransactionDAO(mContext);
        List<Transaction> listTransactions = transactionDAO.getTransactionOfTransaction(id);
        if (listTransactions != null && !listTransactions.isEmpty()) {
            for (Transaction t : listTransactions) {
                transactionDAO.deleteTransaction(t);
            }
        }
        System.out.println("the deleted company has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_CUSTOMERS, DBHelper.COLUMN_CUSTOMER_ID
                + " = " + id, null);

    }

    protected Customer cursorToCustomer(Cursor cursor) {
        Customer company = new Customer();
        company.setId(cursor.getLong(0));
        company.setName(cursor.getString(1));
        company.setAddress(cursor.getString(2));
        return company;
    }

    public Customer getCustomerById(long id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_CUSTOMERS, mAllColumns,
                DBHelper.COLUMN_CUSTOMER_ID + " = ? ",
                new  String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Customer customer = cursorToCustomer(cursor);
        return customer;
    }

}

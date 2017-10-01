package com.tiyanrcode.bon.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sulistiyanto on 19-Apr-15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";

    // columns of the companies table
    public static final String TABLE_CUSTOMERS = "customer";
    public static final String COLUMN_CUSTOMER_ID = "_id";
    public static final String COLUMN_CUSTOMER_NAME = "cus_name";
    public static final String COLUMN_CUSTOMER_ADDRESS = "address";

    // columns of the employees table
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANSACTION_ID = COLUMN_CUSTOMER_ID;
    public static final String COLUMN_TRANSACTION_DATE = "tran_date";
    public static final String COLUMN_TRANSACTION_CREDIT = "kredit";
    public static final String COLUMN_TRANSACTION_PAY = "pay";
    public static final String COLUMN_TRANSACTION_SALDO = "saldo";
    public static final String COLUMN_TRANSACTION_CUSTOMER_ID = "company_id";

    private static final String DATABASE_NAME = "companies.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement of the employees table creation
    private static final String SQL_CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
            + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TRANSACTION_DATE + " TEXT NOT NULL, "
            + COLUMN_TRANSACTION_CREDIT + " INTEGER NOT NULL, "
            + COLUMN_TRANSACTION_PAY + " INTEGER NOT NULL, "
            + COLUMN_TRANSACTION_SALDO + " INTEGER NOT NULL, "
            + COLUMN_TRANSACTION_CUSTOMER_ID + " INTEGER NOT NULL "
            +");";

    // SQL statement of the companies table creation
    private static final String SQL_CREATE_TABLE_CUSTOMER = "CREATE TABLE " + TABLE_CUSTOMERS + "("
            + COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CUSTOMER_NAME + " TEXT NOT NULL, "
            + COLUMN_CUSTOMER_ADDRESS + " TEXT NOT NULL "
            +");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_CUSTOMER);
        db.execSQL(SQL_CREATE_TABLE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading the database from version " + oldVersion + " to " + newVersion);
        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);

        // recreate the tables
        onCreate(db);
    }
}

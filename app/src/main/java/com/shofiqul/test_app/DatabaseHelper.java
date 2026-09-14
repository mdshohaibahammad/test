package com.shofiqul.test_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hisab_enterprise.db";
    private static final int DATABASE_VERSION = 1;

    // Table Transactions
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TX_ID = "id";
    public static final String COL_TX_TITLE = "title";
    public static final String COL_TX_BUY = "buy_price";
    public static final String COL_TX_SELL = "sell_price";
    public static final String COL_TX_QTY = "quantity";
    public static final String COL_TX_EXTRA = "extra_cost";
    public static final String COL_TX_VAT = "vat_percent";
    public static final String COL_TX_DISCOUNT = "discount_percent";
    public static final String COL_TX_PROFIT = "net_profit";
    public static final String COL_TX_MARGIN = "margin_percent";
    public static final String COL_TX_DATE = "date_str";
    public static final String COL_TX_TIMESTAMP = "timestamp";

    // Table Dues (Khata)
    public static final String TABLE_DUES = "dues";
    public static final String COL_DUE_ID = "id";
    public static final String COL_DUE_NAME = "customer_name";
    public static final String COL_DUE_PHONE = "customer_phone";
    public static final String COL_DUE_AMOUNT = "due_amount";
    public static final String COL_DUE_NOTES = "notes";
    public static final String COL_DUE_DATE = "date_str";
    public static final String COL_DUE_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTxTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TX_TITLE + " TEXT, " +
                COL_TX_BUY + " REAL, " +
                COL_TX_SELL + " REAL, " +
                COL_TX_QTY + " INTEGER, " +
                COL_TX_EXTRA + " REAL, " +
                COL_TX_VAT + " REAL, " +
                COL_TX_DISCOUNT + " REAL, " +
                COL_TX_PROFIT + " REAL, " +
                COL_TX_MARGIN + " REAL, " +
                COL_TX_DATE + " TEXT, " +
                COL_TX_TIMESTAMP + " INTEGER)";

        String createDueTable = "CREATE TABLE " + TABLE_DUES + " (" +
                COL_DUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DUE_NAME + " TEXT, " +
                COL_DUE_PHONE + " TEXT, " +
                COL_DUE_AMOUNT + " REAL, " +
                COL_DUE_NOTES + " TEXT, " +
                COL_DUE_DATE + " TEXT, " +
                COL_DUE_STATUS + " TEXT)";

        db.execSQL(createTxTable);
        db.execSQL(createDueTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DUES);
        onCreate(db);
    }

    // --- TRANSACTION METHODS ---

    public long insertTransaction(TransactionItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TX_TITLE, item.getTitle());
        cv.put(COL_TX_BUY, item.getBuyPrice());
        cv.put(COL_TX_SELL, item.getSellPrice());
        cv.put(COL_TX_QTY, item.getQuantity());
        cv.put(COL_TX_EXTRA, item.getExtraCost());
        cv.put(COL_TX_VAT, item.getVatPercent());
        cv.put(COL_TX_DISCOUNT, item.getDiscountPercent());
        cv.put(COL_TX_PROFIT, item.getNetProfit());
        cv.put(COL_TX_MARGIN, item.getMarginPercent());
        cv.put(COL_TX_DATE, item.getDate());
        cv.put(COL_TX_TIMESTAMP, item.getTimestamp() > 0 ? item.getTimestamp() : System.currentTimeMillis());
        return db.insert(TABLE_TRANSACTIONS, null, cv);
    }

    public List<TransactionItem> getAllTransactions(String searchKeyword) {
        List<TransactionItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSACTIONS;
        String[] args = null;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            query += " WHERE " + COL_TX_TITLE + " LIKE ? OR " + COL_TX_DATE + " LIKE ?";
            String wild = "%" + searchKeyword.trim() + "%";
            args = new String[]{wild, wild};
        }
        query += " ORDER BY " + COL_TX_TIMESTAMP + " DESC";

        Cursor c = db.rawQuery(query, args);
        if (c != null && c.moveToFirst()) {
            do {
                TransactionItem item = new TransactionItem(
                        c.getLong(c.getColumnIndexOrThrow(COL_TX_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TX_TITLE)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_BUY)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_SELL)),
                        c.getInt(c.getColumnIndexOrThrow(COL_TX_QTY)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_EXTRA)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_VAT)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_DISCOUNT)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_PROFIT)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_TX_MARGIN)),
                        c.getString(c.getColumnIndexOrThrow(COL_TX_DATE)),
                        c.getLong(c.getColumnIndexOrThrow(COL_TX_TIMESTAMP))
                );
                list.add(item);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public boolean deleteTransaction(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRANSACTIONS, COL_TX_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public float getTotalNetProfit() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_TX_PROFIT + ") FROM " + TABLE_TRANSACTIONS, null);
        float total = 0f;
        if (c != null && c.moveToFirst()) {
            total = c.getFloat(0);
            c.close();
        }
        return total;
    }

    // --- DUES (KHATA) METHODS ---

    public long insertDue(DueItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DUE_NAME, item.getCustomerName());
        cv.put(COL_DUE_PHONE, item.getCustomerPhone());
        cv.put(COL_DUE_AMOUNT, item.getDueAmount());
        cv.put(COL_DUE_NOTES, item.getNotes());
        cv.put(COL_DUE_DATE, item.getDate());
        cv.put(COL_DUE_STATUS, item.getStatus());
        return db.insert(TABLE_DUES, null, cv);
    }

    public List<DueItem> getAllDues(String searchKeyword) {
        List<DueItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_DUES;
        String[] args = null;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            query += " WHERE " + COL_DUE_NAME + " LIKE ? OR " + COL_DUE_PHONE + " LIKE ?";
            String wild = "%" + searchKeyword.trim() + "%";
            args = new String[]{wild, wild};
        }
        query += " ORDER BY " + COL_DUE_ID + " DESC";

        Cursor c = db.rawQuery(query, args);
        if (c != null && c.moveToFirst()) {
            do {
                DueItem item = new DueItem(
                        c.getLong(c.getColumnIndexOrThrow(COL_DUE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_DUE_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_DUE_PHONE)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_DUE_AMOUNT)),
                        c.getString(c.getColumnIndexOrThrow(COL_DUE_NOTES)),
                        c.getString(c.getColumnIndexOrThrow(COL_DUE_DATE)),
                        c.getString(c.getColumnIndexOrThrow(COL_DUE_STATUS))
                );
                list.add(item);
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public boolean updateDueStatus(long id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DUE_STATUS, status);
        return db.update(TABLE_DUES, cv, COL_DUE_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteDue(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DUES, COL_DUE_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public float getTotalDueAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_DUE_AMOUNT + ") FROM " + TABLE_DUES + " WHERE " + COL_DUE_STATUS + "='DUE'", null);
        float total = 0f;
        if (c != null && c.moveToFirst()) {
            total = c.getFloat(0);
            c.close();
        }
        return total;
    }
}

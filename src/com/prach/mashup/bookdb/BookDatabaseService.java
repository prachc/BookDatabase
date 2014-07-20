package com.prach.mashup.bookdb;
import java.util.Arrays;
import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;


public class BookDatabaseService extends Service {
	private static final String TAG = "com.prach.mashup.BookDBService";
	private String DATABASE_NAME;
	private String DATABASE_TABLE;
	private static final int BDB_SERVICE_CODE = 0x66686601; //GPS1
	
	private class BDBServiceBinder extends Binder {
		@Override
		protected synchronized boolean onTransact(int code, Parcel data, Parcel reply,int flags) {
			DATABASE_NAME = getString(R.string.db_name);
			DATABASE_TABLE = getString(R.string.db_table);
			if(code==BDB_SERVICE_CODE){				
				Bundle bundle = data.readBundle();
				String command = bundle.getString("COMMAND");
				Log.i(TAG,"COMMAND="+command);
				if(command.equals("ADD")){
					String title = bundle.getString("TITLE");
					String isbn = bundle.getString("ISBN");
					String price = bundle.getString("PRICE");
					Log.i(TAG,"TITLE="+command);
					Log.i(TAG,"ISBN="+isbn);
					Log.i(TAG,"PRICE="+price);
					bundle = new Bundle();
					if(doAdd(title,isbn,price))
						bundle.putString("STATUS", "succeed");
					else
						bundle.putString("STATUS", "failed");
					reply.writeBundle(bundle);
				}else if(command.equals("SUM")){
					String[][] dbdata = getData();
					//int sumprice = sumPrice();
					bundle = new Bundle();
					if(dbdata==null){
						bundle.putString("STATUS", "failed");
						bundle.putStringArray("ID", null);
						bundle.putStringArray("TITLE", null);
						bundle.putStringArray("ISBN", null);
						bundle.putStringArray("PRICE", null);
						bundle.putString("TOTAL", "0");
					}else{// if(sumprice!=0){
						bundle.putString("STATUS", "succeed");
						bundle.putStringArray("ID", dbdata[0]);
						bundle.putStringArray("TITLE", dbdata[1]);
						bundle.putStringArray("ISBN", dbdata[2]);
						bundle.putStringArray("PRICE", dbdata[3]);
						//bundle.putString("TOTAL", Integer.toString(sumprice));
					}/*else if(sumprice==0){
						bundle.putString("STATUS", "no data");
						bundle.putStringArray("ID", dbdata[0]);
						bundle.putStringArray("TITLE", dbdata[1]);
						bundle.putStringArray("ISBN", dbdata[2]);
						bundle.putStringArray("PRICE", dbdata[3]);
						bundle.putString("TOTAL", Integer.toString(sumprice));
					}*/
					reply.writeBundle(bundle);
				}
				
				
				return true;
			}else{ 
				Log.e(getClass().getSimpleName(),"Transaction code should be "+ BDB_SERVICE_CODE + ";"	+ " received instead " + code);
				return false;
			}
		}
	}
	
	/*private int sumPrice(){
		SQLiteDatabase myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
		Cursor cursor = myDB.rawQuery("SELECT SUM(price) FROM "+DATABASE_TABLE, null);
		if(cursor.moveToFirst()) {
		    int temp = cursor.getInt(0);
		    cursor.close();
		    myDB.close();
		    return temp;
		}else{ 
		    cursor.close();
		    myDB.close();
			return 0;
		}
			
	}*/
	
	public boolean doAdd(String title,String isbn,String price){
		title = escapeSQL(title);
		isbn = escapeSQL(isbn);
		price = escapeSQL(price);
		SQLiteDatabase myDB = null;
		try {
			Log.i(TAG,"doAdd title="+title+" ,isbn="+isbn+" ,price="+price);
			myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
			myDB.execSQL("INSERT INTO "
					+ DATABASE_TABLE
					+ " (title, isbn, price)"
					+ " VALUES ('"+title+"', '"+isbn+"', "+price+");");
			myDB.close();
		} catch (Exception e) {
			Log.i("bookdb",e.toString());
			return false;
		} finally {
			if (myDB != null)
				myDB.close();
		}
		return true;
	}
	
	public String escapeSQL(String temp){
		return temp.replaceAll("'", "''");
	}
	
	private String[][] getData(){
		Vector<String> ids = new Vector<String>();
		Vector<String> titles = new Vector<String>();
		Vector<String> isbns = new Vector<String>();
		Vector<String> prices = new Vector<String>();
		
		SQLiteDatabase myDB = null;
		try {
			myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
			Cursor c = myDB.query(DATABASE_TABLE, new String[]{"*"}, null, null, null, null, null);
			Log.i("bookdb","columncount="+c.getColumnCount());
			/* Cursor c = myDB.query("SELECT FirstName,Age" +
                                                        " FROM " + DATABASE_TABLE
                                                        + " WHERE Age > 10 LIMIT 7;",
                                                        null);*/
			/* Get the indices of the Columns we will need */
			int idColumn = c.getColumnIndex("id");
			int titleColumn = c.getColumnIndex("title");
			int isbnColumn = c.getColumnIndex("isbn");
			int priceColumn = c.getColumnIndex("price");

			/* Check if our result was valid. */
			c.moveToFirst();
			if (c != null) {
				/* Check if at least one Result was returned. */
				if (c.isFirst()) {
					int i = 0;
					/* Loop through all Results */
					do {
						i++;
						/* Retrieve the values of the Entry
						 * the Cursor is pointing to. */
						
						/* We can also receive the Name
						 * of a Column by its Index.
						 * Makes no sense, as we already
						 * know the Name, but just to shwo we can <img src="http://www.anddev.org/images/smilies/wink.png" alt=";)" title="Wink" /> */
						
						ids.add(c.getString(idColumn));
						titles.add(c.getString(titleColumn));
						isbns.add(c.getString(isbnColumn));
						prices.add(c.getString(priceColumn));

						/* Add current Entry to results. */
						
					} while (c.moveToNext());
					Log.i("bookdb","count="+i);
				}
				c.close();
			}
			myDB.close();
		} catch (Exception e) {
			Log.i("bookdb",e.toString());
			return null;
		} finally {
			if (myDB != null)
				myDB.close();
		}
		
		String[][] results = new String[4][];
		results[0] = Arrays.copyOf(ids.toArray(),ids.size(),String[].class);
		results[1] = Arrays.copyOf(titles.toArray(),titles.size(),String[].class);
		results[2] = Arrays.copyOf(isbns.toArray(),isbns.size(),String[].class);
		results[3] = Arrays.copyOf(prices.toArray(),prices.size(),String[].class);
		
		return results;
	}
	
	public void debug(String msg){
		Log.d(TAG,msg);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new BDBServiceBinder();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}
}

package com.prach.mashup.bookdb;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BookDatabaseActivity extends ListActivity {
	public String DATABASE_NAME;
	public String DATABASE_TABLE;
	private ArrayList<String> results;
	private ArrayAdapter<String> aadapter;
	//private Context context;Å@
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//context = this;
		Log.i("bookdb","start"); 
		//Log.i("bookdb",getString(R.string.db_name)); 
		super.onCreate(savedInstanceState);
		DATABASE_NAME = this.getString(R.string.db_name);
		DATABASE_TABLE = getString(R.string.db_table);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		results = doQuery();
		//results = new ArrayList<String>();
		aadapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results);

		this.setListAdapter(aadapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
				builder.setMessage("Operation")
				.setCancelable(false)
				.setPositiveButton("Browse", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int sid) {
						//MyActivity.this.finish(); 
						//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), (querybyID((int)id)), Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int sid) {
						deletebyID((int)id);
						results = doQuery();
						setListAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results));
						aadapter.notifyDataSetChanged();
					}
				});
				@SuppressWarnings("unused")
				AlertDialog alert = builder.create();
				builder.show();
				// When clicked, show a toast with the TextView text
				//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.cmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.summary:
	    	Toast.makeText(getApplicationContext(), "Total Price:Åè"+sumPrice(), Toast.LENGTH_SHORT).show();
	    	results = doQuery();
	    	setListAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results));
			aadapter.notifyDataSetChanged();
	        return true;
	    case R.id.reset:
	    	results = doInit();
	    	setListAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results));
			aadapter.notifyDataSetChanged();
	        return true;
	    case R.id.clear:
	    	deleteAll();
	    	results = new ArrayList<String>();
	    	setListAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results));
			aadapter.notifyDataSetChanged();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	public ArrayList<String> doInit(){
		ArrayList<String> temp = new ArrayList<String>();
		SQLiteDatabase myDB = null;
		try {
			myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
			/* Create the Database (no Errors if it already exists) */
			//this.createDatabase(MY_DATABASE_NAME, 1, MODE_PRIVATE, null);
			/* Open the DB and remember it */
			//myDB = this.open.openDatabase(MY_DATABASE_NAME, null);
			/* Create a Table in the Database. */
			//t_Users
			//myDB.execSQL("DROP TABLE t_Users");
			myDB.execSQL("DROP TABLE "+DATABASE_TABLE);
			myDB.execSQL("CREATE TABLE IF NOT EXISTS "
					+ DATABASE_TABLE
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
							"title VARCHAR, " +
							"isbn VARCHAR, " + 
							"price INT(3));");
			/* Add two DataSets to the Table. */
			myDB.execSQL("INSERT INTO "
					+ DATABASE_TABLE
					+ " (title, isbn, price)"
					+ " VALUES ('The Android Developer''s Cookbook', '9780321741233', 2172);");
			myDB.execSQL("INSERT INTO "
					+ DATABASE_TABLE
					+ " (title, isbn, price)"
					+ " VALUES ('iPhone SDK Programming', '9780470742822', 2060);");
			myDB.execSQL("INSERT INTO "
					+ DATABASE_TABLE
					+ " (title, isbn, price)"
					+ " VALUES ('Pro Android 2', '9781430226598', 2510);");
			myDB.execSQL("INSERT INTO "
					+ DATABASE_TABLE
					+ " (title, isbn, price)"
					+ " VALUES ('ANDROID A PROGRAMMER GUIDE', '9780071599887', 2242);");
			/* Query for some results with Selection and Projection. */
			Cursor c = myDB.query(DATABASE_TABLE, new String[]{"*"}, null, null, null, null, null);
			Log.i("bookdb",""+c.getColumnCount());
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
						int id = c.getInt(idColumn);
						String title = c.getString(titleColumn);
						@SuppressWarnings("unused")
						String isbn = c.getString(isbnColumn);
						int price = c.getInt(priceColumn);
						
						/* We can also receive the Name
						 * of a Column by its Index.
						 * Makes no sense, as we already
						 * know the Name, but just to shwo we can <img src="http://www.anddev.org/images/smilies/wink.png" alt=";)" title="Wink" /> */
						

						/* Add current Entry to results. */
						temp.add("" + id + ": " + title
								+ " (price:Åè" + price + ")");
					} while (c.moveToNext());
					Log.i("bookdb","count="+i);
				}
				c.close();
			}
			myDB.close();
		} catch (Exception e) {
			Log.i("bookdb",e.toString());
		} finally {
			if (myDB != null)
				myDB.close();
		}
		return temp;
	}

	public void deletebyID(int id){
		SQLiteDatabase myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
		myDB.execSQL("DELETE FROM "
				+ DATABASE_TABLE
				+ " WHERE ID='"
				+ results.get(id).split(":", 2)[0]+"';");
		Toast.makeText(getApplicationContext(), "Delete:"+id, Toast.LENGTH_SHORT).show();
		myDB.close();
	}
	
	public String querybyID(int id){
		int dbid = Integer.parseInt(results.get(id).split(":", 2)[0]);
		StringBuffer temp = new StringBuffer();
		SQLiteDatabase myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
		Cursor c = myDB.query(DATABASE_TABLE, new String[]{"*"}, "id='"+dbid+"'", null, null, null, null);
		//int idColumn = c.getColumnIndex("id");
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
					String title = c.getString(titleColumn);
					String isbn = c.getString(isbnColumn);
					int price = c.getInt(priceColumn);
					
					/* We can also receive the Name
					 * of a Column by its Index.
					 * Makes no sense, as we already
					 * know the Name, but just to shwo we can <img src="http://www.anddev.org/images/smilies/wink.png" alt=";)" title="Wink" /> */
					

					/* Add current Entry to results. */
					temp.append(dbid+":"+title+"\n");
					temp.append("isbn:"+isbn+"\n");
					temp.append("(price:Åè" + price + ")");
				} while (c.moveToNext());
				Log.i("bookdb","count="+i);
				return temp.toString();
			}
			c.close();
		}
		myDB.close();
		return "no data";
	}
	
	private int sumPrice(){
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
	}
	
	public void deleteAll(){
		SQLiteDatabase myDB = openOrCreateDatabase(DATABASE_NAME, MODE_WORLD_WRITEABLE, null);
		myDB.execSQL("DROP TABLE "+DATABASE_TABLE);
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
				+ DATABASE_TABLE
				+ " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
						"title VARCHAR, " +
						"isbn VARCHAR, " + 
						"price INT(3));");
		Toast.makeText(getApplicationContext(), "Delete All", Toast.LENGTH_SHORT).show();
		myDB.close();
	}

	public ArrayList<String> doQuery(){
		ArrayList<String> temp = new ArrayList<String>();
		//results.clear();
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
						int id = c.getInt(idColumn);
						String title = c.getString(titleColumn);
						String isbn = c.getString(isbnColumn);
						int price = c.getInt(priceColumn);
						
						/* We can also receive the Name
						 * of a Column by its Index.
						 * Makes no sense, as we already
						 * know the Name, but just to shwo we can <img src="http://www.anddev.org/images/smilies/wink.png" alt=";)" title="Wink" /> */
						

						/* Add current Entry to results. */
						temp.add("" + id + ": " + title
								+ " (price:Åè" + price + ")");
					} while (c.moveToNext());
					Log.i("bookdb","count="+i);
				}
				c.close();
			}
		} catch (Exception e) {
			Log.i("bookdb",e.toString());
		} finally {
			if (myDB != null)
				myDB.close();
		}
		myDB.close();
		return temp;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			results = doQuery();
			setListAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, results));
			aadapter.notifyDataSetChanged();
			onResume();
		}
		return super.onKeyDown(keyCode, event);
	}
}
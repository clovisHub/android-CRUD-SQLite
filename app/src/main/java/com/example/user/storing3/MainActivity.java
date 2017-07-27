package com.example.user.storing3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.storing3.FeedReaderContract.FeedEntry;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DBHelper helper;
    private SQLiteDatabase database;
    private static final String TAG = MainActivity.class.getSimpleName()+"_TAG";
    EditText titleUI, subTilteUI;
    TextView resultUI;
    Button saveUI_btn, readUI_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        database = helper.getWritableDatabase();

        titleUI = (EditText) findViewById(R.id.lbl_titleId);
        subTilteUI =(EditText) findViewById(R.id.lbl_subtitleId);
        resultUI = (TextView) findViewById(R.id.lbl_result_Tv_Id);

        saveUI_btn = (Button) findViewById(R.id.lbl_savedBtn);
        readUI_btn = (Button) findViewById(R.id.lbl_readBtn);

        saveUI_btn.setOnClickListener(this);
        readUI_btn.setOnClickListener(this);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        database.close();
    }


    private void saveRecord(){


        //String title = "Record title";
        //String subtitle = "Record subtitle";

        String valueUnderTitle = titleUI.getText().toString();
        String valueUnderSubtitle = subTilteUI.getText().toString();

        ContentValues values;
        values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE,valueUnderTitle);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE,valueUnderSubtitle);

        long recordId = database.insert( FeedEntry.TABLE_NAME, null, values);

        if(recordId>0){
            Log.d(TAG, "saveRecord: Record saved");
        }
        else{
            Log.d(TAG, "saveRecord: Record not save");
        }
        titleUI.setText("");
        subTilteUI.setText("");

    }

    @Override
    protected void onResume(){
        super.onResume();
        saveRecord();
        readRecord();
    }

    private void readRecord(){
        String [] projection = {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_SUBTITLE};

        String selection = FeedEntry.COLUMN_NAME_TITLE+"= ?";

        String [] selectionArg = { "Record title"};

        String sortedOrder = FeedEntry.COLUMN_NAME_SUBTITLE+" DESC";

        StringBuilder myName = new StringBuilder();

        myName.append("  Title          SubTitle  \n");

        Cursor cursor = database.query(
                FeedEntry.TABLE_NAME, //TAble
                projection,           //Projection
                null,                 //Selection
                null,                 //Values
                null,                 // Group by
                null,                 // Filter
                null);                // Sort order

        while(cursor.moveToNext()){
            long entryId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            String entryTitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String entrySubtitle = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            myName.append(" "+entryTitle+"  "+entrySubtitle+" \n");
            Log.d(TAG, "readRecord: id: "+entryId+ " title: "+entryTitle+" subtitle: "+entrySubtitle);


        }

        resultUI.setText(""+myName.toString());

        ;
    }

    private void deleteRecord(){
        
        String selection = FeedEntry.COLUMN_NAME_TITLE+" LIKE ?";
        String [] selectionArg = {"Record title"};
        
        int deleted = database.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArg
        );
        if(deleted>0){
            Log.d(TAG, "deleteRecord: record deleted");
        }
        else{
            Log.d(TAG, "deleteRecord: record not deleted");
        }
    }

    private void updateRecord(){

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE,"Record new title");
        
        String selection = FeedEntry.COLUMN_NAME_TITLE +" LIKE ?";
        String [] selectionArg ={"Record Title"};
        
        int count = database.update(FeedEntry.TABLE_NAME, values,selection,selectionArg);
        
        if(count > 0){
            Log.d(TAG, "updateRecord: Records Updated");
        }
        else{
            Log.d(TAG, "updateRecord: No Records Updated");
        }

    }
    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.lbl_savedBtn:
                saveRecord();
                break;
            case R.id.lbl_readBtn:
                readRecord();
                break;

        }
    }
}

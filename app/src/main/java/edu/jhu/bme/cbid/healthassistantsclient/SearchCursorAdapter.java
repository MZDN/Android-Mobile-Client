package edu.jhu.bme.cbid.healthassistantsclient;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Class to populate the patient search system with information from the database
 */
public class SearchCursorAdapter extends CursorAdapter {

    public SearchCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }




    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBody = (TextView) view.findViewById(R.id.list_item_body);
        TextView tvHead = (TextView) view.findViewById(R.id.list_item_head);

        // Extract properties from cursor
        String patientID = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String fName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
        String mName = cursor.getString(cursor.getColumnIndexOrThrow("middle_name"));
        char mInitial = '\0';
        if (!mName.equals("")) mInitial = mName.charAt(0);

        String lName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
        String header = String.format("%s, %s %c - ID: %s", lName, fName, mInitial, patientID);

        String dob = cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth"));
        int age = HelperMethods.getAge(dob);
        // String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
        String body = String.format("Date of Birth: %s (Age %d)", dob, age);

        // Populate fields with extracted properties
        tvHead.setText(header);
        tvBody.setText(body);
    }


}

package com.example.suraj.deltaappdevtask3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ListView list;
    CustomAdapter customAdapter;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        editText = (EditText) findViewById(R.id.editText2);
        list = (ListView) findViewById(R.id.list);
        customAdapter = new CustomAdapter(this);
        list.setAdapter(customAdapter);
    }

    public void clearHistory (View v) {
        if (MainActivity.count == 0)
            Toast.makeText(getApplicationContext(),"No items present",Toast.LENGTH_LONG).show();
        MainActivity.count = 0;
        MainActivity.strings.clear();
        MainActivity.bitmaps.clear();
        list.setAdapter(customAdapter);

    }

    public void removePokemon (View v) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int flag = -1;
        String s = editText.getText().toString();
        s = s.trim().toUpperCase();

        editText.setText("");
        editText.clearFocus();

       for (int i=0; i<MainActivity.count; i++) {
           String t = MainActivity.strings.get(i);
            if (s.equalsIgnoreCase(t)) {
               MainActivity.strings.remove(i);
               MainActivity.bitmaps.remove(i);
               MainActivity.count--;
               flag = 1;
               list.setAdapter(customAdapter);
               break;
          }
       }

        if (flag != 1)
        Toast.makeText(getApplicationContext(), "Item not found!", Toast.LENGTH_LONG).show();
    }

    class MyViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder (View view) {
            imageView = (ImageView) view.findViewById(R.id.icon);
            textView = (TextView) view.findViewById(R.id.text1);
        }
    }

    class CustomAdapter extends ArrayAdapter<String> {

        Context context;

        public CustomAdapter (Context context) {
            super(context, R.layout.layout_list, R.id.text1, MainActivity.strings);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            MyViewHolder holder = null;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.layout_list,parent,false);
                holder = new MyViewHolder(row);
                row.setTag(holder);
            }

            else {
                holder = (MyViewHolder) row.getTag();
            }

            holder.imageView.setImageBitmap(MainActivity.bitmaps.get(position));
            holder.textView.setText(MainActivity.strings.get(position));

            return row;
        }
    }
}



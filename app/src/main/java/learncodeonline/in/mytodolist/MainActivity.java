package learncodeonline.in.mytodolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> myTodoTasks = new ArrayList<>();
    ListView listView;
    Button add_task;
    DatabaseHelper myDB;
    ArrayAdapter<String> arrayAdapter;

    //code added by vaibhav
    //hello guys, I am vaibhav, and this is the code added by me
    //end of code by vaibhav

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);
        listView = findViewById(R.id.listview);
        add_task = findViewById(R.id.add_task);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myTodoTasks);
        listView.setAdapter(arrayAdapter);

        viewAll();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog("Update", position);
            }
        });

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Add", 0);
            }
        });
    }

    public void viewAll() {
        Cursor cursor = myDB.getAllData();
        myTodoTasks.clear();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "The list is empty", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                myTodoTasks.add(cursor.getString(0));
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void openDialog(String decider, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your ToDo");
        final View view = getLayoutInflater().inflate(R.layout.task_taker, null);
        final EditText editText = view.findViewById(R.id.task);

        if (decider.equals("Add")) {
            builder.setPositiveButton(decider, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please provide some data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean isInserted = myDB.insertData(editText.getText().toString());

                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                        viewAll();
                    } else {
                        Toast.makeText(MainActivity.this, "Error in insert data", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else if (decider.equals("Update")) {
            editText.setText(myTodoTasks.get(position));

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "ADD Your Todo First", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }

                    boolean check = myDB.updateData(myTodoTasks.get(position), editText.getText().toString());

                    if (check) {
                        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        viewAll();
                    }
                }
            });

            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Integer check = myDB.deleteData(editText.getText().toString());

                    if (check > 0) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Delete Error", Toast.LENGTH_SHORT).show();
                    }
                    myTodoTasks.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                }
            });
        }

        builder.setIcon(R.drawable.pen);
        builder.setView(view);
        builder.show();
    }
}

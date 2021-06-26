package com.javaproje.alisveris;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EditListActivity extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    public static ArrayList < ProductModel > arrayList = new ArrayList < > ();
    TextView txtDate, txtTime;
    static EditText alisverisadi;
    static TextView alisveristarihi;
    static EditText alisverisyeri;
    static TextView alisverissaati;
    public static RecyclerView rc;
    static DatabaseReference database2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editlist);

        FirebaseApp.initializeApp(getApplicationContext());

        Log.e("calisanlar1", "EditlistActivity");

        alisveristarihi = findViewById(R.id.alisveristarih);
        alisverisadi = findViewById(R.id.alisverisadi);
        alisverisyeri = findViewById(R.id.alisverisyeri);
        alisverissaati = findViewById(R.id.alisverissaat);

        arrayList.clear();

        Bundle b = getIntent().getExtras();
        alisverisadi.setText(b.getString("listeadi"));

        database2 = FirebaseDatabase.getInstance().getReference().child("mylists").child(alisverisadi.getText().toString());
        database2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alisveristarihi.setText(snapshot.child("shopdate").getValue(String.class));
                alisverisyeri.setText(snapshot.child("shopplace").getValue(String.class));
                alisverissaati.setText(snapshot.child("shoptime").getValue(String.class));

                if (snapshot.hasChild("products")) {
                    if (snapshot.child("products").hasChild("notbought")) {

                        for (DataSnapshot postSnapshot: snapshot.child("products").child("notbought").getChildren()) {

                            arrayList.add(new ProductModel(postSnapshot.child("productname").getValue(String.class),
                                    postSnapshot.child("productquantity").getValue(String.class),
                                    postSnapshot.child("productprice").getValue(String.class)
                            ));
                        }
                    }
                    if (snapshot.child("products").hasChild("bought")) {
                        for (DataSnapshot postSnapshot: snapshot.child("products").child("bought").getChildren()) {

                            arrayList.add(new ProductModel(postSnapshot.child("productname").getValue(String.class),
                                    postSnapshot.child("productquantity").getValue(String.class),
                                    postSnapshot.child("productprice").getValue(String.class)
                            ));
                        }
                    }
                }
                final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList, EditListActivity.this);
                rc.setAdapter(listViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        alisveristarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alisveristarihi.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        alisverissaati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditListActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                alisverissaati.setText(hourOfDay + ":" + minute);

                                String date = alisveristarihi.getText().toString() + " " + hourOfDay + ":" + minute;
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy H:m", Locale.ENGLISH);
                                LocalDateTime localDate = LocalDateTime.parse(date, formatter);
                                MainActivity.alarmmilisec = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

                                Log.e("alartime", "alarmmilis: " + MainActivity.alarmmilisec + " currentmilis: " + System.currentTimeMillis());

                                AlarmManager alarmMgr = (AlarmManager) MainActivity.activity.getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(MainActivity.activity, AlarmService.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, MainActivity.alarmmilisec, 10 * 60 * 1000 * 999999, pendingIntent);

                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        Button uruneklebtn = findViewById(R.id.button2);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new MyDialogFragment2();
                dialogFragment.show(getSupportFragmentManager(), "aa");
            }
        });

        rc = findViewById(R.id.recylcerurnler);
        final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList, this);
        rc.setAdapter(listViewAdapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
        listViewAdapter.notifyDataSetChanged();

        final HashMap < String, String > hashMap = new HashMap < > ();

        Button kaydetbtn = findViewById(R.id.kaydetbtn);
        kaydetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alisverisadi.getText().toString().length() < 1 ||
                        alisveristarihi.getText().toString().length() < 1 ||
                        alisverisyeri.getText().toString().length() < 1 ||
                        arrayList.size() < 1) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle("Hata");
                    alertDialog.setMessage("Tüm bilgileri girin ve en az 1 ürün ekleyin");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    DatabaseReference database3 = FirebaseDatabase.getInstance().getReference().child("mylists").child(alisverisadi.getText().toString());

                    hashMap.put("shopname", alisverisadi.getText().toString());
                    hashMap.put("shopdate", alisveristarihi.getText().toString());
                    hashMap.put("shoptime", alisverissaati.getText().toString());
                    hashMap.put("shopplace", alisverisyeri.getText().toString());
                    database3.setValue(hashMap).addOnCompleteListener(new OnCompleteListener < Void > () {
                        @Override
                        public void onComplete(@NonNull Task < Void > task) {
                            if (task.isSuccessful()) {
                                final HashMap < String, String > hashMapUrunler = new HashMap < > ();

                                for (ProductModel myurunmodel: arrayList) {

                                    hashMapUrunler.put("productname", myurunmodel.getUrunadi());
                                    hashMapUrunler.put("productprice", myurunmodel.getUrunfiyati());
                                    hashMapUrunler.put("productquantity", myurunmodel.getUrunadeti());

                                    database3.child("products").child("notbought").child(myurunmodel.getUrunadi()).setValue(hashMapUrunler);
                                    hashMapUrunler.clear();
                                }

                                alisverisadi.setText("");
                                alisveristarihi.setText("");
                                alisverisyeri.setText("");
                                alisverissaati.setText("");
                                arrayList.clear();

                                ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList, getApplicationContext());
                                rc.setAdapter(listViewAdapter);

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                /*
                                            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                                            alertDialog.setTitle("Kaydedildi");
                                            alertDialog.setMessage("Liste veritabanına kaydedildi");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();*/

                            }
                        }
                    });
                }

            }
        });

    }
}
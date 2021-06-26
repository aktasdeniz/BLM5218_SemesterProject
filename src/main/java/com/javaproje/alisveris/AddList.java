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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.javaproje.alisveris.sayfalar.mymainpage.ListViewAdapter;
import com.javaproje.alisveris.sayfalar.mymainpage.MyDialogFragment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddList extends AppCompatActivity {
    private int mYear, mMonth, mDay, mHour, mMinute;
    public static ArrayList < ProductModel > arrayList = new ArrayList < > ();
    TextView txtDate, txtTime;
    static EditText alisverisadi;
    static TextView alisveristarihi;
    static EditText alisverisyeri;
    static TextView alisverissaati;
    public static RecyclerView rc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlist);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        alisveristarihi = findViewById(R.id.alisveristarih);
        alisverissaati = findViewById(R.id.alisverissaat);

        alisverisadi = findViewById(R.id.alisverisadi);
        alisverisyeri = findViewById(R.id.alisverisyeri);

        alisveristarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddList.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddList.this,
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
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        alisverisyeri.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 39.92004889164904, 32.85409449650908);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button uruneklebtn = findViewById(R.id.button2);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "aa");
            }
        });

        rc = findViewById(R.id.recylcerurnler);
        final ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList, getApplicationContext());
        rc.setAdapter(listViewAdapter);
        rc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listViewAdapter.notifyDataSetChanged();

        FirebaseApp.initializeApp(getApplicationContext());

        final HashMap < String, String > hashMap = new HashMap < > ();

        Button kaydetbtn = findViewById(R.id.kaydetbtn);
        kaydetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alisverisadi.getText().toString().length() < 1 ||
                        alisveristarihi.getText().toString().length() < 1 ||
                        alisverissaati.getText().toString().length() < 1 ||
                        alisverisyeri.getText().toString().length() < 1 ||
                        arrayList.size() < 1) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AddList.this).create();
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
                                alisverissaati.setText("");
                                alisverisyeri.setText("");
                                arrayList.clear();

                                ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList, getApplicationContext());
                                rc.setAdapter(listViewAdapter);

                                AlertDialog alertDialog = new AlertDialog.Builder(AddList.this).create();
                                alertDialog.setTitle("Kaydedildi");
                                alertDialog.setMessage("Liste veritabanına kaydedildi");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);

                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }
                        }
                    });
                }
            }
        });

    }
}
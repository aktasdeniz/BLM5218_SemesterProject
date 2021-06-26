package com.javaproje.alisveris.sayfalar.mymainpage;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.javaproje.alisveris.AlarmService;
import com.javaproje.alisveris.MainActivity;
import com.javaproje.alisveris.R;
import com.javaproje.alisveris.ProductModel;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class ListViewerActivith extends Fragment {

    private int mYear, mMonth, mDay, mHour, mMinute;
    public static ArrayList < ProductModel > arrayList = new ArrayList < > ();
    TextView txtDate, txtTime;
    static EditText alisverisadi;
    static TextView alisveristarihi;
    static TextView alisverissaati;
    static EditText alisverisyeri;
    public static RecyclerView rc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.listviewerfragmentactivity, container, false);

        Log.e("calisanlar1", "HomeFragment");
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        alisveristarihi = root.findViewById(R.id.alisveristarih);
        alisverissaati = root.findViewById(R.id.alisverissaat);

        alisverisadi = root.findViewById(R.id.alisverisadi);
        alisverisyeri = root.findViewById(R.id.alisverisyeri);

        alisveristarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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

        /*
         **Paylaş detaylandır.
         **Maile aktarma.
         ***T liste ekledikten sonra anamenüye dönsün.
         **T saati çekmiyor.
         **T alarm çaldığında toast yazısı ekle.
         */

        alisverissaati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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

                                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(getActivity(), AlarmService.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, MainActivity.alarmmilisec, 10 * 60 * 1000 * 999999, pendingIntent);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        Button uruneklebtn = root.findViewById(R.id.button2);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(getParentFragmentManager(), "aa");
            }
        });

        rc = root.findViewById(R.id.recylcerurnler);
        final ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList, getActivity());
        rc.setAdapter(listViewAdapter);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        listViewAdapter.notifyDataSetChanged();

        FirebaseApp.initializeApp(getContext());

        final HashMap < String, String > hashMap = new HashMap < > ();

        Button kaydetbtn = root.findViewById(R.id.kaydetbtn);
        kaydetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alisverisadi.getText().toString().length() < 1 ||
                        alisveristarihi.getText().toString().length() < 1 ||
                        alisverisyeri.getText().toString().length() < 1 ||
                        arrayList.size() < 1) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Hata oluştu");
                    alertDialog.setMessage("Tüm bilgileri girin ve en az 1 ürün ekleyin");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Kapat",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    DatabaseReference database3 = FirebaseDatabase.getInstance().getReference().child("listeler").child(alisverisadi.getText().toString());

                    hashMap.put("shoplistname", alisverisadi.getText().toString());
                    hashMap.put("shoplistdate", alisveristarihi.getText().toString());
                    hashMap.put("shoplistplace", alisverisyeri.getText().toString());
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
                                arrayList.clear();

                                ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList, getContext());
                                rc.setAdapter(listViewAdapter);

                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Kayıt yapıldı ");
                                alertDialog.setMessage("Listeniz başarıyla kaydedilmiştir");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Kapat",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
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

        return root;
    }

}
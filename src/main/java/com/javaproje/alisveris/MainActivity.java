package com.javaproje.alisveris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;
    String urunlist = "";
    static TextView textView;
    public static FragmentManager fragmentManager;
    DatabaseReference databasepay;
    public static long alarmmilisec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text_dashboard);
        databasepay = FirebaseDatabase.getInstance().getReference().child("mylists");

        fragmentManager = getSupportFragmentManager();

        activity = this;
        FloatingActionButton floatbtn = findViewById(R.id.floatingActionButton2);
        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddList.class);
                startActivity(i);
            }
        });

        Query query = databasepay;

        FirebaseRecyclerOptions < ShoplistModel > options =
                new FirebaseRecyclerOptions.Builder < ShoplistModel > ()
                        .setQuery(query, ShoplistModel.class)
                        .build();

        FirebaseRecyclerAdapter < ShoplistModel, MainActivity.UserViewHolder2 > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter < ShoplistModel, MainActivity.UserViewHolder2 > (
                options
        ) {
            @NonNull
            @Override
            public MainActivity.UserViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listnodelayout, parent, false);
                return new MainActivity.UserViewHolder2(view);
            }

            @Override
            protected void onBindViewHolder(final MainActivity.UserViewHolder2 userViewHolder2, int i, @NonNull final ShoplistModel transaction) {
                final String key = getRef(i).getKey();
                databasepay.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("shopdate")) {

                            final String amount = dataSnapshot.child("shopname").getValue().toString();
                            final String status = dataSnapshot.child("shopdate").getValue().toString();
                            final String datetext = dataSnapshot.child("shopplace").getValue().toString();

                            userViewHolder2.setAmount(amount);
                            userViewHolder2.setStatus(status);
                            userViewHolder2.setDate(datetext);

                            if (dataSnapshot.hasChild("products")) {
                                int alinmayanlar = (int) dataSnapshot.child("products").child("notbought").getChildrenCount();
                                int alinanlar = (int) dataSnapshot.child("products").child("bought").getChildrenCount();
                                int toplam = alinanlar + alinmayanlar;

                                userViewHolder2.setUrunadet(toplam + " üründen " + alinanlar + " tanesi alınmış");
                            }
                            textView.setVisibility(View.GONE);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();

        RecyclerView rc2;

        rc2 = findViewById(R.id.recview);
        rc2.setHasFixedSize(true);
        rc2.setLayoutManager(new LinearLayoutManager(getApplicationContext()) {

        });

        rc2.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    public class UserViewHolder2 extends RecyclerView.ViewHolder {
        View mView;
        TextView satatustext;
        TextView amounttext;
        TextView datetext;
        TextView urunedettext;
        Button silbtn, duzenlebtn, paylasbtn;

        public UserViewHolder2(View itemView) {
            super(itemView);
            mView = itemView;
            satatustext = mView.findViewById(R.id.statustext);
            amounttext = mView.findViewById(R.id.Amounttext);
            datetext = mView.findViewById(R.id.exptime);
            urunedettext = mView.findViewById(R.id.urunadettext);
            paylasbtn = mView.findViewById(R.id.button3);

            silbtn = mView.findViewById(R.id.silbtn);
            duzenlebtn = mView.findViewById(R.id.duzenlebtn);

            duzenlebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), EditListActivity.class);
                    Bundle b = new Bundle();
                    b.putString("listeadi", amounttext.getText().toString()); //Your id
                    i.putExtras(b); //Put your id to your next Intent
                    startActivity(i);
                }
            });

            paylasbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    urunlist = "";
                    databasepay.child(amounttext.getText().toString()).child("products").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds: snapshot.child("bought").getChildren()) {
                                urunlist = urunlist + "\n" + ds.getKey();
                            }
                            for (DataSnapshot ds: snapshot.child("notbought").getChildren()) {
                                urunlist = urunlist + "\n" + ds.getKey();
                            }

                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[] {
                                    "ornekemail@gmail.com"
                            });
                            i.putExtra(Intent.EXTRA_SUBJECT, "Paylas");
                            i.putExtra(Intent.EXTRA_TEXT, "Liste Adi: " + amounttext.getText().toString() +
                                    "\n Alışveriş Lokasyonu: " + datetext.getText().toString() +
                                    "\n Alısveris Tarihi: " + satatustext.getText().toString() +
                                    "\n" + urunedettext.getText().toString() +
                                    "\n"+
                                    "\n Ürünler:" +
                                    "\n" + urunlist);
                            //i.setType("application/octet-stream");
                            //i.setData(Uri.parse("mailto:"));
                            //i.setType("message/rfc822");
                            try {
                                MainActivity.activity.startActivity(Intent.createChooser(i, "Mail gönder..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(), "Hiç mail gönderme aracı yüklü değil.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

            silbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databasepay.child(amounttext.getText().toString()).removeValue();
                }
            });

        }
        public void setName(DataSnapshot name) {}
        public void setStatus(String status) {
            TextView UserNameView2 = mView.findViewById(R.id.statustext);
            UserNameView2.setText(status);
        }
        public void setDate(String time) {
            TextView UserNameView2 = mView.findViewById(R.id.exptime);
            UserNameView2.setText(time);
        }
        public void setAmount(String amount) {
            TextView UserNameView2 = mView.findViewById(R.id.Amounttext);
            UserNameView2.setText(amount);

        }
        public void setUrunadet(String amount) {
            TextView UserNameView2 = mView.findViewById(R.id.urunadettext);
            UserNameView2.setText(amount);
        }

    }

}
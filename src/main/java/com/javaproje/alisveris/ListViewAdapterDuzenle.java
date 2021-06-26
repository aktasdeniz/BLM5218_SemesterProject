package com.javaproje.alisveris;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.javaproje.alisveris.sayfalar.mymainpage.MyDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterDuzenle extends RecyclerView.Adapter < ListViewAdapterDuzenle.ViewHolder > {

    private ArrayList < ProductModel > urunmodels = new ArrayList < > ();
    private Context context;

    public ListViewAdapterDuzenle(ArrayList < ProductModel > urunmodels, Context context) {
        this.urunmodels = urunmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productsnodelayout2, parent, false);
        ViewHolder holder = new ViewHolder(view);

        MultiDex.install(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name1.setText(urunmodels.get(position).getUrunadi());
        holder.name2.setText(urunmodels.get(position).getUrunadeti());
        holder.price1.setText(urunmodels.get(position).getUrunfiyati());

        holder.name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.silbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditListActivity.arrayList.remove(position);
                final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(EditListActivity.arrayList, context);
                EditListActivity.rc.setAdapter(listViewAdapter);
            }
        });
        holder.duzenlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new MyDialogFragment();
                Bundle b = new Bundle();
                b.putString("productname", holder.name1.getText().toString()); //Your id
                b.putString("productprice", holder.name2.getText().toString()); //Your id
                b.putString("productquantity", holder.price1.getText().toString()); //Your id
                b.putString("callclass", "listduzenle"); //Your id
                b.putInt("index", position); //Your id
                dialogFragment.setArguments(b); //Put your id
                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "aa");

            }
        });
        EditListActivity.database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("products")) {
                    if (snapshot.child("products").hasChild("notbought")) {
                        if (snapshot.child("products").child("notbought").hasChild(holder.name1.getText().toString())) {
                            holder.checkBox.setChecked(false);
                        }
                    } else {
                        holder.checkBox.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                EditListActivity.database2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("products")) {

                            if (isChecked) {

                                final HashMap < String, String > hashMapUrunler = new HashMap < > ();

                                hashMapUrunler.put("productname", holder.name1.getText().toString());
                                hashMapUrunler.put("productprice", holder.name2.getText().toString());
                                hashMapUrunler.put("productquantity", holder.price1.getText().toString());

                                EditListActivity.database2.child("products").child("notbought").child(holder.name1.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener < Void > () {
                                    @Override
                                    public void onComplete(@NonNull Task < Void > task) {

                                        EditListActivity.database2.child("products").child("bought").child(holder.name1.getText().toString()).setValue(hashMapUrunler);
                                        hashMapUrunler.clear();
                                    }
                                });

                            } else {

                                final HashMap < String, String > hashMapUrunler = new HashMap < > ();

                                hashMapUrunler.put("productname", holder.name1.getText().toString());
                                hashMapUrunler.put("productprice", holder.name2.getText().toString());
                                hashMapUrunler.put("productquantity", holder.price1.getText().toString());

                                EditListActivity.database2.child("products").child("bought").child(holder.name1.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener < Void > () {
                                    @Override
                                    public void onComplete(@NonNull Task < Void > task) {

                                        EditListActivity.database2.child("products").child("notbought").child(holder.name1.getText().toString()).setValue(hashMapUrunler);
                                        hashMapUrunler.clear();
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return urunmodels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name1, name2;
        Button duzenlebtn, silbtn, paylasbtn;
        TextView price1, value1;
        LinearLayout parentLayout;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1 = itemView.findViewById(R.id.name1);
            name2 = itemView.findViewById(R.id.name2);
            price1 = itemView.findViewById(R.id.price1);
            //            value1 = itemView.findViewById(R.id.value1);
            paylasbtn = itemView.findViewById(R.id.button3);
            duzenlebtn = itemView.findViewById(R.id.button4);
            silbtn = itemView.findViewById(R.id.button5);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            checkBox = itemView.findViewById(R.id.checkBox4);
        }
    }
}
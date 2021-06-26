package com.javaproje.alisveris.sayfalar.mymainpage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.javaproje.alisveris.R;
import com.javaproje.alisveris.ProductModel;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter < com.javaproje.alisveris.sayfalar.mymainpage.ListViewAdapter.ViewHolder > {

    private ArrayList < ProductModel > urunmodels = new ArrayList < > ();
    private Context context;

    public ListViewAdapter(ArrayList < ProductModel > urunmodels, Context context) {
        this.urunmodels = urunmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productsnodelayout, parent, false);
        ViewHolder holder = new ViewHolder(view);
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
                ListViewerActivith.arrayList.remove(position);
                final ListViewAdapter listViewAdapter = new ListViewAdapter(ListViewerActivith.arrayList, context);
                ListViewerActivith.rc.setAdapter(listViewAdapter);
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
                b.putString("callclass", "list"); //Your id
                b.putInt("index", position); //Your id
                dialogFragment.setArguments(b); //Put your id
                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "aa");
            }
        });
    }

    @Override
    public int getItemCount() {
        return urunmodels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name1, name2;
        Button duzenlebtn, silbtn;
        TextView price1, value1;
        LinearLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1 = itemView.findViewById(R.id.name1);
            name2 = itemView.findViewById(R.id.name2);
            price1 = itemView.findViewById(R.id.price1);
            //            value1 = itemView.findViewById(R.id.value1);
            duzenlebtn = itemView.findViewById(R.id.button4);
            silbtn = itemView.findViewById(R.id.button5);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
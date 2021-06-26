package com.javaproje.alisveris.sayfalar.mymainpage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.javaproje.alisveris.EditListActivity;
import com.javaproje.alisveris.R;
import com.javaproje.alisveris.ProductModel;
import com.javaproje.alisveris.AddList;

public class MyDialogFragment extends AppCompatDialogFragment {

    String urunadi = "";
    String urunfiyati = "";
    String urunadeti = "";
    String callclass = "";
    int index = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater l = getActivity().getLayoutInflater();
        View view = l.inflate(R.layout.dialogfragment, null);

        Log.e("calisanlar1", "Mydialogfragment(mymianpage klasoru)");

        EditText editTexturunadi = view.findViewById(R.id.urunadiedittext);
        EditText editTexturunmiktari = view.findViewById(R.id.urunmiktariedittext);
        EditText editTexturunfiyati = view.findViewById(R.id.urunfiyatiedittext);

        if (getArguments() != null) {

            urunadi = getArguments().getString("productname");
            urunfiyati = getArguments().getString("productprice");
            urunadeti = getArguments().getString("productquantity");

            callclass = getArguments().getString("callclass");

            index = getArguments().getInt("index");
            editTexturunadi.setText(urunadi);
            editTexturunfiyati.setText(urunfiyati);
            editTexturunmiktari.setText(urunadeti);
        }

        Button uruneklebtn = view.findViewById(R.id.uruneklebtn);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urunadi.length() == 0) {
                    AddList.arrayList.add(new ProductModel(editTexturunadi.getText().toString(),
                            editTexturunfiyati.getText().toString(),
                            editTexturunmiktari.getText().toString()
                    ));
                    final ListViewAdapter listViewAdapter = new ListViewAdapter(AddList.arrayList, getActivity());
                    AddList.rc.setAdapter(listViewAdapter);
                    dismiss();
                } else if (callclass.equals("list")) {
                    AddList.arrayList.get(index).setUrunadeti(editTexturunmiktari.getText().toString());
                    AddList.arrayList.get(index).setUrunadi(editTexturunadi.getText().toString());
                    AddList.arrayList.get(index).setUrunfiyati(editTexturunfiyati.getText().toString());

                    final ListViewAdapter listViewAdapter = new ListViewAdapter(AddList.arrayList, getActivity());
                    AddList.rc.setAdapter(listViewAdapter);
                    dismiss();
                } else {

                    EditListActivity.arrayList.get(index).setUrunadeti(editTexturunmiktari.getText().toString());
                    EditListActivity.arrayList.get(index).setUrunadi(editTexturunadi.getText().toString());
                    EditListActivity.arrayList.get(index).setUrunfiyati(editTexturunfiyati.getText().toString());

                    final ListViewAdapter listViewAdapter = new ListViewAdapter(EditListActivity.arrayList, getActivity());
                    EditListActivity.rc.setAdapter(listViewAdapter);
                    dismiss();
                }
            }
        });
        b.setView(view);
        return b.create();
    }

    public MyDialogFragment() {
        super();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
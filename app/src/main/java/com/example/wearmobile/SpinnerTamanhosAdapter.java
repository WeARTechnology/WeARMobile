package com.example.wearmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerTamanhosAdapter extends ArrayAdapter<String> {


    public SpinnerTamanhosAdapter(Context context, List<String> ringSizes) {
        super(context, android.R.layout.simple_spinner_item, ringSizes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String tamanho = getItem(position);

        View view = convertView;
        view = LayoutInflater.from(getContext()).inflate(R.layout.ring_size_spinner_item, parent,
                false);


        TextView ringSizeTextView = (TextView) view.findViewById(R.id.ring_size);
        ringSizeTextView.append(" " + tamanho);

        return view;
    }


}

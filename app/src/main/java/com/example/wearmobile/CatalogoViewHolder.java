package com.example.wearmobile;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CatalogoViewHolder extends RecyclerView.ViewHolder {

    static TextView nome_do_produto;
    static TextView caracteristica1;
    static TextView caracteristica2;
    static TextView caracteristica3;
    static TextView cifrao;
    static TextView preco;

    public CatalogoViewHolder(@NonNull View itemView) {
        super(itemView);
        nome_do_produto = itemView.findViewById(R.id.nome_do_produto);
        caracteristica1 = itemView.findViewById(R.id.caracteristica1);
        caracteristica2 = itemView.findViewById(R.id.caracteristica2);
        caracteristica3 = itemView.findViewById(R.id.caracteristica3);
        cifrao = itemView.findViewById(R.id.cifrao);
        preco = itemView.findViewById(R.id.preco);

    }
}

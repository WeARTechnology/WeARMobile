package com.example.wearmobile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CatalogoViewHolder extends RecyclerView.ViewHolder {

     TextView nome_do_produto;
     TextView quantidade_do_produto;
     TextView preco;
     ImageView imgProduto;

    public CatalogoViewHolder(@NonNull View itemView) {
        super(itemView);
        nome_do_produto = itemView.findViewById(R.id.nome_do_produto);
        quantidade_do_produto = itemView.findViewById(R.id.quantidadeProduto);
        preco = itemView.findViewById(R.id.preco);
        imgProduto = itemView.findViewById(R.id.imgProduto);

    }
}

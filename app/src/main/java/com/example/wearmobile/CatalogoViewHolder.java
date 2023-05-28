package com.example.wearmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


public class CatalogoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     TextView nome_do_produto;
     TextView quantidade_do_produto;
     TextView preco;
     ImageView imgProduto;
     int id;
     FragmentManager fragmentManager;

    public CatalogoViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
        super(itemView);
        id = itemView.getId();
        nome_do_produto = itemView.findViewById(R.id.nome_do_produto);
        quantidade_do_produto = itemView.findViewById(R.id.quantidadeProduto);
        preco = itemView.findViewById(R.id.preco);
        imgProduto = itemView.findViewById(R.id.imgProduto);
        this.fragmentManager = fragmentManager;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        ProdutoFragment produto = new ProdutoFragment();
        produto.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();

    }
}

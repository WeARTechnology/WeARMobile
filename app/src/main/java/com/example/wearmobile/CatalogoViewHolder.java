package com.example.wearmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
    MainActivity mainActivity;

    public CatalogoViewHolder(@NonNull View itemView, FragmentManager fragmentManager, MainActivity mainActivity) {
        super(itemView);
        //Atribui os itens do recycler aos itens da tela
        nome_do_produto = itemView.findViewById(R.id.nome_do_produto);
        quantidade_do_produto = itemView.findViewById(R.id.quantidadeProduto);
        preco = itemView.findViewById(R.id.preco);
        imgProduto = itemView.findViewById(R.id.imgProduto);
        //Define o fragment manager e a main activity
        this.fragmentManager = fragmentManager;
        this.mainActivity = mainActivity;
        //Define o id do usuario
        id = itemView.getId();
        //Coloca um onClickListener no item do recycler
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Adiciona o ID a um Bundle do fragment
        Bundle bundle = new Bundle();
        bundle.putInt("ID", id);

        ProdutoFragment produto = new ProdutoFragment();
        produto.setArguments(bundle);

        //Troca a fragment pela fragment do produto
        fragmentManager.beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();

        //Esconde o Recycler do SearchBar e coloca o produto, no caso de o produto ter sido encontrado pelo Search
        RecyclerView recycler = mainActivity.findViewById(R.id.recyclerViewSearch);
        recycler.setVisibility(View.INVISIBLE);
        FrameLayout frameLayout = mainActivity.findViewById(R.id.fragmentHolder);
        frameLayout.setVisibility(View.VISIBLE);

    }
}

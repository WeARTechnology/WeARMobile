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
        this.mainActivity = mainActivity;
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

        // Hide the RecyclerView and show the FrameLayout
        RecyclerView recycler = mainActivity.findViewById(R.id.recyclerViewSearch);
        recycler.setVisibility(View.INVISIBLE);
        FrameLayout frameLayout = mainActivity.findViewById(R.id.fragmentHolder);
        frameLayout.setVisibility(View.VISIBLE);

    }
}

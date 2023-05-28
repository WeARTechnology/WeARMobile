package com.example.wearmobile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarrinhoViewHolder extends RecyclerView.ViewHolder{

    TextView txtNome,txtQuantidade,txtPreco, txtTamanho;
    EditText edtQntd;
    Button btnRemover;
    ImageView ivProduto;

    public CarrinhoViewHolder(@NonNull View itemView) {
        super(itemView);
        txtNome = itemView.findViewById(R.id.titulo_carrinho);
        txtQuantidade = itemView.findViewById(R.id.txtQntdCarrinho);
        txtPreco = itemView.findViewById(R.id.txtPrecoCarrinho);
        edtQntd = itemView.findViewById(R.id.edtQntdCarrinho);
        btnRemover = itemView.findViewById(R.id.btnRemoverProduto);
        ivProduto = itemView.findViewById(R.id.ivImagemProdCarrinho);
        txtTamanho = itemView.findViewById(R.id.txtTamanho);
    }

}

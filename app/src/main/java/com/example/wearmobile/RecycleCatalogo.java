package com.example.wearmobile;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class RecycleCatalogo {

    private String nome_do_produto;
    private String quantidade_do_produto;
    private String preco;
    private Bitmap imgProduto;

    public RecycleCatalogo(String nome_do_produto, String quantidade_do_produto, String preco, Bitmap imgProduto){
        this.nome_do_produto = nome_do_produto;
        this.quantidade_do_produto = quantidade_do_produto;
        this.preco = preco;
        this.imgProduto = imgProduto;
    }

    public Bitmap getImgProduto() {
        return imgProduto;
    }

    public void Bitmap (Bitmap imgProduto) {
        this.imgProduto = imgProduto;
    }

    public String getNome_do_produto() {
        return nome_do_produto;
    }

    public void setNome_do_produto(String nome_do_produto) {
        this.nome_do_produto = nome_do_produto;
    }

    public String getQuantidade_do_produto() {
        return quantidade_do_produto;
    }

    public void setQuantidade_do_produto(String quantidade_do_produto) {
        this.quantidade_do_produto = quantidade_do_produto;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }
}

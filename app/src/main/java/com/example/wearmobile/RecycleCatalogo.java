package com.example.wearmobile;

import android.graphics.Bitmap;

public class RecycleCatalogo {

    //Definição de Atributos do recycler
    private int produto_id;
    private String nome_do_produto;
    private String quantidade_do_produto;
    private String preco;
    private Bitmap imgProduto;

    //Construtor da classe
    public RecycleCatalogo(int produto_id, String nome_do_produto, String quantidade_do_produto, String preco, Bitmap imgProduto){
        this.produto_id = produto_id;
        this.nome_do_produto = nome_do_produto;
        this.quantidade_do_produto = quantidade_do_produto;
        this.preco = preco;
        this.imgProduto = imgProduto;
    }

    //Getters e Setters
    public Bitmap getImgProduto() {
        return imgProduto;
    }
    public void setImgProduto (Bitmap imgProduto) {
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

    public int getProduto_id() {
        return produto_id;
    }
    public void setProduto_id(int produto_id) {
        this.produto_id = produto_id;
    }
}

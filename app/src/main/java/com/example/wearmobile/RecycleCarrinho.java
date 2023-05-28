package com.example.wearmobile;

import android.graphics.Bitmap;
import android.widget.Button;

public class RecycleCarrinho {

    private String nome,quantidade,preco, edtQntd, btnRemover,txtTamanho;
    private Bitmap imagem;
    private int id;

    public RecycleCarrinho(String nome, String quantidade, String preco, String edtQntd, Bitmap imagem, int id, String txtTamanho) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
        this.edtQntd = edtQntd;
        this.imagem = imagem;
        this.id = id;
        this.txtTamanho = txtTamanho;
    }

    //Getters
    public String getNome() {
        return nome;
    }
    public String getQuantidade() {
        return quantidade;
    }
    public String getPreco() {
        return preco;
    }
    public String getEdtQntd() {
        return edtQntd;
    }
    public String getBtnRemover() {
        return btnRemover;
    }
    public Bitmap getImagem() {
        return imagem;
    }
    public int getProdId() {
        return id;
    }
    public String getTxtTamanho() {
        return txtTamanho;
    }

    //Setters
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
    public void setPreco(String preco) {
        this.preco = preco;
    }
    public void setEdtQntd(String edtQntd) {
        this.edtQntd = edtQntd;
    }
    public void setBtnRemover(String btnRemover) {
        this.btnRemover = btnRemover;
    }
    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTxtTamanho(String txtTamanho) {
        this.txtTamanho = txtTamanho;
    }
}

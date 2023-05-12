package com.example.wearmobile;

public class RecycleCatalogo {

    private String nome_do_produto;
    private String caracteristica1;
    private String caracteristica2;
    private String caracteristica3;
    private String cifrao;
    private Float preco;

    public RecycleCatalogo(String nome_do_produto, String caracteristica1, String caracteristica2, String caracteristica3, String cifrao, float preco){
        this.nome_do_produto = nome_do_produto;
        this.caracteristica1 = caracteristica1;
        this.caracteristica2 = caracteristica2;
        this.caracteristica3 = caracteristica3;
        this.cifrao = cifrao;
        this.preco = preco;
    }

    public String getNome_do_produto() {
        return nome_do_produto;
    }

    public void setNome_do_produto(String nome_do_produto) {
        this.nome_do_produto = nome_do_produto;
    }

    public String getCaracteristica1() {
        return caracteristica1;
    }

    public void setCaracteristica1(String caracteristica1) {
        this.caracteristica1 = caracteristica1;
    }

    public String getCaracteristica2() {
        return caracteristica2;
    }

    public void setCaracteristica2(String caracteristica2) {
        this.caracteristica2 = caracteristica2;
    }

    public String getCaracteristica3() {
        return caracteristica3;
    }

    public void setCaracteristica3(String caracteristica3) {
        this.caracteristica3 = caracteristica3;
    }

    public String getCifrao() {
        return cifrao;
    }

    public void setCifrao(String cifrao) {
        this.cifrao = cifrao;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }
}

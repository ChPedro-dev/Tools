package com.pz.api_previsao.model.SorteioInstagram;

public class Comentario {
    private int id;
    private String comentario;
    private String usuario;


    public Comentario(int id, String comentario, String usuario) {
        this.id = id;
        this.comentario = comentario;
        this.usuario = usuario;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "id: " + id + "\nusuario: " + usuario + "\ncomentario: " + comentario;
    }



}

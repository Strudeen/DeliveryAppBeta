package com.CriStru.orurodeliveryapp.Models;

public class SubCategoria {
    private String Categoria;
    private String Nombre;
    private String Descripcion;
    private String FotoUrl;
    private String IdSubCategoria;

    public SubCategoria() {
    }

    public SubCategoria(String categoria, String nombre, String descripcion, String fotoUrl, String idSubCategoria) {
        Categoria = categoria;
        Nombre = nombre;
        Descripcion = descripcion;
        FotoUrl = fotoUrl;
        IdSubCategoria = idSubCategoria;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFotoUrl() {
        return FotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        FotoUrl = fotoUrl;
    }

    public String getIdSubCategoria() {
        return IdSubCategoria;
    }

    public void setIdSubCategoria(String idSubCategoria) {
        IdSubCategoria = idSubCategoria;
    }
}

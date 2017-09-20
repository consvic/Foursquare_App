package com.example.cocoy.foursquare_app;

/**
 * Created by cocoy on 18/09/2017.
 */

public class Foto {
    private String nombre;
    private int imagen;

    public Foto(String nombre, int imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public int getImagen() {
        return imagen;
    }
}

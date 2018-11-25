package com.example.carlosjof.testmaps;

public class MapsModel {
    private double latitud;
    private double longitud;
    private String denuncia;
    private String tipodenuncia;
    private String fecha;
    private String usuario;
    private String detalle;
    private String imagen;

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getTipodenuncia() {
        return tipodenuncia;
    }

    public void setTipodenuncia(String tipodenuncia) {
        this.tipodenuncia = tipodenuncia;
    }
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(String denuncia) {
        this.denuncia = denuncia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public MapsModel(){

    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}

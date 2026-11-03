package dto;

import modelo.Direccion;
import modelo.EstadoFantasma;

public class FantasmaDTO {
    private double x;
    private double y;
    private Direccion direccion;
    private EstadoFantasma estado;
    private double tiempoAsustadoRestante;

    public FantasmaDTO() { }

    public FantasmaDTO(double x, double y, Direccion direccion, EstadoFantasma estado,
                       double tiempoAsustadoRestante) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
        this.estado = estado;
        this.tiempoAsustadoRestante = tiempoAsustadoRestante;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public EstadoFantasma getEstado() {
        return estado;
    }

    public double getTiempoAsustadoRestante() {
        return tiempoAsustadoRestante;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public void setEstado(EstadoFantasma estado) {
        this.estado = estado;
    }

    public void setTiempoAsustadoRestante(double tiempoAsustadoRestante) {
        this.tiempoAsustadoRestante = tiempoAsustadoRestante;
    }
}

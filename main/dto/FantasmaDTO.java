package dto;

import modelo.Direccion;
import modelo.EstadoFantasma;

public class FantasmaDTO {
    private final double x;
    private final double y;
    private final Direccion direccion;
    private final EstadoFantasma estado;
    private final double tiempoAsustadoRestante;

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
}

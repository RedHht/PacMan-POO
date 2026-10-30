package dto;

import modelo.Direccion;

public class PacManDTO {
    private final double x;
    private final double y;
    private final Direccion direccion;

    public PacManDTO(double x, double y, Direccion direccion) {
        this.x = x;
        this.y = y;
        this.direccion = direccion;
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
}

package dto;

import modelo.Direccion;

public class PacManDTO {
    private double x;
    private double y;
    private Direccion direccion;

    public PacManDTO() { }

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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}

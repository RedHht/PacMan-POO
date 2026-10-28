package modelo;

public class Celda {
    private final boolean transitable;
    private Punto punto;

    public Celda(boolean transitable) {
        this.transitable = transitable;
    }

    public boolean esTransitable() {
        return transitable;
    }
    public boolean tienePunto() {
        return punto != null;
    }
    public Punto getPunto() {
        return punto;
    }

    void colocarPunto(Punto punto) {
        if (!transitable) {
            throw new IllegalStateException("Una pared no puede contener puntos");
        }
        this.punto = punto;
    }

    public Punto retirarPunto() {
        Punto retirado = punto;
        punto = null;
        return retirado;
    }

}

package modelo;

public final class Posicion {
    private final double x;
    private final double y;

    public Posicion(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public Posicion avanzar(Direccion direccion) {
        switch (direccion) {
            case ARRIBA: return new Posicion(x, y - 1);
            case ABAJO: return new Posicion(x, y + 1);
            case IZQUIERDA: return new Posicion(x - 1, y);
            case DERECHA: return new Posicion(x + 1, y);
            default: throw new IllegalStateException();
        }
    }

    public double distanciaA(Posicion otra) {
        return Math.abs(x - otra.x) + Math.abs(y - otra.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Posicion)) {
            return false;
        }
        Posicion otra = (Posicion) obj;
        return x == otra.x && y == otra.y;
    }

    @Override
    public int hashCode() {
        return 31 * (int) x + (int) y;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

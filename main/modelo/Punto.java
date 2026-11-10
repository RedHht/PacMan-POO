package modelo;

public enum Punto {
    COMUN(10),
    SUPER(50);

    private final int valor;

    Punto(int valor) {
        this.valor = valor;
    }

    public int obtenerValor() {
        return valor;
    }
}

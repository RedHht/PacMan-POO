package modelo;

public abstract class Personaje {
    public static final double VELOCIDAD = 4.0;
    private Posicion destino;
    private Posicion posicion;
    private final Posicion posicionInicial;
    private Direccion direccionActual;

    protected Personaje(Posicion posicionInicial) {
        this.posicionInicial = posicionInicial;
        this.posicion = posicionInicial;
    }

    public Posicion getPosicion() {
        return posicion;
    }
    public Posicion getPosicionInicial() {
        return posicionInicial;
    }
    public Direccion getDireccionActual() {
        return direccionActual;
    }

    protected void cambiarPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    protected void cambiarDireccion(Direccion direccion) {
        direccionActual = direccion;
    }

    protected abstract Direccion elegirDireccion(Laberinto laberinto);

    public double tiempoHastaCentro() {
        if (destino == null) {
            return 1.0 / VELOCIDAD;
        }
        return posicion.distanciaA(destino) / VELOCIDAD;
    }

    protected void desplazar(Laberinto laberinto, double deltaTiempo) {
        if (deltaTiempo == 0) {
            return;
        }

        if (destino == null) {
            Direccion direccion = elegirDireccion(laberinto);
            if (!laberinto.puedeMover(posicion, direccion)) {
                return;
            }
            cambiarDireccion(direccion);
            destino = posicion.avanzar(direccion);
        }

        double avance = VELOCIDAD * deltaTiempo;
        double distancia = posicion.distanciaA(destino);
        if (avance >= distancia) {
            cambiarPosicion(destino);
            destino = null;
            return;
        }

        double x = posicion.getX();
        double y = posicion.getY();
        switch (direccionActual) {
            case ARRIBA:
                y = Math.max(y - avance, destino.getY());
                break;
            case ABAJO:
                y = Math.min(y + avance, destino.getY());
                break;
            case IZQUIERDA:
                x = Math.max(x - avance, destino.getX());
                break;
            case DERECHA:
                x = Math.min(x + avance, destino.getX());
                break;
        }
        cambiarPosicion(new Posicion(x, y));
        if (posicion.equals(destino)) {
            destino = null;
        }
    }

    public void volverPosicionInicial() {
        destino = null;
        posicion = posicionInicial;
        direccionActual = null;
    }
}

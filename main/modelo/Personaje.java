package modelo;

public abstract class Personaje {
    public static final double VELOCIDAD = 3.0;
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

    public void cambiarDireccion(Direccion direccion) {
        direccionActual = direccion;
    }

    public double tiempoHastaCentro() {
        if (destino == null) {
            return 1.0 / VELOCIDAD;
        }
        return posicion.distanciaA(destino) / VELOCIDAD;
    }

    protected void desplazar(double deltaTiempo) {

        if (destino == null) {
            return;
        }

        double avance = VELOCIDAD * deltaTiempo;
        double distancia = posicion.distanciaA(destino);

        // Si ya llego a destino, coloca al personaje exactamente en el centro.
        if (avance >= distancia - 0.0000000001) {
            cambiarPosicion(destino);
            destino = null;
            return;
        }

        // Avanza la nueva posición
        double x = posicion.getX();
        double y = posicion.getY();
        switch (direccionActual) {
            case ARRIBA:
                y -= avance;
                break;
            case ABAJO:
                y += avance;
                break;
            case IZQUIERDA:
                x -= avance;
                break;
            case DERECHA:
                x += avance;
                break;
        }
        cambiarPosicion(new Posicion(x, y));
    }

    public void volverPosicionInicial() {
        destino = null;
        posicion = posicionInicial;
        direccionActual = null;
    }

    public Posicion getDestino() {
        return destino;
    }

    public void setDestino(Posicion destino) {
        this.destino = destino;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public void setDireccionActual(Direccion direccionActual) {
        this.direccionActual = direccionActual;
    }
}

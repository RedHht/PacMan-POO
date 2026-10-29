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

    // Abstracta para que cada implementación de Personaje pueda decidir como direccionar
    protected abstract Direccion elegirDireccion(Laberinto laberinto);

    public double tiempoHastaCentro() {
        if (destino == null) {
            return 1.0 / VELOCIDAD;
        }
        return posicion.distanciaA(destino) / VELOCIDAD;
    }

    protected void desplazar(Laberinto laberinto, double deltaTiempo) {

        // Si no tengo destino elijo uno nuevo basado en la dirección
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
}

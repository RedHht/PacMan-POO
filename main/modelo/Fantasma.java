package modelo;

import java.util.List;
import java.util.Random;

public class Fantasma extends Personaje {
    private EstadoFantasma estado = EstadoFantasma.NORMAL;
    private double tiempoAsustadoRestante;
    private final Random aleatorio;
    private Posicion objetivoPacMan;

    public Fantasma(Posicion origen) {
        super(origen);
        this.aleatorio = new Random();
    }

    public EstadoFantasma getEstado() {
        return estado;
    }
    public double getTiempoAsustadoRestante() {
        return tiempoAsustadoRestante;
    }
    public boolean estaAsustado() {
        return estado == EstadoFantasma.ASUSTADO;
    }
    public boolean estaRegresando() {
        return estado == EstadoFantasma.REGRESANDO;
    }
    public boolean estaNormal() {
        return estado == EstadoFantasma.NORMAL;
    }

    public void asustar(double duracion) {
        if (!estaRegresando()) {
            estado = EstadoFantasma.ASUSTADO;
            tiempoAsustadoRestante = duracion;
        }
    }

    public void serComido() {
        estado = EstadoFantasma.REGRESANDO;
        tiempoAsustadoRestante = 0;
    }

    private void actualizarTiempo(double deltaTiempo) {
        if (estaAsustado()) {
            tiempoAsustadoRestante -= deltaTiempo;
            if (tiempoAsustadoRestante <= 0) {
                tiempoAsustadoRestante = 0;
                estado = EstadoFantasma.NORMAL;
            }
        }
    }

    public void actualizar(Laberinto laberinto, Posicion posicionPacMan, double deltaTiempo) {

        actualizarTiempo(deltaTiempo);
        objetivoPacMan = new Posicion(Math.round(posicionPacMan.getX()),
                Math.round(posicionPacMan.getY()));

        if (estaRegresando() && getPosicion().equals(getPosicionInicial())) {
            estado = EstadoFantasma.NORMAL;
            return;
        }
        desplazar(laberinto, deltaTiempo);
        if (estaRegresando() && getPosicion().equals(getPosicionInicial())) {
            estado = EstadoFantasma.NORMAL;
        }
    }

    @Override
    protected Direccion elegirDireccion(Laberinto laberinto) {
        List<Direccion> disponibles = laberinto.direccionesDisponibles(getPosicion());
        if (disponibles.isEmpty()) {
            return null;
        }
        if (estaAsustado()) {
            return disponibles.get(aleatorio.nextInt(disponibles.size()));
        }

        Posicion objetivo = objetivoPacMan;
        if (estaRegresando()) {
            objetivo = getPosicionInicial();
        }

        Direccion elegida = disponibles.get(0);
        double menorDistancia = Double.MAX_VALUE;
        for (Direccion direccion : disponibles) {
            Posicion siguiente = getPosicion().avanzar(direccion);
            // Para perseguir alcanza con comparar la distancia directa.
            // Al regresar al origen sí buscamos un camino entre las paredes.
            double distancia = estaRegresando()
                    ? laberinto.distanciaMinima(siguiente, objetivo)
                    : siguiente.distanciaA(objetivo);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                elegida = direccion;
            }
        }
        return elegida;
    }

    @Override
    public void volverPosicionInicial() {
        super.volverPosicionInicial();
        estado = EstadoFantasma.NORMAL;
        tiempoAsustadoRestante = 0;
    }
}

package modelo;

import dto.FantasmaDTO;

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

    public FantasmaDTO toDTO() {
        return new FantasmaDTO(getPosicion().getX(), getPosicion().getY(),
                getDireccionActual(), estado, tiempoAsustadoRestante);
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

    public void actualizar(double deltaTiempo) {

        actualizarTiempo(deltaTiempo);

        if (estaRegresando() && getPosicion().equals(getPosicionInicial())) {
            estado = EstadoFantasma.NORMAL;
            return;
        }
        if (estaRegresando() && getPosicion().equals(getPosicionInicial())) {
            estado = EstadoFantasma.NORMAL;
        }

        desplazar(deltaTiempo);
    }

    @Override
    public void volverPosicionInicial() {
        super.volverPosicionInicial();
        estado = EstadoFantasma.NORMAL;
        tiempoAsustadoRestante = 0;
    }
}

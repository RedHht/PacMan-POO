package controlador;

import modelo.Direccion;
import modelo.Partida;

public final class Controlador {
    private static Controlador instance;
    private Partida partida;

    private Controlador() { }

    public static Controlador getInstance() {
        if (instance == null) {
            instance = new Controlador();
        }
        return instance;
    }

    public void iniciarPartida() {
        if (partida != null && !partida.estaFinalizada()) {
            throw new IllegalStateException("Ya existe una partida activa");
        }

        partida = new Partida();
    }

    public Partida getPartida() {
        return partida;
    }

    public void moverPacMan(Direccion direccion) {
        partida.moverPacMan(direccion);
    }

    public void pausarPartida() {
        partida.pausar();
    }
    public void reanudarPartida() {
        partida.reanudar();
    }

    public void nuevaPartida() {
        if (partida == null || !partida.estaFinalizada()) {
            throw new IllegalStateException("Solo se puede reiniciar una partida finalizada");
        }
        partida = new Partida();
    }

    public void actualizarPartida(double deltaTiempo) {
        partida.actualizar(deltaTiempo);
    }

}

package controlador;

import dto.PartidaDTO;
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

    public PartidaDTO iniciarPartida() {
        if (partida != null && !partida.estaFinalizada()) {
            throw new IllegalStateException("Ya existe una partida activa");
        }

        partida = new Partida();
        return getPartida();
    }

    public PartidaDTO getPartida() {
        if (partida == null) {
            return null;
        }
        return partida.toDTO();
    }

    public PartidaDTO moverPacMan(Direccion direccion) {
        partida.moverPacMan(direccion);
        return getPartida();
    }

    public PartidaDTO pausarPartida() {
        partida.pausar();
        return getPartida();
    }
    public PartidaDTO reanudarPartida() {
        partida.reanudar();
        return getPartida();
    }

    public PartidaDTO nuevaPartida() {
        if (partida == null || !partida.estaFinalizada()) {
            throw new IllegalStateException("Solo se puede reiniciar una partida finalizada");
        }
        partida = new Partida();
        return getPartida();
    }

    public PartidaDTO actualizarPartida(double deltaTiempo) {
        partida.actualizar(deltaTiempo);
        return getPartida();
    }

}

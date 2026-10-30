package dto;

import java.util.List;
import modelo.EstadoPartida;

public class PartidaDTO {
    private final EstadoPartida estado;
    private final int puntaje;
    private final int vidas;
    private final int puntosRestantes;
    private final PacManDTO pacMan;
    private final List<FantasmaDTO> fantasmas;
    private final List<String> tablero;

    public PartidaDTO(EstadoPartida estado, int puntaje, int vidas, int puntosRestantes,
                      PacManDTO pacMan, List<FantasmaDTO> fantasmas, List<String> tablero) {
        this.estado = estado;
        this.puntaje = puntaje;
        this.vidas = vidas;
        this.puntosRestantes = puntosRestantes;
        this.pacMan = pacMan;
        this.fantasmas = List.copyOf(fantasmas);
        this.tablero = List.copyOf(tablero);
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public int getVidas() {
        return vidas;
    }

    public int getPuntosRestantes() {
        return puntosRestantes;
    }

    public PacManDTO getPacMan() {
        return pacMan;
    }

    public List<FantasmaDTO> getFantasmas() {
        return fantasmas;
    }

    public List<String> getTablero() {
        return tablero;
    }
}

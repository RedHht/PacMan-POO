package dto;

import java.util.List;
import modelo.EstadoPartida;

public class PartidaDTO {
    private EstadoPartida estado;
    private int puntaje;
    private int vidas;
    private int puntosRestantes;
    private PacManDTO pacMan;
    private List<FantasmaDTO> fantasmas;
    private List<String> tablero;

    public PartidaDTO() { }

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

    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public void setPuntosRestantes(int puntosRestantes) {
        this.puntosRestantes = puntosRestantes;
    }

    public void setPacMan(PacManDTO pacMan) {
        this.pacMan = pacMan;
    }

    public void setFantasmas(List<FantasmaDTO> fantasmas) {
        this.fantasmas = List.copyOf(fantasmas);
    }

    public void setTablero(List<String> tablero) {
        this.tablero = List.copyOf(tablero);
    }
}

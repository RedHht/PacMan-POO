package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Partida {
    public static final double DURACION_ASUSTADO = 8.0;
    // A 4 celdas por segundo, cada personaje avanza como maximo 0.04 por intervalo.
    // Esto permite detectar el contacto sin calcular el cruce de los recorridos.
    private static final double MAX_INTERVALO = 0.01;
    private static final double DISTANCIA_COLISION = 0.3;
    private static final double DISTANCIA_CONSUMO = 0.3;

    private final Laberinto laberinto;
    private final PacMan pacMan;
    private final List<Fantasma> fantasmas;
    private EstadoPartida estado = EstadoPartida.EN_CURSO;
    private int puntaje;
    private int vidas = 3;

    public Partida() {
        this(new Laberinto(), new Posicion(5, 8),
                List.of(new Posicion(5, 5), new Posicion(4, 5),
                        new Posicion(3, 5), new Posicion(5, 4)), new Random());
    }

    public Partida(Laberinto laberinto, Posicion inicioPacMan,
                   List<Posicion> origenesFantasmas, Random aleatorio) {
        this.laberinto = laberinto;

        if (origenesFantasmas.size() != 4) {
            throw new IllegalArgumentException("La partida requiere cuatro fantasmas");
        }

        pacMan = new PacMan(inicioPacMan);
        fantasmas = new ArrayList<>();
        for (Posicion origen : origenesFantasmas) {
            fantasmas.add(new Fantasma(origen, aleatorio));
        }
        consumirPunto();
        if (!laberinto.quedanPuntos()) {
            estado = EstadoPartida.GANADA;
        }
    }

    public Laberinto getLaberinto() {
        return laberinto;
    }
    public PacMan getPacMan() {
        return pacMan;
    }
    public List<Fantasma> getFantasmas() {
        return new ArrayList<>(fantasmas);
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
    public boolean estaFinalizada() {
        return estado == EstadoPartida.GANADA || estado == EstadoPartida.PERDIDA;
    }
    public boolean estaEnCurso() {
        return estado == EstadoPartida.EN_CURSO;
    }

    public void moverPacMan(Direccion direccion) {
        if (estaEnCurso()) {
            pacMan.solicitarDireccion(direccion);
        }
    }

    public void pausar() {
        if (estaEnCurso()) {
            estado = EstadoPartida.EN_PAUSA;
        }
    }

    public void reanudar() {
        if (estado == EstadoPartida.EN_PAUSA) {
            estado = EstadoPartida.EN_CURSO;
        }
    }

    public void actualizar(double deltaTiempo) {
        if (deltaTiempo < 0) {
            throw new IllegalArgumentException("El aumento de tiempo no puede ser negativo");
        }
        double restante = deltaTiempo;

        // Para no perder colisiones intermedias, dividimos el tiempo en "tramos"
        while (restante > 0.0000000001 && estaEnCurso()) {
            double tramo = Math.min(restante, MAX_INTERVALO);

            tramo = Math.min(tramo, pacMan.tiempoHastaCentro());
            for (Fantasma fantasma : fantasmas) {
                tramo = Math.min(tramo, fantasma.tiempoHastaCentro());
            }

            pacMan.actualizar(laberinto, tramo);
            consumirPunto();
            boolean vidaPerdida = false;
            for (Fantasma fantasma : fantasmas) {
                boolean regresando = fantasma.estaRegresando();
                fantasma.actualizar(laberinto, pacMan.getPosicion(), tramo);
                if (!regresando && resolverColision(fantasma)) {
                    vidaPerdida = true;
                    break;
                }
            }
            if (!vidaPerdida && !laberinto.quedanPuntos()) {
                estado = EstadoPartida.GANADA;
            }
            restante -= tramo;
        }
    }

    private void consumirPunto() {
        Posicion posicionPacMan = pacMan.getPosicion();
        Posicion centroCelda = new Posicion(Math.round(posicionPacMan.getX()),
                Math.round(posicionPacMan.getY()));

        if (posicionPacMan.distanciaA(centroCelda) > DISTANCIA_CONSUMO) {
            return;
        }

        Punto punto = laberinto.retirarPunto(centroCelda);
        if (punto == null) {
            return;
        }
        puntaje += punto.obtenerValor();
        if (punto instanceof SuperPunto)
            for (Fantasma fantasma : fantasmas) {
                fantasma.asustar(DURACION_ASUSTADO);
            }
    }

    // NOTA: Devuelve true si Pac-Man pierde una vida.
    private boolean resolverColision(Fantasma fantasma) {
        double distancia = pacMan.getPosicion().distanciaA(fantasma.getPosicion());

        if (distancia > DISTANCIA_COLISION) { // No hay colisión
            return false;
        }

        // Hay colisión pero no se pierde vida
        if (fantasma.estaAsustado()) {  // Con un fantasma asustado
            fantasma.serComido();
            puntaje += 200;
            return false;
        }
        if (fantasma.estaRegresando()) { // Con un par de ojos
            return false;
        }

        // Hay colisión con perdida de vida
        vidas--;

        if (vidas == 0) { // Si no quedan vidas, perdio
            estado = EstadoPartida.PERDIDA;
        }
        else { // Si quedan vidas volvemos a posiciones iniciales
            pacMan.volverPosicionInicial();
            for (Fantasma otro : fantasmas) {
                otro.volverPosicionInicial();
            }
        }

        return true;
    }
}

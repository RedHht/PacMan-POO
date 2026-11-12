package modelo;

import dto.PartidaDTO;
import dto.FantasmaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Partida {

    public static final double DURACION_ASUSTADO = 8.0;
    // Pasos cortos para detectar encuentros mientras los personajes se mueven.
    private static final double MAX_INTERVALO = 0.05;

    private final Laberinto laberinto;
    private final PacMan pacMan;
    private final List<Fantasma> fantasmas;
    private EstadoPartida estado = EstadoPartida.EN_CURSO;
    private int puntaje;
    private int vidas = 3;

    public Partida() {
        this(new Laberinto(), new Posicion(7, 11),
                List.of(new Posicion(7, 5), new Posicion(6, 5),
                        new Posicion(8, 5), new Posicion(7, 4)));
    }

    public Partida(Laberinto laberinto, Posicion inicioPacMan,
                   List<Posicion> origenesFantasmas) {
        this.laberinto = laberinto;
        pacMan = new PacMan(inicioPacMan);
        fantasmas = new ArrayList<>();
        for (Posicion origen : origenesFantasmas) {
            fantasmas.add(new Fantasma(origen));
        }
        consumirPunto();
        if (!laberinto.quedanPuntos()) {
            estado = EstadoPartida.GANADA;
        }
    }

    public PartidaDTO toDTO() {
        List<FantasmaDTO> datosFantasmas = new ArrayList<>();
        for (Fantasma fantasma : fantasmas) {
            datosFantasmas.add(fantasma.toDTO());
        }
        return new PartidaDTO(estado, puntaje, vidas, laberinto.getPuntosRestantes(),
                pacMan.toDTO(), datosFantasmas, laberinto.getTablero());
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

    public Direccion elegirDireccionPacman() {
        if (pacMan.getDireccionSolicitada() != null && laberinto.puedeMover(pacMan.getPosicion(), pacMan.getDireccionSolicitada())) {
            pacMan.cambiarDireccion(pacMan.getDireccionSolicitada());
            pacMan.setDireccionSolicitada(null);
        }
        return pacMan.getDireccionActual();
    }

    public void desplazarPacman() {
        if (pacMan.getDestino() == null) {
            Direccion direccion = elegirDireccionPacman();

            if (!laberinto.puedeMover(pacMan.getPosicion(), direccion)) {
                return;
            }

            pacMan.cambiarDireccion(direccion);
            pacMan.setDestino(pacMan.getPosicion().avanzar(direccion));;
        }
    }

    public Direccion elegirDireccionFantasma(Fantasma fantasma) {

        Random aleatorio = new Random();
        List<Direccion> disponibles = laberinto.direccionesDisponibles(fantasma.getPosicion());
        if (disponibles.isEmpty()) {
            return null;
        }
        if (fantasma.estaAsustado()) {
            return disponibles.get(aleatorio.nextInt(disponibles.size()));
        }

        Posicion objetivo = new Posicion(Math.round(pacMan.getPosicion().getX()),
                Math.round(pacMan.getPosicion().getY()));

        if (fantasma.estaRegresando()) {
            objetivo = fantasma.getPosicionInicial();
        }

        Direccion elegida = disponibles.get(0);
        int menorDistancia = Integer.MAX_VALUE;
        for (Direccion direccion : disponibles) {
            Posicion siguiente = fantasma.getPosicion().avanzar(direccion);
            int distancia = laberinto.distanciaMinima(siguiente, objetivo);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                elegida = direccion;
            }
        }
        return elegida;
    }

    public void desplazarFantasma(Fantasma fantasma) {
        if (fantasma.getDestino() == null) {
            Direccion direccion = elegirDireccionFantasma(fantasma);
            if (!laberinto.puedeMover(fantasma.getPosicion(), direccion)) {
                return;
            }
            fantasma.cambiarDireccion(direccion);
            Posicion destino = fantasma.getPosicion().avanzar(direccion);
            fantasma.setDestino(fantasma.getPosicion().avanzar(direccion));;
        }
    }

    // En el futuro esto va a ser llamado de forma regular por un timer.
    public void actualizar(double deltaTiempo) {
        double restante = deltaTiempo;

        // Si no esta pausada y queda tiempo para procesar
        while (restante > 0 && estaEnCurso()) {

            // Agarramos un pedazo del tiempo (Sub-stepping) para que no se salten colisiones o puntos y si gano.
            double tramo = Math.min(restante, MAX_INTERVALO);
            tramo = Math.min(tramo, pacMan.tiempoHastaCentro());
            for (Fantasma fantasma : fantasmas) {
                tramo = Math.min(tramo, fantasma.tiempoHastaCentro());
            }

            // Para cada pedazo de tiempo que tomamos, calculamos colisiones, consumo, etc.
            Posicion anteriorPacMan = pacMan.getPosicion();
            desplazarPacman();
            pacMan.actualizar(tramo);
            consumirPunto();
            boolean vidaPerdida = false;
            for (Fantasma fantasma : fantasmas) {
                Posicion anteriorFantasma = fantasma.getPosicion();
                boolean regresando = fantasma.estaRegresando();
                desplazarFantasma(fantasma);
                fantasma.actualizar(tramo);
                if (!regresando && resolverColision(fantasma, anteriorPacMan, anteriorFantasma)) {
                    vidaPerdida = true;
                    break;
                }
            }
            if (!vidaPerdida && !laberinto.quedanPuntos()) {
                estado = EstadoPartida.GANADA;
            }

            // Restamos el step que usamos al tiempo restante
            restante -= tramo;
        }
    }

    private void consumirPunto() {
        Posicion posicionPacMan = pacMan.getPosicion();
        if (!laberinto.estaAlineado(posicionPacMan)) {
            return;
        }

        Punto punto = laberinto.retirarPunto(posicionPacMan);
        if (punto == null) {
            return;
        }

        puntaje += punto.obtenerValor();

        if (punto == Punto.SUPER) {
            for (Fantasma fantasma : fantasmas) {
                fantasma.asustar(DURACION_ASUSTADO);
            }
        }
    }

    private Posicion celdaMasCercana(Posicion posicion) {
        return new Posicion(Math.round(posicion.getX()), Math.round(posicion.getY()));
    }

    // Devuelve true si Pac-Man pierde una vida.
    private boolean resolverColision(Fantasma fantasma, Posicion anteriorPacMan,
                                    Posicion anteriorFantasma) {
        Posicion celdaPacMan = celdaMasCercana(pacMan.getPosicion());
        Posicion celdaFantasma = celdaMasCercana(fantasma.getPosicion());
        boolean mismaCelda = celdaPacMan.equals(celdaFantasma);
        boolean seCruzaron = celdaMasCercana(anteriorPacMan).equals(celdaFantasma)
                && celdaMasCercana(anteriorFantasma).equals(celdaPacMan);

        // Caso donde no hay colisión
        if (!mismaCelda && !seCruzaron) {
            return false;
        }

        // Casos donde hay colisión sin perdida de vida
        if (fantasma.estaAsustado()) { // Comer fantasma
            fantasma.serComido();
            puntaje += 200;
            return false;
        }
        if (fantasma.estaRegresando()) { // Colisión con par de ojos
            return false;
        }

        // Casos donde hay colisión CON perdida de vida
        vidas--;

        if (vidas == 0) { // Se queda sin vidas
            estado = EstadoPartida.PERDIDA;
        }
        else { // Se pierde la vida pero quedan mas
            pacMan.volverPosicionInicial();
            for (Fantasma otro : fantasmas) {
                otro.volverPosicionInicial();
            }
        }

        return true;
    }
}

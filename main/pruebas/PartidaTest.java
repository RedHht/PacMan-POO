package pruebas;

import controlador.Controlador;
import modelo.Direccion;
import modelo.EstadoPartida;
import modelo.Fantasma;
import modelo.Laberinto;
import modelo.PacMan;
import modelo.Partida;
import modelo.Posicion;
import modelo.PuntoComun;
import modelo.SuperPunto;

import java.util.List;
import java.util.Random;

public class PartidaTest {
    private static final String[] MAPA = {
        "#####", "#o.o#", "#...#", "#o.o#", "#####"
    };

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void main(String[] args) {
        puntosDelMapa();
        inicioPuntosYMovimiento();
        consumoConMargen();
        puntosDuranteRecorrido();
        movimientoYGiro();
        llegadaAlCentro();
        tiempoDividido();
        pausaYTemporizador();
        finEstadoAsustado();
        comerFantasma();
        recuperacionEnOrigen();
        regresoAlOrigen();
        colisionDeFrente();
        colisionYDerrota();
        victoria();
        controlador();
        System.out.println("Todas las pruebas pasaron");
    }

    private static void finEstadoAsustado() {
        Laberinto laberinto = new Laberinto(MAPA);
        Fantasma fantasma = new Fantasma(new Posicion(3, 3), new Random(1));
        fantasma.asustar(8);
        for (int i = 0; i < 799; i++) {
            fantasma.actualizar(laberinto, new Posicion(1, 1), 0.01);
        }
        verificar(fantasma.estaAsustado(), "No debe terminar el estado asustado antes de tiempo");
        fantasma.actualizar(laberinto, new Posicion(1, 1), 0.01);
        fantasma.actualizar(laberinto, new Posicion(1, 1), 0.01);
        verificar(fantasma.estaNormal() && fantasma.getTiempoAsustadoRestante() == 0,
                "El fantasma no volvio al estado normal");

    }

    private static void llegadaAlCentro() {
        Laberinto laberinto = new Laberinto(MAPA);
        PacMan pacMan = new PacMan(new Posicion(1, 1));
        pacMan.solicitarDireccion(Direccion.DERECHA);
        pacMan.actualizar(laberinto, 0.249999);
        verificar(pacMan.getPosicion().getX() < 2,
                "No debe adelantar la llegada al centro");
        pacMan.actualizar(laberinto, 0.000001);
        pacMan.actualizar(laberinto, 0.01);
        verificar(pacMan.getPosicion().equals(new Posicion(2, 1)),
                "Pac-Man no quedo en el centro de la celda");
    }

    private static void puntosDelMapa() {
        Laberinto laberinto = new Laberinto("#####", "#...#", "#.o.#", "#...#", "#####");
        verificar(laberinto.retirarPunto(new Posicion(1, 1)) instanceof PuntoComun,
                "Una esquina con '.' debe contener un punto comun");
        verificar(laberinto.retirarPunto(new Posicion(2, 2)) instanceof SuperPunto,
                "El simbolo 'o' debe colocar el superpunto donde indica el mapa");
        verificar(laberinto.getPuntosRestantes() == 7,
                "Los dos tipos de punto deben contarse una sola vez");
    }

    private static void consumoConMargen() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        Posicion punto = new Posicion(2, 1);
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.17);
        verificar(partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "Comio el punto antes de entrar en el radio de 0.3");
        partida.actualizar(0.01);
        verificar(!partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "No comio el punto al acercarse a su centro");
        int puntaje = partida.getPuntaje();
        int restantes = partida.getLaberinto().getPuntosRestantes();
        partida.actualizar(0.01);
        verificar(partida.getPuntaje() == puntaje
                && partida.getLaberinto().getPuntosRestantes() == restantes,
                "El mismo punto se conto dos veces");
    }

    private static Partida crearPartida(Posicion pacMan, Posicion fantasma) {
        return new Partida(new Laberinto(MAPA), pacMan,
                List.of(fantasma, fantasma, fantasma, fantasma), new Random(1));
    }

    private static void inicioPuntosYMovimiento() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        verificar(partida.getVidas() == 3 && partida.getPuntaje() == 50,
                "Se esperaban 3 vidas y 50 puntos al empezar sobre un superpunto");
        verificar(partida.getLaberinto().getPuntosRestantes() == 8,
                "El punto inicial sigue en el mapa");
        for (Fantasma fantasma : partida.getFantasmas()) {
            verificar(fantasma.estaAsustado(), "Un fantasma no quedo asustado");
        }
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.25);
        verificar(partida.getPacMan().getPosicion().distanciaA(new Posicion(2, 1)) < 0.000000001,
                "Pac-Man no avanzo una celda");
        verificar(partida.getPuntaje() == 60, "El punto comun no sumo 10 puntos");
        verificar(!partida.getLaberinto().puedeMover(new Posicion(1, 1), Direccion.IZQUIERDA),
                "Se permite avanzar hacia una pared");
    }

    private static void pausaYTemporizador() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        double restante = partida.getFantasmas().get(0).getTiempoAsustadoRestante();
        partida.pausar();
        partida.actualizar(20);
        verificar(partida.getEstado() == EstadoPartida.EN_PAUSA
                && partida.getFantasmas().get(0).getTiempoAsustadoRestante() == restante,
                "La pausa debe congelar el estado y los temporizadores");
        partida.reanudar();
        partida.actualizar(0.1);
        verificar(partida.getFantasmas().get(0).getTiempoAsustadoRestante() < restante,
                "El temporizador debe continuar al reanudar");
    }

    private static void colisionYDerrota() {
        Partida partida = crearPartida(new Posicion(2, 2), new Posicion(2, 3));
        partida.actualizar(0.25);
        verificar(partida.getVidas() == 2
                && partida.getPacMan().getPosicion().equals(new Posicion(2, 2)),
                "La colision normal debe descontar una vida y reiniciar posiciones");
        for (Fantasma fantasma : partida.getFantasmas()) {
            verificar(fantasma.estaNormal(), "La colision debe restablecer cada estado");
        }
        partida.actualizar(2 * 0.25);
        verificar(partida.getEstado() == EstadoPartida.PERDIDA,
                "No hubo derrota al perder las 3 vidas");
    }

    private static void comerFantasma() {
        Partida partida = new Partida(new Laberinto(MAPA), new Posicion(1, 1),
                List.of(new Posicion(1, 1), new Posicion(3, 3),
                        new Posicion(3, 3), new Posicion(3, 3)), new Random(1));
        int puntajeInicial = partida.getPuntaje();
        partida.actualizar(0.01);
        verificar(partida.getPuntaje() == puntajeInicial + 200, "El fantasma comido no sumo 200 puntos");
        verificar(partida.getVidas() == 3, "Comer un fantasma desconto una vida");
    }

    private static void recuperacionEnOrigen() {
        Fantasma fantasma = new Fantasma(new Posicion(2, 1), new Random(1));
        fantasma.asustar(8);
        fantasma.serComido();
        fantasma.actualizar(new Laberinto(MAPA), new Posicion(1, 1), 0.01);
        verificar(fantasma.estaNormal() && fantasma.getPosicion().equals(new Posicion(2, 1)),
                "Un fantasma comido en su origen debe recuperarse sin desplazarse");

    }

    private static void regresoAlOrigen() {
        Laberinto laberinto = new Laberinto(MAPA);
        Fantasma retorno = new Fantasma(new Posicion(3, 3), new Random(1));
        retorno.actualizar(laberinto, new Posicion(1, 1), 0.25);
        retorno.asustar(8);
        retorno.serComido();
        verificar(retorno.estaRegresando(), "El fantasma comido debe regresar al origen");
        retorno.actualizar(laberinto, new Posicion(1, 1), 0.25);
        verificar(retorno.estaNormal()
                && retorno.getPosicion().equals(new Posicion(3, 3)),
                "Al llegar al origen debe recuperar el estado normal");
    }

    private static void victoria() {
        Laberinto laberinto = new Laberinto(MAPA);
        Partida partida = new Partida(laberinto, new Posicion(1, 1),
                List.of(new Posicion(3, 3), new Posicion(3, 3),
                        new Posicion(3, 3), new Posicion(3, 3)), new Random(1));
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 3; x++) {
                if (x != 2 || y != 1) {
                    laberinto.retirarPunto(new Posicion(x, y));
                }
            }
        }
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.25);
        verificar(partida.getEstado() == EstadoPartida.GANADA
                && laberinto.getPuntosRestantes() == 0,
                "Al consumir todos los puntos la partida debe terminar en victoria");
    }

    private static void puntosDuranteRecorrido() {
        Partida recorrido = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        recorrido.moverPacMan(Direccion.DERECHA);
        recorrido.actualizar(0.5);
        verificar(!recorrido.getLaberinto().obtenerCelda(new Posicion(2, 1)).tienePunto()
                && !recorrido.getLaberinto().obtenerCelda(new Posicion(3, 1)).tienePunto(),
                "Quedaron puntos sin comer en el recorrido");
        Posicion antes = recorrido.getPacMan().getPosicion();
        recorrido.pausar();
        recorrido.actualizar(1);
        verificar(recorrido.getPacMan().getPosicion().equals(antes),
                "Pac-Man se movio durante la pausa");
    }

    private static void movimientoYGiro() {
        Laberinto laberinto = new Laberinto(MAPA);
        PacMan pacMan = new PacMan(new Posicion(1, 1));
        pacMan.solicitarDireccion(Direccion.DERECHA);
        pacMan.actualizar(laberinto, 0.1);
        verificar(Math.abs(pacMan.getPosicion().getX() - 1.4) < 0.000000001,
                "El desplazamiento debe ser proporcional al tiempo");
        pacMan.solicitarDireccion(Direccion.ABAJO);
        pacMan.actualizar(laberinto, 0.15);
        verificar(pacMan.getPosicion().equals(new Posicion(2, 1)),
                "Debe llegar al centro antes de girar");
        pacMan.actualizar(laberinto, 0.1);
        verificar(Math.abs(pacMan.getPosicion().getY() - 1.4) < 0.000000001,
                "Debe aplicar el giro solicitado al alcanzar el centro");
        pacMan.actualizar(laberinto, 5);
        verificar(pacMan.getPosicion().equals(new Posicion(2, 2)),
                "Pac-Man se paso de su destino");
        for (int i = 0; i < 50; i++) {
            pacMan.actualizar(laberinto, 0.01);
        }
        verificar(pacMan.getPosicion().equals(new Posicion(2, 3)),
                "Pac-Man no se detuvo ante la pared");
    }

    private static void tiempoDividido() {
        Partida completa = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        Partida dividida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        completa.moverPacMan(Direccion.DERECHA);
        dividida.moverPacMan(Direccion.DERECHA);
        completa.actualizar(0.35);
        for (int i = 0; i < 35; i++) {
            dividida.actualizar(0.01);
        }
        verificar(completa.getPacMan().getPosicion().distanciaA(
                dividida.getPacMan().getPosicion()) < 0.000000001,
                "El desplazamiento cambio al dividir el tiempo");
    }

    private static void colisionDeFrente() {
        Partida partida = crearPartida(new Posicion(2, 2), new Posicion(3, 2));
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.1);
        verificar(partida.getVidas() == 2,
                "Debe detectar a Pac-Man y un fantasma que avanzan uno hacia el otro");
    }

    private static void controlador() {
        Controlador controlador = Controlador.getInstance();
        verificar(controlador == Controlador.getInstance(),
                "getInstance debe devolver siempre el mismo controlador");
        controlador.iniciarPartida();
        verificar(controlador.getPartida() != null
                && controlador.getPartida().getFantasmas().size() == 4,
                "El controlador debe iniciar la partida con cuatro fantasmas");
        controlador.pausarPartida();
        verificar(controlador.getPartida().getEstado() == EstadoPartida.EN_PAUSA,
                "El controlador debe pausar la partida");
        controlador.reanudarPartida();
        verificar(controlador.getPartida().getEstado() == EstadoPartida.EN_CURSO,
                "El controlador debe reanudar la partida");
        Posicion inicio = controlador.getPartida().getPacMan().getPosicion();
        controlador.moverPacMan(Direccion.ARRIBA);
        controlador.actualizarPartida(0.01);
        verificar(controlador.getPartida().getPacMan().getPosicion().getY() < inicio.getY(),
                "El personaje no se movio desde el controlador");
    }
}

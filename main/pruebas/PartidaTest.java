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

public class PartidaTest {
    private static final String[] MAPA = {
        "#####", "#o.o#", "#...#", "#o.o#", "#####"
    };

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static Partida crearPartida(Posicion posicionPacMan, Posicion posicionFantasma) {
        return new Partida(new Laberinto(MAPA), posicionPacMan,
                List.of(posicionFantasma, posicionFantasma,
                        posicionFantasma, posicionFantasma));
    }

    public static void main(String[] args) {
        probarPuntosDelMapa();
        probarInicio();
        probarMovimiento();
        probarConsumoDePuntos();
        probarRecorridoLargo();
        probarPausa();
        probarEstadosDelFantasma();
        probarPersecucionFantasma();
        probarColisiones();
        probarComerFantasma();
        probarVictoria();
        probarControlador();
        System.out.println("Todas las pruebas pasaron");
    }

    private static void probarPuntosDelMapa() {
        Laberinto laberinto = new Laberinto(MAPA);
        verificar(laberinto.getPuntosRestantes() == 9, "El mapa debe tener 9 puntos");
        verificar(laberinto.retirarPunto(new Posicion(2, 1)) instanceof PuntoComun,
                "La celda con '.' debe tener un punto comun");
        verificar(laberinto.retirarPunto(new Posicion(1, 1)) instanceof SuperPunto,
                "La celda con 'o' debe tener un superpunto");
        verificar(laberinto.getPuntosRestantes() == 7, "Los puntos no se retiraron del mapa");
    }

    private static void probarInicio() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        verificar(partida.getVidas() == 3, "La partida debe empezar con 3 vidas");
        verificar(partida.getPuntaje() == 50, "El superpunto inicial debe sumar 50");
        verificar(partida.getFantasmas().size() == 4, "Debe haber 4 fantasmas");
        for (Fantasma fantasma : partida.getFantasmas()) {
            verificar(fantasma.estaAsustado(), "El superpunto debe asustar a los fantasmas");
        }
    }

    private static void probarMovimiento() {
        Laberinto laberinto = new Laberinto(MAPA);
        PacMan pacMan = new PacMan(new Posicion(1, 1));

        pacMan.solicitarDireccion(Direccion.IZQUIERDA);
        pacMan.actualizar(laberinto, 0.25);
        verificar(pacMan.getPosicion().equals(new Posicion(1, 1)),
                "Pac-Man atraveso una pared");

        pacMan.solicitarDireccion(Direccion.DERECHA);
        pacMan.actualizar(laberinto, 0.25);
        verificar(pacMan.getPosicion().equals(new Posicion(2, 1)),
                "Pac-Man no avanzo una celda");

        pacMan.solicitarDireccion(Direccion.ABAJO);
        pacMan.actualizar(laberinto, 0.25);
        verificar(pacMan.getPosicion().equals(new Posicion(2, 2)),
                "Pac-Man no giro hacia abajo");
    }

    private static void probarConsumoDePuntos() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(1, 3));
        Posicion punto = new Posicion(2, 1);
        partida.moverPacMan(Direccion.DERECHA);

        partida.actualizar(0.20);
        verificar(partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "Pac-Man comio el punto antes de llegar al centro");

        partida.actualizar(0.05);
        verificar(!partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "Pac-Man no comio el punto al llegar al centro");
        verificar(partida.getPuntaje() == 60, "El punto comun debe sumar 10");
    }

    private static void probarRecorridoLargo() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(1, 3));
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.5);
        verificar(partida.getPuntaje() == 110,
                "Pac-Man debe comer los dos puntos que atraviesa");
    }

    private static void probarPausa() {
        Partida partida = crearPartida(new Posicion(1, 1), new Posicion(3, 3));
        partida.moverPacMan(Direccion.DERECHA);
        partida.pausar();
        partida.actualizar(2);

        verificar(partida.getEstado() == EstadoPartida.EN_PAUSA, "La partida no se pauso");
        verificar(partida.getPacMan().getPosicion().equals(new Posicion(1, 1)),
                "Pac-Man se movio durante la pausa");
        verificar(partida.getFantasmas().get(0).getTiempoAsustadoRestante() == 8,
                "El tiempo del fantasma avanzo durante la pausa");

        partida.reanudar();
        partida.actualizar(0.25);
        verificar(Math.round(partida.getPacMan().getPosicion().getX()) == 2,
                "Pac-Man no se movio al reanudar");
    }

    private static void probarEstadosDelFantasma() {
        Laberinto laberinto = new Laberinto(MAPA);
        Fantasma fantasma = new Fantasma(new Posicion(3, 3));
        Posicion pacMan = new Posicion(1, 1);

        fantasma.asustar(8);
        fantasma.actualizar(laberinto, pacMan, 7.9);
        verificar(fantasma.estaAsustado(), "El susto termino antes de tiempo");
        fantasma.actualizar(laberinto, pacMan, 0.2);
        verificar(fantasma.estaNormal(), "El fantasma no volvio a la normalidad");

        fantasma.volverPosicionInicial();
        fantasma.actualizar(laberinto, pacMan, 0.25);
        fantasma.asustar(8);
        fantasma.serComido();
        fantasma.actualizar(laberinto, pacMan, 0.25);
        verificar(fantasma.estaNormal(), "El fantasma no se recupero al llegar al origen");
        verificar(fantasma.getPosicion().equals(new Posicion(3, 3)),
                "El fantasma no regreso a su origen");
    }

    private static void probarPersecucionFantasma() {
        Laberinto laberinto = new Laberinto();
        Fantasma fantasma = new Fantasma(new Posicion(3, 5));
        Posicion pacMan = new Posicion(5, 8);

        for (int i = 0; i < 20 && !fantasma.getPosicion().equals(pacMan); i++) {
            fantasma.actualizar(laberinto, pacMan, 0.25);
        }
        verificar(fantasma.getPosicion().equals(pacMan),
                "El fantasma no encontro el camino hacia Pac-Man");
    }

    private static void probarColisiones() {
        Partida partida = crearPartida(new Posicion(2, 2), new Posicion(2, 3));
        partida.actualizar(0.25);
        verificar(partida.getVidas() == 2, "La colision debe quitar una vida");
        verificar(partida.getPacMan().getPosicion().equals(new Posicion(2, 2)),
                "Pac-Man no volvio al inicio");
        partida.actualizar(0.5);
        verificar(partida.getEstado() == EstadoPartida.PERDIDA,
                "La partida debe terminar al perder las 3 vidas");

        Partida cruce = crearPartida(new Posicion(2, 2), new Posicion(3, 2));
        cruce.moverPacMan(Direccion.DERECHA);
        cruce.actualizar(0.15);
        verificar(cruce.getVidas() == 2, "No se detecto el choque de frente");
    }

    private static void probarComerFantasma() {
        Partida partida = new Partida(new Laberinto(MAPA), new Posicion(1, 1),
                List.of(new Posicion(1, 1), new Posicion(3, 3),
                        new Posicion(3, 3), new Posicion(3, 3)));
        partida.actualizar(0.01);
        verificar(partida.getPuntaje() == 250, "Comer un fantasma debe sumar 200");
        verificar(partida.getVidas() == 3, "Comer un fantasma no debe quitar una vida");
    }

    private static void probarVictoria() {
        Laberinto laberinto = new Laberinto("#######", "#..#..#", "#######");
        laberinto.retirarPunto(new Posicion(4, 1));
        laberinto.retirarPunto(new Posicion(5, 1));
        Partida partida = new Partida(laberinto, new Posicion(1, 1),
                List.of(new Posicion(5, 1), new Posicion(5, 1),
                        new Posicion(5, 1), new Posicion(5, 1)));
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.25);
        verificar(partida.getEstado() == EstadoPartida.GANADA,
                "La partida debe terminar al comer el ultimo punto");
    }

    private static void probarControlador() {
        Controlador controlador = Controlador.getInstance();
        controlador.iniciarPartida();
        verificar(controlador.getPartida().getFantasmas().size() == 4,
                "El controlador no creo los fantasmas");
        controlador.pausarPartida();
        verificar(controlador.getPartida().getEstado() == EstadoPartida.EN_PAUSA,
                "El controlador no pauso la partida");
        controlador.reanudarPartida();
        verificar(controlador.getPartida().getEstado() == EstadoPartida.EN_CURSO,
                "El controlador no reanudo la partida");
    }
}

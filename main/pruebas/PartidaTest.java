package pruebas;

import controlador.Controlador;
import modelo.Direccion;
import modelo.EstadoPartida;
import modelo.Fantasma;
import modelo.Laberinto;
import modelo.PacMan;
import modelo.Partida;
import modelo.Personaje;
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
        inicioPuntosYMovimiento();
        pausaYTemporizador();
        colisionYDerrota();
        fantasmaComidoYRegreso();
        victoria();
        controlador();
        colisionDeFrente();
        movimientoContinuo();
        consumoConMargen();
        puntosDelMapa();
        System.out.println("Todas las pruebas pasaron");
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
        Partida partida = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
        Posicion punto = new Posicion(2, 1);
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar(0.17);
        verificar(partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "No debe consumir el punto a mas de 0.3 celdas");
        partida.actualizar(0.01);
        verificar(!partida.getLaberinto().obtenerCelda(punto).tienePunto(),
                "Debe consumir el punto al entrar en el margen, antes de llegar al centro");
        int puntaje = partida.getPuntaje();
        int restantes = partida.getLaberinto().getPuntosRestantes();
        partida.actualizar(0.01);
        verificar(partida.getPuntaje() == puntaje
                && partida.getLaberinto().getPuntosRestantes() == restantes,
                "No debe consumir el mismo punto dos veces");
    }

    private static Partida nuevaPartida(Posicion pacMan, Posicion fantasma) {
        return new Partida(new Laberinto(MAPA), pacMan,
                List.of(fantasma, fantasma, fantasma, fantasma), new Random(1));
    }

    private static void inicioPuntosYMovimiento() {
        Partida partida = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
        verificar(partida.getVidas() == 3 && partida.getPuntaje() == 50,
                "El inicio debe otorgar tres vidas y consumir el super punto inicial");
        verificar(partida.getLaberinto().getPuntosRestantes() == 8,
                "El punto consumido debe desaparecer");
        for (Fantasma fantasma : partida.getFantasmas()) {
            verificar(fantasma.estaAsustado(), "El super punto debe asustar a cada fantasma");
        }
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar((1.0 / Personaje.VELOCIDAD));
        verificar(partida.getPacMan().getPosicion().equals(new Posicion(2, 1)),
                "Pac-Man debe avanzar sin recibir una orden por cada paso");
        verificar(partida.getPuntaje() >= 60, "Un punto comun suma diez");
        verificar(!partida.getLaberinto().puedeMover(new Posicion(1, 1), Direccion.IZQUIERDA),
                "Una pared debe bloquear el movimiento");
    }

    private static void pausaYTemporizador() {
        Partida partida = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
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
        Partida partida = nuevaPartida(new Posicion(2, 2), new Posicion(2, 3));
        partida.actualizar((1.0 / Personaje.VELOCIDAD));
        verificar(partida.getVidas() == 2
                && partida.getPacMan().getPosicion().equals(new Posicion(2, 2)),
                "La colision normal debe descontar una vida y reiniciar posiciones");
        for (Fantasma fantasma : partida.getFantasmas()) {
            verificar(fantasma.estaNormal(), "La colision debe restablecer cada estado");
        }
        partida.actualizar(2 * (1.0 / Personaje.VELOCIDAD));
        verificar(partida.getEstado() == EstadoPartida.PERDIDA,
                "La tercera colision debe finalizar la partida");
    }

    private static void fantasmaComidoYRegreso() {
        Partida partida = nuevaPartida(new Posicion(1, 1), new Posicion(2, 1));
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar((1.0 / Personaje.VELOCIDAD));
        verificar(partida.getPuntaje() >= 260 && partida.getVidas() == 3,
                "Comer un fantasma asustado debe sumar 200 sin restar vidas");
        Fantasma fantasma = new Fantasma(new Posicion(2, 1), new Random(1));
        fantasma.asustar(8);
        fantasma.serComido();
        fantasma.actualizar(new Laberinto(MAPA), new Posicion(1, 1), 0.01);
        verificar(fantasma.estaNormal() && fantasma.getPosicion().equals(new Posicion(2, 1)),
                "Un fantasma comido en su origen debe recuperarse sin desplazarse");

        Laberinto laberinto = new Laberinto(MAPA);
        Fantasma retorno = new Fantasma(new Posicion(3, 3), new Random(1));
        retorno.actualizar(laberinto, new Posicion(1, 1), 1.0 / Personaje.VELOCIDAD);
        retorno.asustar(8);
        retorno.serComido();
        verificar(retorno.estaRegresando(), "El fantasma comido debe regresar al origen");
        retorno.actualizar(laberinto, new Posicion(1, 1), 1.0 / Personaje.VELOCIDAD);
        verificar(retorno.estaNormal()
                && retorno.getPosicion().equals(new Posicion(3, 3)),
                "Al llegar al origen debe recuperar el estado normal");
    }

    private static void victoria() {
        Laberinto laberinto = new Laberinto(MAPA);
        Partida partida = new Partida(laberinto, new Posicion(1, 1),
                List.of(new Posicion(3, 3), new Posicion(3, 3),
                        new Posicion(3, 3), new Posicion(3, 3)), new Random(1));
        for (int y = 1; y <= 3; y++)
            for (int x = 1; x <= 3; x++) {
                if (x != 2 || y != 1) {
                    laberinto.retirarPunto(new Posicion(x, y));
                }
            }
        partida.moverPacMan(Direccion.DERECHA);
        partida.actualizar((1.0 / Personaje.VELOCIDAD));
        verificar(partida.getEstado() == EstadoPartida.GANADA
                && laberinto.getPuntosRestantes() == 0,
                "Al consumir todos los puntos la partida debe terminar en victoria");
    }

    private static void movimientoContinuo() {
        Partida recorrido = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
        recorrido.moverPacMan(Direccion.DERECHA);
        recorrido.actualizar(0.5);
        verificar(!recorrido.getLaberinto().obtenerCelda(new Posicion(2, 1)).tienePunto()
                && !recorrido.getLaberinto().obtenerCelda(new Posicion(3, 1)).tienePunto(),
                "Una actualizacion larga debe consumir cada punto del recorrido");
        Posicion antes = recorrido.getPacMan().getPosicion();
        recorrido.pausar();
        recorrido.actualizar(1);
        verificar(recorrido.getPacMan().getPosicion().equals(antes),
                "La pausa debe detener el movimiento continuo");
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
                "Una llamada debe detenerse en el destino sin pasarlo");
        for (int i = 0; i < 50; i++) {
            pacMan.actualizar(laberinto, 0.01);
        }
        verificar(pacMan.getPosicion().equals(new Posicion(2, 3)),
                "Las siguientes llamadas deben avanzar y detenerse ante la pared");
        Partida completa = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
        Partida dividida = nuevaPartida(new Posicion(1, 1), new Posicion(3, 3));
        completa.moverPacMan(Direccion.DERECHA);
        dividida.moverPacMan(Direccion.DERECHA);
        completa.actualizar(0.35);
        for (int i = 0; i < 35; i++) {
            dividida.actualizar(0.01);
        }
        verificar(completa.getPacMan().getPosicion().distanciaA(
                dividida.getPacMan().getPosicion()) < 0.000000001,
                "Partida debe conservar el desplazamiento al dividir el tiempo");
    }

    private static void colisionDeFrente() {
        Partida partida = nuevaPartida(new Posicion(2, 2), new Posicion(3, 2));
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
                "El controlador debe delegar la actualizacion hasta el personaje");
    }
}

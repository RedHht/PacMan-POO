package pruebas;

import controlador.Controlador;
import dto.FantasmaDTO;
import dto.PartidaDTO;
import dto.PacManDTO;
import modelo.Direccion;
import modelo.EstadoPartida;

public class ControladorTest {
    public static void main(String[] args) {
        Controlador controlador = Controlador.getInstance();
        mostrar("Consultar antes de iniciar", controlador.getPartida());
        PartidaDTO inicio = controlador.iniciarPartida();
        mostrar("Iniciar partida", inicio);
        System.out.println("Tablero: # pared, . punto, o superpunto, espacio sin punto");
        for (String fila : inicio.getTablero()) {
            System.out.println(fila);
        }

        probarMovimientoYPausa(controlador);
        probarSuperPuntoYPared(controlador);

        mostrar("Volver a mostrar la respuesta inicial", inicio);
        mostrar("Consultar el estado actual", controlador.getPartida());

        probarFinYNuevaPartida(controlador);
    }

    private static void probarMovimientoYPausa(Controlador controlador) {
        mostrar("Solicitar izquierda",
                controlador.moverPacMan(Direccion.IZQUIERDA));
        mostrar("Actualizar 0.25 segundos: avanza una celda y come un punto",
                controlador.actualizarPartida(0.25));
        mostrar("Pausar", controlador.pausarPartida());
        mostrar("Actualizar durante la pausa: el estado se mantiene",
                controlador.actualizarPartida(1));
        mostrar("Reanudar", controlador.reanudarPartida());
    }

    private static void probarSuperPuntoYPared(Controlador controlador) {
        mostrar("Avanzar otras tres celdas a la izquierda",
                controlador.actualizarPartida(0.75));
        mostrar("Solicitar abajo", controlador.moverPacMan(Direccion.ABAJO));
        mostrar("Llegar al superpunto: suma 50 y asusta fantasmas",
                controlador.actualizarPartida(0.25));
        mostrar("Pausar con fantasmas asustados", controlador.pausarPartida());
        mostrar("Actualizar en pausa: tampoco baja el tiempo asustado",
                controlador.actualizarPartida(0.5));
        mostrar("Reanudar después del superpunto", controlador.reanudarPartida());
        mostrar("Solicitar izquierda hacia una pared",
                controlador.moverPacMan(Direccion.IZQUIERDA));
        mostrar("Actualizar: Pac-Man permanece contra la pared",
                controlador.actualizarPartida(0.25));
    }

    private static void probarFinYNuevaPartida(Controlador controlador) {
        try {
            controlador.nuevaPartida();
        } catch (IllegalStateException e) {
            System.out.println("\nNueva partida durante el juego: " + e.getMessage());
        }

        PartidaDTO respuesta = controlador.getPartida();
        for (double tiempo = 0; tiempo < 60 && respuesta.getEstado() == EstadoPartida.EN_CURSO; tiempo += 0.25) {
            int vidasAnteriores = respuesta.getVidas();
            respuesta = controlador.actualizarPartida(0.25);
            if (respuesta.getVidas() != vidasAnteriores) {
                mostrar("Colisión: cambia la cantidad de vidas", respuesta);
            }
        }
        mostrar("Estado después de esperar a los fantasmas", respuesta);
        mostrar("Comenzar una nueva partida", controlador.nuevaPartida());
    }

    private static void mostrar(String accion, PartidaDTO respuesta) {
        System.out.println("\n" + accion);
        if (respuesta == null) {
            System.out.println("No hay partida");
            return;
        }
        System.out.println("Estado: " + respuesta.getEstado()
                + " | Puntaje: " + respuesta.getPuntaje()
                + " | Vidas: " + respuesta.getVidas()
                + " | Puntos restantes: " + respuesta.getPuntosRestantes());
        PacManDTO pacMan = respuesta.getPacMan();
        System.out.printf("Pac-Man: (%.2f, %.2f) | Dirección: %s%n",
                pacMan.getX(), pacMan.getY(), pacMan.getDireccion());
        for (int i = 0; i < respuesta.getFantasmas().size(); i++) {
            FantasmaDTO fantasma = respuesta.getFantasmas().get(i);
            System.out.printf("Fantasma %d: (%.2f, %.2f) | Estado: %s | Tiempo asustado: %.2f%n",
                    i + 1, fantasma.getX(), fantasma.getY(), fantasma.getEstado(),
                    fantasma.getTiempoAsustadoRestante());
        }
    }
}

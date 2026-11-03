package pruebas;

import controlador.Controlador;
import dto.FantasmaDTO;
import dto.PartidaDTO;
import dto.PacManDTO;
import modelo.Direccion;
import modelo.EstadoPartida;

public class ControladorTest {
    public static void main(String[] args) {
        mostrar("Consultar antes de iniciar", Controlador.getInstance().getPartida());
        PartidaDTO inicio = Controlador.getInstance().iniciarPartida();
        mostrar("Iniciar partida", inicio);
        System.out.println("Tablero: # pared, . punto, o superpunto, espacio sin punto");
        for (String fila : inicio.getTablero()) {
            System.out.println(fila);
        }

        probarMovimientoYPausa();
        probarSuperPuntoYPared();

        mostrar("Volver a mostrar la respuesta inicial", inicio);
        mostrar("Consultar el estado actual", Controlador.getInstance().getPartida());

        probarFinYNuevaPartida();
    }

    private static void probarMovimientoYPausa() {
        mostrar("Solicitar izquierda",
                Controlador.getInstance().moverPacMan(Direccion.IZQUIERDA));
        mostrar("Actualizar 0.25 segundos: avanza una celda y come un punto",
                Controlador.getInstance().actualizarPartida(0.25));
        mostrar("Pausar", Controlador.getInstance().pausarPartida());
        mostrar("Actualizar durante la pausa: el estado se mantiene",
                Controlador.getInstance().actualizarPartida(1));
        mostrar("Reanudar", Controlador.getInstance().reanudarPartida());
    }

    private static void probarSuperPuntoYPared() {
        mostrar("Avanzar otras tres celdas a la izquierda",
                Controlador.getInstance().actualizarPartida(0.75));
        mostrar("Solicitar abajo", Controlador.getInstance().moverPacMan(Direccion.ABAJO));
        mostrar("Llegar al superpunto: suma 50 y asusta fantasmas",
                Controlador.getInstance().actualizarPartida(0.25));
        mostrar("Pausar con fantasmas asustados", Controlador.getInstance().pausarPartida());
        mostrar("Actualizar en pausa: tampoco baja el tiempo asustado",
                Controlador.getInstance().actualizarPartida(0.5));
        mostrar("Reanudar después del superpunto", Controlador.getInstance().reanudarPartida());
        mostrar("Solicitar izquierda hacia una pared",
                Controlador.getInstance().moverPacMan(Direccion.IZQUIERDA));
        mostrar("Actualizar: Pac-Man permanece contra la pared",
                Controlador.getInstance().actualizarPartida(0.25));
    }

    private static void probarFinYNuevaPartida() {
        try {
            Controlador.getInstance().nuevaPartida();
        } catch (IllegalStateException e) {
            System.out.println("\nNueva partida durante el juego: " + e.getMessage());
        }

        PartidaDTO respuesta = Controlador.getInstance().getPartida();
        for (double tiempo = 0; tiempo < 60 && respuesta.getEstado() == EstadoPartida.EN_CURSO; tiempo += 0.25) {
            int vidasAnteriores = respuesta.getVidas();
            respuesta = Controlador.getInstance().actualizarPartida(0.25);
            if (respuesta.getVidas() != vidasAnteriores) {
                mostrar("Colisión: cambia la cantidad de vidas", respuesta);
            }
        }
        mostrar("Estado después de esperar a los fantasmas", respuesta);
        mostrar("Comenzar una nueva partida", Controlador.getInstance().nuevaPartida());
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

package vista;

import controlador.Controlador;
import dto.PartidaDTO;
import modelo.Direccion;
import modelo.EstadoPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class VentanaJuego extends JFrame {
    private static final int INTERVALO_ACTUALIZACION = 40;
    private static final double SEGUNDOS_POR_ACTUALIZACION = INTERVALO_ACTUALIZACION / 1000.0;
    private final PanelTablero panel;
    private final JLabel puntaje = new JLabel();
    private final JLabel vidas = new JLabel();
    private final JLabel estadoPartida = new JLabel(" ", SwingConstants.CENTER);
    private final Timer timer;
    private final JButton botonReiniciar = new JButton("Volver a jugar");
    private final JLabel ayuda = new JLabel("Presioná una flecha para empezar", SwingConstants.CENTER);
    private boolean juegoIniciado = false;
    private boolean enMenu = true;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel pantallas = new JPanel(tarjetas);

    public VentanaJuego() {
        super("Pac-Man");

        PartidaDTO datos = Controlador.getInstance().iniciarPartida();

        panel = new PanelTablero(datos);
        timer = new Timer(INTERVALO_ACTUALIZACION, e -> actualizarJuego());
        configurarVentana();
        configurarEventos();
        configurarTeclado();
        actualizarVista(datos);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
    }

    private void configurarVentana() {
        JPanel informacion = new JPanel(new FlowLayout());
        informacion.add(puntaje);
        informacion.add(vidas);

        JPanel controles = new JPanel(new GridLayout(3, 1, 0, 5));
        controles.add(estadoPartida);
        controles.add(ayuda);
        controles.add(botonReiniciar);
        controles.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel juego = new JPanel(new BorderLayout());
        juego.add(informacion, BorderLayout.NORTH);
        juego.add(panel, BorderLayout.CENTER);
        juego.add(controles, BorderLayout.SOUTH);

        pantallas.add(crearMenu(), "menu");
        pantallas.add(juego, "juego");
        setContentPane(pantallas);
        tarjetas.show(pantallas, "menu");

        botonReiniciar.setEnabled(false);
        // El teclado queda en el tablero; el boton sigue funcionando con el mouse.
        botonReiniciar.setFocusable(false);
    }

    private void configurarEventos() {
        botonReiniciar.addActionListener(e -> reiniciarJuego());
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 80));
        JPanel contenido = new JPanel(new BorderLayout(0, 24));
        contenido.add(new JLabel("Pac-Man", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel instrucciones = new JPanel(new GridLayout(4, 1, 0, 8));
        instrucciones.add(new JLabel("Comé todos los puntos para ganar.", SwingConstants.CENTER));
        instrucciones.add(new JLabel("Flechas: mover a Pac-Man", SwingConstants.CENTER));
        instrucciones.add(new JLabel("P: pausar o continuar", SwingConstants.CENTER));
        instrucciones.add(new JLabel("Los superpuntos asustan a los fantasmas.", SwingConstants.CENTER));
        contenido.add(instrucciones, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        JButton jugar = new JButton("Jugar");
        jugar.addActionListener(e -> mostrarJuego());
        botones.add(jugar);
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> {
            timer.stop();
            dispose();
        });
        botones.add(salir);
        contenido.add(botones, BorderLayout.SOUTH);
        menu.add(contenido);
        return menu;
    }

    private void mostrarJuego() {
        enMenu = false;
        tarjetas.show(pantallas, "juego");
        panel.requestFocusInWindow();
    }

    private void actualizarVista(PartidaDTO datos) {
        panel.actualizarPartida(datos);
        puntaje.setText("Puntos: " + datos.getPuntaje());
        vidas.setText("Vidas: " + datos.getVidas());
        switch (datos.getEstado()) {
            case EN_CURSO:
                if (juegoIniciado) {
                    estadoPartida.setText("En curso");
                } else {
                    estadoPartida.setText(" ");
                }
                break;
            case EN_PAUSA:
                estadoPartida.setText("Pausa");
                break;
            case GANADA:
                estadoPartida.setText("¡Ganaste!");
                break;
            case PERDIDA:
                estadoPartida.setText("Fin del juego");
                break;
        }
    }

    private void actualizarJuego() {
        if (!juegoIniciado || Controlador.getInstance().getPartida().getEstado() != EstadoPartida.EN_CURSO) {
            return;
        }
        PartidaDTO datosActualizados = Controlador.getInstance().actualizarPartida(SEGUNDOS_POR_ACTUALIZACION);
        actualizarVista(datosActualizados);
        if (datosActualizados.getEstado() == EstadoPartida.GANADA
                || datosActualizados.getEstado() == EstadoPartida.PERDIDA) {
            timer.stop();
            botonReiniciar.setEnabled(true);
            ayuda.setText("Partida terminada. Podés volver a jugar.");
        }
    }

    private void alternarPausa() {
        if (enMenu || !juegoIniciado) {
            return;
        }
        EstadoPartida estado = Controlador.getInstance().getPartida().getEstado();
        if (estado == EstadoPartida.EN_CURSO) {
            timer.stop();
            actualizarVista(Controlador.getInstance().pausarPartida());
            ayuda.setText("Presioná P para continuar");
        } else if (estado == EstadoPartida.EN_PAUSA) {
            actualizarVista(Controlador.getInstance().reanudarPartida());
            ayuda.setText("Flechas: mover  |  P: pausar");
            timer.start();
        }
    }

    private void reiniciarJuego() {
        EstadoPartida estado = Controlador.getInstance().getPartida().getEstado();
        if (estado != EstadoPartida.GANADA && estado != EstadoPartida.PERDIDA) {
            return;
        }
        timer.stop();
        juegoIniciado = false;
        actualizarVista(Controlador.getInstance().nuevaPartida());
        botonReiniciar.setEnabled(false);
        ayuda.setText("Presioná una flecha para empezar");
        panel.requestFocusInWindow();
    }

    private void configurarTeclado() {
        panel.setFocusable(true);
        panel.addKeyListener(new ManejoTeclado());
    }

    private void moverPacMan(Direccion direccion) {
        if (enMenu || Controlador.getInstance().getPartida().getEstado() != EstadoPartida.EN_CURSO) {
            return;
        }
        Controlador.getInstance().moverPacMan(direccion);
        if (!juegoIniciado) {
            juegoIniciado = true;
            ayuda.setText("Flechas: mover  |  P: pausar");
            // Registrar la dirección antes de avanzar conserva la primera tecla.
            actualizarJuego();
            if (Controlador.getInstance().getPartida().getEstado() == EstadoPartida.EN_CURSO) {
                timer.start();
            }
        }
    }

    private class ManejoTeclado implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    moverPacMan(Direccion.ARRIBA);
                    break;
                case KeyEvent.VK_DOWN:
                    moverPacMan(Direccion.ABAJO);
                    break;
                case KeyEvent.VK_LEFT:
                    moverPacMan(Direccion.IZQUIERDA);
                    break;
                case KeyEvent.VK_RIGHT:
                    moverPacMan(Direccion.DERECHA);
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Al soltar P se alterna una vez, aunque se mantenga presionada.
            if (e.getKeyCode() == KeyEvent.VK_P) {
                alternarPausa();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // NOOP
        }
    }

    public static void main(String[] args) {
        new VentanaJuego().setVisible(true);
    }
}


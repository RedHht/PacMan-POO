package vista;

import controlador.Controlador;
import dto.PartidaDTO;
import modelo.Direccion;
import modelo.EstadoPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        setLocationRelativeTo(null);
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
    }

    private void configurarEventos() {
        botonReiniciar.addActionListener(e -> reiniciarJuego());
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel(new GridLayout(7, 1, 0, 10));
        menu.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        menu.add(new JLabel("Pac-Man", SwingConstants.CENTER));
        menu.add(new JLabel("Comé todos los puntos para ganar.", SwingConstants.CENTER));
        menu.add(new JLabel("Flechas: mover a Pac-Man", SwingConstants.CENTER));
        menu.add(new JLabel("P: pausar o continuar", SwingConstants.CENTER));
        menu.add(new JLabel("Los superpuntos asustan a los fantasmas.", SwingConstants.CENTER));

        JButton jugar = new JButton("Jugar");
        jugar.addActionListener(e -> mostrarJuego());
        menu.add(jugar);
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> dispose());
        menu.add(salir);
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
                estadoPartida.setText("En curso");
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
        actualizarVista(Controlador.getInstance().nuevaPartida());
        juegoIniciado = false;
        botonReiniciar.setEnabled(false);
        ayuda.setText("Presioná una flecha para empezar");
    }

    @Override
    public void dispose() {
        timer.stop();
        super.dispose();
    }

    private void configurarTeclado() {
        asignarTecla("UP", Direccion.ARRIBA);
        asignarTecla("DOWN", Direccion.ABAJO);
        asignarTecla("LEFT", Direccion.IZQUIERDA);
        asignarTecla("RIGHT", Direccion.DERECHA);
        // Al soltar P se alterna una sola vez, aunque se mantenga presionada.
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("released P"), "pausar");
        panel.getActionMap().put("pausar", new AccionPausa());
    }

    private void asignarTecla(String tecla, Direccion direccion) {
        String nombreAccion = "mover_" + direccion.name();
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla), nombreAccion);
        panel.getActionMap().put(nombreAccion, new AccionMovimiento(direccion));
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

    // Las acciones se registran para toda la ventana, incluso si un botón tiene el foco.
    private class AccionMovimiento extends AbstractAction {
        private final Direccion direccion;

        public AccionMovimiento(Direccion direccion) {
            this.direccion = direccion;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            moverPacMan(direccion);
        }
    }

    private class AccionPausa extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            alternarPausa();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaJuego().setVisible(true);
        });
    }
}



package vista;

import controlador.Controlador;
import dto.PartidaDTO;
import modelo.Direccion;
import modelo.EstadoPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaJuego extends JFrame {
    private final Controlador controlador;
    private final PanelTablero panel;
    private final Timer timer;
    private final JButton botonReiniciar = new JButton("Volver a jugar");
    private final JLabel ayuda = new JLabel("Presioná una flecha para empezar", SwingConstants.CENTER);
    private boolean juegoIniciado = false;
    private boolean enMenu = true;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel pantallas = new JPanel(tarjetas);

    public VentanaJuego() {
        super("Pac-Man");

        controlador = Controlador.getInstance();
        PartidaDTO datos = controlador.iniciarPartida();

        panel = new PanelTablero(datos);
        JPanel juego = new JPanel(new BorderLayout());
        juego.add(panel);
        JPanel controles = new JPanel(new BorderLayout(8, 8));
        controles.add(ayuda, BorderLayout.CENTER);
        controles.add(botonReiniciar, BorderLayout.SOUTH);
        controles.setBackground(new Color(10, 15, 31));
        controles.setBorder(BorderFactory.createEmptyBorder(10, 16, 14, 16));
        ayuda.setForeground(new Color(193, 203, 225));
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        estilizarBoton(botonReiniciar);
        juego.add(controles, BorderLayout.SOUTH);
        pantallas.add(crearMenu(), "menu");
        pantallas.add(juego, "juego");
        setContentPane(pantallas);
        tarjetas.show(pantallas, "menu");
        botonReiniciar.setEnabled(false);
        botonReiniciar.setFocusable(false);
        botonReiniciar.addActionListener(e -> reiniciarJuego());

        configurarTeclado();

        // Actualizar el juego cada 40 milisegundos
        timer = new Timer(40, e -> actualizarJuego());
        // El primer avance se realiza al recibir la primera flecha.

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(new Color(7, 10, 23));
        menu.setBorder(BorderFactory.createEmptyBorder(32, 30, 32, 30));
        menu.add(Box.createVerticalGlue());
        agregarTexto(menu, "P A C - M A N", 38, new Color(255, 220, 67));
        menu.add(Box.createVerticalStrut(10));
        agregarTexto(menu, "EL LABERINTO TE ESPERA", 13, new Color(91, 191, 255));
        menu.add(Box.createVerticalStrut(38));
        agregarTexto(menu, "Comé todos los puntos para ganar.", 16, Color.WHITE);
        menu.add(Box.createVerticalStrut(12));
        agregarTexto(menu, "Flechas  ·  Mover a Pac-Man", 14, Color.LIGHT_GRAY);
        agregarTexto(menu, "P  ·  Pausar o continuar", 14, Color.LIGHT_GRAY);
        menu.add(Box.createVerticalStrut(12));
        agregarTexto(menu, "Los superpuntos asustan a los fantasmas.", 13, Color.LIGHT_GRAY);
        menu.add(Box.createVerticalStrut(32));
        JButton jugar = new JButton("Jugar");
        estilizarBoton(jugar);
        jugar.addActionListener(e -> {
            enMenu = false;
            tarjetas.show(pantallas, "juego");
            panel.requestFocusInWindow();
        });
        menu.add(jugar);
        menu.add(Box.createVerticalStrut(12));
        JButton salir = new JButton("Salir");
        estilizarBoton(salir);
        salir.addActionListener(e -> dispose());
        menu.add(salir);
        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private void agregarTexto(JPanel destino, String texto, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        etiqueta.setMaximumSize(new Dimension(Integer.MAX_VALUE, etiqueta.getPreferredSize().height + 4));
        destino.add(etiqueta);
    }

    private void estilizarBoton(JButton boton) {
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setBackground(new Color(255, 220, 67));
        boton.setForeground(new Color(12, 18, 35));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void actualizarJuego() {
        if (!juegoIniciado || controlador.getPartida().getEstado() != EstadoPartida.EN_CURSO) {
            return;
        }
        PartidaDTO datosActualizados = controlador.actualizarPartida(0.04);
        panel.actualizarPartida(datosActualizados);
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
        EstadoPartida estado = controlador.getPartida().getEstado();
        if (estado == EstadoPartida.EN_CURSO) {
            timer.stop();
            panel.actualizarPartida(controlador.pausarPartida());
            ayuda.setText("Presioná P para continuar");
        } else if (estado == EstadoPartida.EN_PAUSA) {
            panel.actualizarPartida(controlador.reanudarPartida());
            ayuda.setText("Flechas: mover  |  P: pausar");
            timer.start();
        }
    }

    private void reiniciarJuego() {
        EstadoPartida estado = controlador.getPartida().getEstado();
        if (estado != EstadoPartida.GANADA && estado != EstadoPartida.PERDIDA) {
            return;
        }
        timer.stop();
        panel.actualizarPartida(controlador.nuevaPartida());
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
        panel.getActionMap().put("pausar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alternarPausa();
            }
        });
    }

    private void asignarTecla(String tecla, Direccion direccion) {

        String nombreAccion = "mover_" + direccion.name();

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla), nombreAccion);

        panel.getActionMap().put(nombreAccion, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (enMenu || controlador.getPartida().getEstado() != EstadoPartida.EN_CURSO) {
                    return;
                }
                controlador.moverPacMan(direccion);
                if (!juegoIniciado) {
                    juegoIniciado = true;
                    ayuda.setText("Flechas: mover  |  P: pausar");
                    // Guardar la direccion antes de avanzar evita perder la primera tecla.
                    actualizarJuego();
                    if (controlador.getPartida().getEstado() == EstadoPartida.EN_CURSO) {
                        timer.start();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaJuego().setVisible(true);
        });
    }
}


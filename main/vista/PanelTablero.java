package vista;

import dto.FantasmaDTO;
import dto.PartidaDTO;
import modelo.Direccion;
import modelo.EstadoFantasma;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class PanelTablero extends JPanel {
    private static final int TAMANIO_CELDA = 40;
    private static final int MARGEN_PERSONAJE = 5;
    private static final int TAMANIO_PERSONAJE = 30;
    private static final Color[] COLORES_FANTASMAS = {
        Color.RED, Color.PINK, Color.CYAN, Color.ORANGE
    };

    private PartidaDTO partida;

    public PanelTablero(PartidaDTO partida) {
        this.partida = partida;
        int filas = partida.getTablero().size();
        int columnas = partida.getTablero().get(0).length();
        setPreferredSize(new Dimension(columnas * TAMANIO_CELDA, filas * TAMANIO_CELDA));
        setBackground(Color.BLACK);
    }

    public void actualizarPartida(PartidaDTO datos) {
        partida = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarLaberinto(g);
        dibujarPacMan(g);
        dibujarFantasmas(g);
    }

    private void dibujarLaberinto(Graphics g) {
        for (int fila = 0; fila < partida.getTablero().size(); fila++) {
            String contenido = partida.getTablero().get(fila);
            for (int columna = 0; columna < contenido.length(); columna++) {
                int x = columna * TAMANIO_CELDA;
                int y = fila * TAMANIO_CELDA;
                switch (contenido.charAt(columna)) {
                    case '#':
                        g.setColor(Color.BLUE);
                        g.fillRect(x + 1, y + 1, TAMANIO_CELDA - 2, TAMANIO_CELDA - 2);
                        break;
                    case '.':
                        dibujarPunto(g, x, y, 6);
                        break;
                    case 'o':
                        dibujarPunto(g, x, y, 14);
                        break;
                }
            }
        }
    }

    private void dibujarPunto(Graphics g, int x, int y, int diametro) {
        int margen = (TAMANIO_CELDA - diametro) / 2;
        g.setColor(Color.WHITE);
        g.fillOval(x + margen, y + margen, diametro, diametro);
    }

    // El modelo expresa las posiciones en celdas; la vista las convierte a pixeles.
    private int convertirAPixeles(double posicion) {
        return (int) Math.round(posicion * TAMANIO_CELDA) + MARGEN_PERSONAJE;
    }

    private void dibujarPacMan(Graphics g) {
        int x = convertirAPixeles(partida.getPacMan().getX());
        int y = convertirAPixeles(partida.getPacMan().getY());
        int angulo = 0;
        Direccion direccion = partida.getPacMan().getDireccion();
        if (direccion == Direccion.ARRIBA) {
            angulo = 90;
        } else if (direccion == Direccion.IZQUIERDA) {
            angulo = 180;
        } else if (direccion == Direccion.ABAJO) {
            angulo = 270;
        }
        g.setColor(Color.YELLOW);
        g.fillArc(x, y, TAMANIO_PERSONAJE, TAMANIO_PERSONAJE, angulo + 30, 300);
    }

    private void dibujarFantasmas(Graphics g) {
        for (int i = 0; i < partida.getFantasmas().size(); i++) {
            FantasmaDTO fantasma = partida.getFantasmas().get(i);
            Color color = COLORES_FANTASMAS[i % COLORES_FANTASMAS.length];
            dibujarFantasma(g, fantasma, color);
        }
    }

    private void dibujarFantasma(Graphics g, FantasmaDTO fantasma, Color color) {
        int x = convertirAPixeles(fantasma.getX());
        int y = convertirAPixeles(fantasma.getY());
        if (fantasma.getEstado() != EstadoFantasma.REGRESANDO) {
            if (fantasma.getEstado() == EstadoFantasma.ASUSTADO) {
                color = Color.BLUE;
            }
            g.setColor(color);
            g.fillArc(x, y, TAMANIO_PERSONAJE, TAMANIO_PERSONAJE, 0, 180);
            g.fillRect(x, y + TAMANIO_PERSONAJE / 2,
                    TAMANIO_PERSONAJE, TAMANIO_PERSONAJE / 2);
        }
        // Al regresar al origen se dibujan solamente los ojos.
        g.setColor(Color.WHITE);
        g.fillOval(x + 6, y + 9, 7, 9);
        g.fillOval(x + 18, y + 9, 7, 9);
        g.setColor(Color.BLACK);
        g.fillOval(x + 8, y + 12, 3, 4);
        g.fillOval(x + 20, y + 12, 3, 4);
    }
}

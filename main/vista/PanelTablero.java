package vista;

import dto.FantasmaDTO;
import dto.PartidaDTO;
import modelo.EstadoFantasma;
import modelo.EstadoPartida;
import modelo.Direccion;
import java.awt.geom.Path2D;

import javax.swing.JPanel;
import java.awt.*;


public class PanelTablero extends JPanel {

    private static final int CELDA = 40;
    private PartidaDTO partida;

    public PanelTablero(PartidaDTO partida) {
        this.partida = partida;

        int alto = partida.getTablero().size();
        int ancho = partida.getTablero().get(0).length();

        setPreferredSize(
            new Dimension(ancho * CELDA, alto * CELDA + 40)
        );
        setBackground(new Color(7, 10, 23));
    }

    public void actualizarPartida(PartidaDTO datos) {
        this.partida = datos;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar el laberinto
        for (int y = 0; y < partida.getTablero().size(); y++) {
            String fila = partida.getTablero().get(y);

            for (int x = 0; x < fila.length(); x++) {
                char simbolo = fila.charAt(x);

                int px = x * CELDA;
                int py = y * CELDA;

                if (simbolo == '#') {
                    g2.setColor(new Color(17, 32, 77));
                    g2.fillRoundRect(px + 2, py + 2, CELDA - 4, CELDA - 4, 10, 10);
                    g2.setColor(new Color(49, 104, 230));
                    g2.drawRoundRect(px + 2, py + 2, CELDA - 5, CELDA - 5, 10, 10);
                } else if (simbolo == '.') {
                    g2.setColor(new Color(255, 231, 184));
                    g2.fillOval(px + 17, py + 17, 6, 6);
                } else if (simbolo == 'o') {
                    g2.setColor(new Color(255, 220, 67, 40));
                    g2.fillOval(px + 8, py + 8, 24, 24);
                    g2.setColor(new Color(255, 220, 67));
                    g2.fillOval(px + 13, py + 13, 14, 14);
                }
            }
        }

        // Dibujar Pac-Man
        int pacX = (int) Math.round(
            partida.getPacMan().getX() * CELDA
        );
        int pacY = (int) Math.round(
            partida.getPacMan().getY() * CELDA
        );

        Direccion direccion = partida.getPacMan().getDireccion();
        int angulo = direccion == Direccion.ARRIBA ? 90
                : direccion == Direccion.IZQUIERDA ? 180
                : direccion == Direccion.ABAJO ? 270 : 0;
        // La boca se anima con la distancia recorrida; se congela al pausar.
        double recorrido = partida.getPacMan().getX() + partida.getPacMan().getY();
        int apertura = 12 + (int) (23 * Math.abs(Math.sin(recorrido * Math.PI * 2)));
        g2.setColor(new Color(255, 220, 67));
        g2.fillArc(pacX + 4, pacY + 4, 32, 32, angulo + apertura, 360 - 2 * apertura);

        // Dibujar los fantasmas
        Color[] colores = {
            Color.RED, Color.PINK, Color.CYAN, Color.ORANGE
        };

        for (int i = 0; i < partida.getFantasmas().size(); i++) {
            FantasmaDTO fantasma = partida.getFantasmas().get(i);

            int fx = (int) Math.round(fantasma.getX() * CELDA);
            int fy = (int) Math.round(fantasma.getY() * CELDA);

            dibujarFantasma(g2, fantasma, fx, fy, colores[i % colores.length]);
        }

        // Dibujar puntaje y vidas
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        int altura = partida.getTablero().size() * CELDA;

        g2.drawString(
            "PUNTOS  " + partida.getPuntaje(),
            15, altura + 26
        );
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(170, 185, 216));
        g2.drawString("VIDAS", getWidth() - 158, altura + 25);
        g2.setColor(new Color(255, 220, 67));
        for (int i = 0; i < partida.getVidas(); i++) {
            g2.fillArc(getWidth() - 105 + i * 30, altura + 10, 22, 22, 35, 290);
        }
        dibujarEstado(g2, altura);
    }

    private void dibujarFantasma(Graphics2D g, FantasmaDTO fantasma, int x, int y, Color color) {
        boolean asustado = fantasma.getEstado() == EstadoFantasma.ASUSTADO;
        if (fantasma.getEstado() != EstadoFantasma.REGRESANDO) {
            g.setColor(asustado ? new Color(62, 83, 245) : color);
            Path2D cuerpo = new Path2D.Double();
            cuerpo.moveTo(x + 5, y + 35);
            cuerpo.lineTo(x + 5, y + 20);
            cuerpo.curveTo(x + 5, y, x + 35, y, x + 35, y + 20);
            cuerpo.lineTo(x + 35, y + 35);
            for (int i = 4; i >= 0; i--) {
                cuerpo.lineTo(x + 5 + i * 6, y + (i % 2 == 0 ? 35 : 30));
            }
            cuerpo.closePath();
            g.fill(cuerpo);
        }
        g.setColor(Color.WHITE);
        if (asustado) {
            g.fillOval(x + 12, y + 15, 5, 5);
            g.fillOval(x + 24, y + 15, 5, 5);
            g.drawPolyline(new int[]{x+11,x+15,x+19,x+23,x+27,x+30},
                    new int[]{y+27,y+24,y+27,y+24,y+27,y+24}, 6);
        } else {
            g.fillOval(x + 10, y + 11, 9, 13);
            g.fillOval(x + 23, y + 11, 9, 13);
            Direccion d = fantasma.getDireccion();
            int dx = d == Direccion.IZQUIERDA ? -2 : d == Direccion.DERECHA ? 2 : 0;
            int dy = d == Direccion.ARRIBA ? -3 : d == Direccion.ABAJO ? 3 : 0;
            g.setColor(new Color(20, 35, 90));
            g.fillOval(x + 12 + dx, y + 15 + dy, 5, 6);
            g.fillOval(x + 25 + dx, y + 15 + dy, 5, 6);
        }
    }

    private void dibujarEstado(Graphics2D g, int altura) {
        EstadoPartida estado = partida.getEstado();
        if (estado == EstadoPartida.EN_CURSO) {
            return;
        }
        Graphics2D capa = (Graphics2D) g.create();
        try {
            capa.setColor(new Color(0, 0, 0, 190));
            capa.fillRect(0, 0, getWidth(), altura);
            capa.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            String titulo = estado == EstadoPartida.EN_PAUSA ? "PAUSA"
                    : estado == EstadoPartida.GANADA ? "¡GANASTE!" : "FIN DEL JUEGO";
            String detalle = estado == EstadoPartida.EN_PAUSA
                    ? "Presioná P para continuar"
                    : "Puntaje final: " + partida.getPuntaje();
            capa.setColor(Color.YELLOW);
            capa.setFont(new Font("Arial", Font.BOLD, 32));
            dibujarCentrado(capa, titulo, altura / 2 - 12);
            capa.setColor(Color.WHITE);
            capa.setFont(new Font("Arial", Font.PLAIN, 18));
            dibujarCentrado(capa, detalle, altura / 2 + 24);
        } finally {
            capa.dispose();
        }
    }

    private void dibujarCentrado(Graphics2D g, String texto, int y) {
        int x = (getWidth() - g.getFontMetrics().stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }
    
}

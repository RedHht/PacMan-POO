package modelo;

import java.util.ArrayList;
import java.util.List;

/** '#' es pared, '.' es punto comun y 'o' es superpunto. */
public class Laberinto {
    private static final String[] MAPA_PREDETERMINADO = {
        "###########",
        "#o..#....o#",
        "#.#.#.###.#",
        "#.#...#...#",
        "#...#...#.#",
        "###...#...#",
        "#...#...#.#",
        "#.###.###.#",
        "#.....#...#",
        "#o#####.#o#",
        "###########"
    };

    private final Celda[][] celdas;
    private int puntosRestantes;

    public Laberinto() {
        this(MAPA_PREDETERMINADO);
    }

    public Laberinto(String... filas) {
        if (filas.length < 5 || filas[0] == null || filas[0].length() < 5) {
            throw new IllegalArgumentException("El mapa debe tener al menos 5 filas y columnas");
        }

        int ancho = filas[0].length();
        celdas = new Celda[filas.length][ancho];
        for (int y = 0; y < filas.length; y++) {
            if (filas[y] == null || filas[y].length() != ancho) {
                throw new IllegalArgumentException("Todas las filas deben tener el mismo ancho");
            }
            for (int x = 0; x < ancho; x++) {
                char simbolo = filas[y].charAt(x);
                if (simbolo != '#' && simbolo != '.' && simbolo != 'o') {
                    throw new IllegalArgumentException("Use solamente '#', '.' y 'o'");
                }
                Celda celda = new Celda(simbolo != '#');
                if (simbolo == '.') {
                    celda.colocarPunto(new PuntoComun());
                    puntosRestantes++;
                } else if (simbolo == 'o') {
                    celda.colocarPunto(new SuperPunto());
                    puntosRestantes++;
                }
                celdas[y][x] = celda;
            }
        }
    }

    public int getAncho() {
        return celdas[0].length;
    }
    public int getAlto() {
        return celdas.length;
    }
    public int getPuntosRestantes() {
        return puntosRestantes;
    }

    public Celda obtenerCelda(Posicion posicion) {
        if (!estaAlineado(posicion) || posicion.getY() < 0 || posicion.getY() >= getAlto()
                || posicion.getX() < 0 || posicion.getX() >= getAncho()) return null;
        return celdas[(int) posicion.getY()][(int) posicion.getX()];
    }

    public boolean esTransitable(Posicion posicion) {
        Celda celda = obtenerCelda(posicion);
        return celda != null && celda.esTransitable();
    }

    public boolean estaAlineado(Posicion posicion) {
        return posicion != null && posicion.getX() == Math.floor(posicion.getX())
                && posicion.getY() == Math.floor(posicion.getY());
    }

    public boolean puedeMover(Posicion posicion, Direccion direccion) {
        return posicion != null && direccion != null && esTransitable(posicion.avanzar(direccion));
    }

    public List<Direccion> direccionesDisponibles(Posicion posicion) {
        List<Direccion> disponibles = new ArrayList<>();
        for (Direccion direccion : Direccion.values()) {
            if (puedeMover(posicion, direccion)) {
                disponibles.add(direccion);
            }
        }
        return disponibles;
    }

    // Distancia minima por los pasillos, calculada con BFS.
    public int distanciaMinima(Posicion origen, Posicion destino) {
        if (!esTransitable(origen) || !esTransitable(destino)) {
            return Integer.MAX_VALUE;
        }
        List<Posicion> pendientes = new ArrayList<>();
        boolean[][] visitadas = new boolean[getAlto()][getAncho()];
        int[][] distancias = new int[getAlto()][getAncho()];
        pendientes.add(origen);
        visitadas[(int) origen.getY()][(int) origen.getX()] = true;
        int siguiente = 0;
        while (siguiente < pendientes.size()) {
            Posicion actual = pendientes.get(siguiente);
            siguiente++;
            int x = (int) actual.getX();
            int y = (int) actual.getY();
            if (actual.equals(destino)) {
                return distancias[y][x];
            }
            for (Direccion direccion : direccionesDisponibles(actual)) {
                Posicion vecina = actual.avanzar(direccion);
                int columna = (int) vecina.getX();
                int fila = (int) vecina.getY();
                if (!visitadas[fila][columna]) {
                    visitadas[fila][columna] = true;
                    distancias[fila][columna] = distancias[y][x] + 1;
                    pendientes.add(vecina);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public Punto retirarPunto(Posicion posicion) {
        Celda celda = obtenerCelda(posicion);
        if (celda == null) {
            return null;
        }
        Punto punto = celda.retirarPunto();
        if (punto != null) {
            puntosRestantes--;
        }
        return punto;
    }

    public boolean quedanPuntos() {
        return puntosRestantes > 0;
    }

}

# Pac-Man básico

Implementación en Java del motor de juego descrito en la documentación de la
fase A para la entrega de la etapa B. Las clases están en `main/` y no
dependen de bibliotecas externas.

## Organización

- `main/modelo`: laberinto, personajes, puntos y reglas de la partida.
- `main/controlador`: controlador del juego.
- `main/pruebas`: pruebas ejecutables.

## Uso

`Controlador` inicia y administra una `Partida`. Una interfaz puede llamar
`moverPacMan(direccion)` cuando recibe una tecla y
`actualizarPartida(deltaTiempo)` en cada actualización. `deltaTiempo` se
expresa en segundos. Los personajes avanzan de forma continua a cuatro celdas
por segundo, usando posiciones decimales. Cada personaje administra su movimiento
y aplica los giros al alcanzar el centro de una celda.
Pac-Man consume los puntos al llegar al centro de su celda. Una colision ocurre
cuando Pac-Man y un fantasma ocupan la misma celda o intercambian celdas durante
una actualizacion.
El estado se consulta mediante
`getPartida()`, sus getters y `getLaberinto()`.

```java
import controlador.Controlador;
import modelo.Direccion;

Controlador controlador = Controlador.getInstance();
controlador.iniciarPartida();
controlador.moverPacMan(Direccion.DERECHA);
controlador.actualizarPartida(0.25);
System.out.println(controlador.getPartida().getPuntaje());
```

El mapa usa `#` para paredes, `.` para puntos comunes y `o` para superpuntos.
Ambos tipos de punto corresponden a celdas transitables. Los cuatro superpuntos
están marcados en las esquinas del mapa predeterminado. También se puede crear
un `Laberinto` con un arreglo rectangular de filas usando estos símbolos.

## Verificación

Con un JDK:

```sh
javac -d out main/modelo/*.java main/controlador/*.java main/pruebas/*.java
java -cp out pruebas.PartidaTest
```

Las pruebas cubren inicio, puntaje, movimiento, paredes, pausa, temporizador,
colisiones, fantasmas comidos, derrota y victoria.

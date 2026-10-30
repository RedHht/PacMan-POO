package modelo;

import dto.PacManDTO;

public class PacMan extends Personaje {

    private Direccion direccionSolicitada;

    public PacMan(Posicion posicionInicial) {
        super(posicionInicial);
    }

    public PacManDTO toDTO() {
        return new PacManDTO(getPosicion().getX(), getPosicion().getY(), getDireccionActual());
    }

    public void solicitarDireccion(Direccion direccion) {
        direccionSolicitada = direccion;
    }

    public void actualizar(Laberinto laberinto, double deltaTiempo) {
        desplazar(laberinto, deltaTiempo);
    }

    @Override
    protected Direccion elegirDireccion(Laberinto laberinto) {
        if (direccionSolicitada != null &&
                laberinto.puedeMover(getPosicion(), direccionSolicitada)) {
            cambiarDireccion(direccionSolicitada);
            direccionSolicitada = null;
        }
        return getDireccionActual();
    }

    @Override
    public void volverPosicionInicial() {
        super.volverPosicionInicial();
        direccionSolicitada = null;
    }
}

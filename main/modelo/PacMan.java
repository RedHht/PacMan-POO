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

    public void actualizar(double deltaTiempo) {
        desplazar(deltaTiempo);
    }

    @Override
    public void volverPosicionInicial() {
        super.volverPosicionInicial();
        direccionSolicitada = null;
    }

    public Direccion getDireccionSolicitada() {
        return direccionSolicitada;
    }

    public void setDireccionSolicitada(Direccion direccionSolicitada) {
        this.direccionSolicitada = direccionSolicitada;
    }
}

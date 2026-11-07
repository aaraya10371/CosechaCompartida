package cosecha.comun;

public enum Operacion {
    REGISTRAR(false),
    INICIAR_SESION(false),
    CERRAR_SESION(true),
    CONSULTAR_PERFIL(true),
    ACTUALIZAR_PERFIL(true),

    CREAR_PUBLICACION(true),
    BUSCAR_PUBLICACIONES(true),
    CONSULTAR_PUBLICACION(true),
    LISTAR_MIS_PUBLICACIONES(true),
    EDITAR_PUBLICACION(true),
    RETIRAR_PUBLICACION(true),
    ELIMINAR_PUBLICACION(true),

    ENVIAR_PROPUESTA(true);

    private final boolean requiereSesion;

    Operacion(boolean requiereSesion) {
        this.requiereSesion = requiereSesion;
    }

    public boolean requiereSesion() {
        return requiereSesion;
    }
}

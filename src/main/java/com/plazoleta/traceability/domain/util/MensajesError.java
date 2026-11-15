package com.plazoleta.traceability.domain.util;

/**
 * Mensajes de error del microservicio, en un solo lugar. Se usan en el dominio, la aplicación y la
 * infraestructura (validaciones, excepciones y manejador de errores). Clase de Java puro, sin Spring.
 */
public final class MensajesError {
    public static final String BASE_DE_DATOS_DE_TRAZABILIDAD_NO_ESTA = "La base de datos de trazabilidad no está disponible";
    public static final String CLIENTE_ES_OBLIGATORIO = "El cliente es obligatorio";
    public static final String CREDENCIALES_DE_SERVICIO_INVALIDAS = "Credenciales de servicio inválidas";
    public static final String CUERPO_DE_LA_PETICION_ES_OBLIGATORIO = "El cuerpo de la petición es obligatorio";
    public static final String ESTADO_NUEVO_ES_OBLIGATORIO = "El estado nuevo es obligatorio";
    public static final String FECHA_DEL_CAMBIO_ES_OBLIGATORIA = "La fecha del cambio es obligatoria";
    public static final String NO_HAY_REGISTROS_PARA_ESTE_PEDIDO = "No hay registros para este pedido";
    public static final String PEDIDO_EL_INICIO_Y_EL_FIN_SON = "El pedido, el inicio y el fin son obligatorios";
    public static final String PEDIDO_ES_OBLIGATORIO = "El pedido es obligatorio";
    public static final String PEDIDO_NO_PUEDE_TERMINAR_ANTES_DE_EMPEZAR = "Un pedido no puede terminar antes de empezar";
    public static final String PEDIDO_PERTENECE_A_OTRO_CLIENTE = "El pedido pertenece a otro cliente";
    public static final String PETICION_ESTA_MAL_FORMADA = "La petición está mal formada";
    public static final String REQUIERE_UN_TOKEN_VALIDO = "Se requiere un token válido";
    public static final String RESTAURANTE_ES_OBLIGATORIO = "El restaurante es obligatorio";
    public static final String RESTAURANTE_NO_EXISTE = "El restaurante no existe";
    public static final String RESTAURANTE_PERTENECE_A_OTRO_PROPIETARIO = "El restaurante pertenece a otro propietario";
    public static final String SERVICIO_DE_RESTAURANTES_NO_ESTA_DISPONIBLE = "El servicio de restaurantes no está disponible";
    public static final String SERVICIO_DE_RESTAURANTES_RECHAZO_LA_CONSULTA_HTTP = "El servicio de restaurantes rechazó la consulta (HTTP ";
    public static final String SOLO_EL_PROPIETARIO_PUEDE_VER_LA_EFICIENCIA = "Solo el propietario puede ver la eficiencia de un restaurante";
    public static final String SOLO_UN_CLIENTE_PUEDE_VER_EL_HISTORIAL = "Solo un cliente puede ver el historial de un pedido";

    private MensajesError() {
    }
}

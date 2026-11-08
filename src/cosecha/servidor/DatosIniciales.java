package cosecha.servidor;

import cosecha.excepciones.CosechaException;
import cosecha.modelo.CategoriaAlimento;
import cosecha.modelo.Publicacion;
import cosecha.modelo.UnidadMedida;
import cosecha.modelo.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de demostración que se cargan cuando el archivo de datos está vacío.
 * Todas las cuentas usan la contraseña "cosecha123".
 */
public final class DatosIniciales {

    public static final String CLAVE_DEMO = "cosecha123";

    private DatosIniciales() {
    }

    public static void cargar(ServidorAplicacion servidor) throws CosechaException {
        ServicioUsuarios su = servidor.getServicioUsuarios();
        ServicioIntercambios si = servidor.getServicioIntercambios();
        String zona = "San Ramón, Alajuela";
        LocalDate limite = LocalDate.now().plusDays(21);

        Usuario andrea = su.registrar("Andrea Araya", "andrea@example.com", CLAVE_DEMO, CLAVE_DEMO, zona);
        Usuario luis = su.registrar("Luis Mora", "luis@example.com", CLAVE_DEMO, CLAVE_DEMO, zona);
        Usuario maria = su.registrar("María Solís", "maria@example.com", CLAVE_DEMO, CLAVE_DEMO,
                "Palmares, Alajuela");

        Publicacion mangos = si.crearPublicacion(andrea.getId(), "Mangos", CategoriaAlimento.FRUTA,
                new BigDecimal("3"), UnidadMedida.KG, zona, "Aguacates o limones",
                "Mangos de mi finca. Se intercambian los 3 kg juntos.", limite);
        si.crearPublicacion(andrea.getId(), "Papayas", CategoriaAlimento.FRUTA, new BigDecimal("4"),
                UnidadMedida.UNIDAD, zona, "Verduras de hoja", "Papayas maduras, listas para comer.", limite);
        si.crearPublicacion(luis.getId(), "Limones", CategoriaAlimento.FRUTA, new BigDecimal("12"),
                UnidadMedida.UNIDAD, zona, "Hierbas",
                "Limones frescos de mi huerta. Se intercambian las 12 unidades juntas.", limite);
        si.crearPublicacion(maria.getId(), "Culantro", CategoriaAlimento.HIERBA, new BigDecimal("2"),
                UnidadMedida.MANOJO, "Palmares, Alajuela", "Frutas", "Culantro recién cortado.", limite);
        si.crearPublicacion(luis.getId(), "Chayotes", CategoriaAlimento.VERDURA, new BigDecimal("6"),
                UnidadMedida.UNIDAD, zona, "", "Chayotes tiernos de la mata del patio.", limite);

        si.enviarPropuesta(luis.getId(), mangos.getId(), "Aguacates", new BigDecimal("2"), UnidadMedida.KG,
                "Tengo aguacates maduros para intercambiar.");
        si.enviarPropuesta(maria.getId(), mangos.getId(), "Limones", new BigDecimal("12"), UnidadMedida.UNIDAD,
                "Puedo entregar el sábado.");
    }
}

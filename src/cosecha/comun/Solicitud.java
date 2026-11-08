package cosecha.comun;

import cosecha.excepciones.ValidacionException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Mensaje que el cliente envía al servidor por el socket
public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Operacion operacion;
    private final HashMap<String, Serializable> datos = new HashMap<>();
    private String token;

    public Solicitud(Operacion operacion) {
        this.operacion = operacion;
    }

    public Solicitud con(String clave, Serializable valor) {
        datos.put(clave, valor);
        return this;
    }

    public Operacion getOperacion() {
        return operacion;
    }

    public Map<String, Serializable> getDatos() {
        return datos;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTexto(String clave) {
        Object v = datos.get(clave);
        return v == null ? "" : v.toString();
    }

    public long getLong(String clave) throws ValidacionException {
        Object v = datos.get(clave);
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        throw new ValidacionException("Falta el dato '" + clave + "'.");
    }

    public BigDecimal getDecimal(String clave) {
        Object v = datos.get(clave);
        return v instanceof BigDecimal ? (BigDecimal) v : null;
    }

    public LocalDate getFecha(String clave) {
        Object v = datos.get(clave);
        return v instanceof LocalDate ? (LocalDate) v : null;
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getEnum(String clave, Class<E> tipo) {
        Object v = datos.get(clave);
        return tipo.isInstance(v) ? (E) v : null;
    }
}

package finanzas.modelo;

public class Meta {
    private int id;
    private int usuarioId;
    private String nombre;
    private double montoObjetivo;
    private double ahorroActual;
    private String descripcion;

    // Constructores
    public Meta() {}

    public Meta(int usuarioId, String nombre, double montoObjetivo, double ahorroActual, String descripcion) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.montoObjetivo = montoObjetivo;
        this.ahorroActual = ahorroActual;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getMontoObjetivo() { return montoObjetivo; }
    public void setMontoObjetivo(double montoObjetivo) { this.montoObjetivo = montoObjetivo; }

    public double getAhorroActual() { return ahorroActual; }
    public void setAhorroActual(double ahorroActual) { this.ahorroActual = ahorroActual; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getProgreso() {
        return (ahorroActual / montoObjetivo) * 100;
    }

    public boolean isCompleta() {
        return ahorroActual >= montoObjetivo;
    }
}
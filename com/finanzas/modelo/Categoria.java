package finanzas.modelo;

public class Categoria {
    private int id;
    private int usuarioId;
    private String nombre;
    private double presupuesto;

    public Categoria() {}

    public Categoria(int usuarioId, String nombre, double presupuesto) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.presupuesto = presupuesto;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }
}
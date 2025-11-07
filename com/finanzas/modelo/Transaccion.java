package finanzas.modelo;

import java.time.LocalDate;

public class Transaccion {
    private int id;
    private int usuarioId;
    private String tipo;
    private double monto;
    private String descripcion;
    private LocalDate fecha;
    private double saldoDespues;
    private int categoriaId; // Agregar este campo


    // Constructores
    public Transaccion() {
        this.fecha = LocalDate.now();
    }

    public Transaccion(int usuarioId, String tipo, double monto, String descripcion) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = LocalDate.now();
    }

    public Transaccion(int usuarioId, String tipo, double monto, String descripcion, int categoriaId) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getSaldoDespues() { return saldoDespues; }
    public void setSaldoDespues(double saldoDespues) { this.saldoDespues = saldoDespues; }

    // Getter y Setter para categoriaId
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public boolean isIngreso() {
        return "Ingreso".equals(tipo);
    }

    public boolean isGasto() {
        return "Gasto".equals(tipo);
    }

    @Override
    public String toString() {
        return "Transaccion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", tipo='" + tipo + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", saldoDespues=" + saldoDespues +
                ", categoriaId=" + categoriaId +
                '}';
    }
}
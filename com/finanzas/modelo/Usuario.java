package finanzas.modelo;

import java.time.LocalDateTime;

public class Usuario {

    private int id;
    private String nombre;
    private byte edad;
    private String tipoUso;
    private String contrasena;
    private double presupuestoInicial;
    private double presupuestoActual;
    private LocalDateTime fechaCreacion;

    public Usuario() {}

    public Usuario(String nombre, byte edad, String tipoUso, String contrasena, double presupuestoInicial) {

        this.nombre = nombre;
        this.edad = edad;
        this.tipoUso = tipoUso;
        this.contrasena = contrasena;
        this.presupuestoInicial = presupuestoInicial;
        this.presupuestoActual = presupuestoInicial;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public byte getEdad() { return edad; }
    public void setEdad(byte edad) { this.edad = edad; }

    public String getTipoUso() { return tipoUso; }
    public void setTipoUso(String tipoUso) { this.tipoUso = tipoUso; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public double getPresupuestoInicial() { return presupuestoInicial; }
    public void setPresupuestoInicial(double presupuestoInicial) { this.presupuestoInicial = presupuestoInicial; }

    public double getPresupuestoActual() { return presupuestoActual; }
    public void setPresupuestoActual(double presupuestoActual) { this.presupuestoActual = presupuestoActual; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
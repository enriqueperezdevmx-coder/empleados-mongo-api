package com.academia.empleados.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document("empleados")
public class Empleado {

    @Id
    private String id;

    private String nombre;
    private String apellidos;

    @Indexed(unique = true)
    private String email;

    private String puesto;
    private String departamento;
    private BigDecimal salario;
    private LocalDate fechaIngreso;
    private boolean activo = true;

    protected Empleado() {
    }

    public Empleado(String nombre, String apellidos, String email, String puesto,
                    String departamento, BigDecimal salario, LocalDate fechaIngreso) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.puesto = puesto;
        this.departamento = departamento;
        this.salario = salario;
        this.fechaIngreso = fechaIngreso;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
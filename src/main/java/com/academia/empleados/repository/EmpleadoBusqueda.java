package com.academia.empleados.repository;

import com.academia.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface EmpleadoBusqueda {
    Page<Empleado> buscar(String departamento, String texto, Boolean activo,
                          BigDecimal salarioMinimo, BigDecimal salarioMaximo, Pageable pageable);
}
package com.academia.empleados.repository;

import com.academia.empleados.entity.Empleado;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;

public interface EmpleadoRepository extends MongoRepository<Empleado, String>, EmpleadoBusqueda {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, String id);

    List<Empleado> findByDepartamentoIgnoreCaseOrderByApellidosAsc(String departamento);

    List<Empleado> findBySalarioBetweenOrderBySalarioDesc(BigDecimal minimo, BigDecimal maximo);
}
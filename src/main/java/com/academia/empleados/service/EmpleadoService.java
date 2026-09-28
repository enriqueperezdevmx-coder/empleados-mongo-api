package com.academia.empleados.service;

import com.academia.empleados.dto.EmpleadoRequest;
import com.academia.empleados.dto.EmpleadoResponse;
import com.academia.empleados.dto.PaginaResponse;
import com.academia.empleados.entity.Empleado;
import com.academia.empleados.exception.EmailDuplicadoException;
import com.academia.empleados.exception.EmpleadoNoEncontradoException;
import com.academia.empleados.repository.EmpleadoRepository;
import org.springframework.data.core.PropertyPath;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public PaginaResponse<EmpleadoResponse> listar(Pageable pageable) {
        return PaginaResponse.desde(repository.findAll(pageable), EmpleadoResponse::desde);
    }

    public PaginaResponse<EmpleadoResponse> buscar(String departamento, String texto, Boolean activo,
                                                   BigDecimal salarioMinimo, BigDecimal salarioMaximo,
                                                   Pageable pageable) {
        validarOrden(pageable);
        return PaginaResponse.desde(
                repository.buscar(departamento, texto, activo, salarioMinimo, salarioMaximo, pageable),
                EmpleadoResponse::desde);
    }

    public List<EmpleadoResponse> porDepartamento(String departamento) {
        return repository.findByDepartamentoIgnoreCaseOrderByApellidosAsc(departamento).stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    public List<EmpleadoResponse> porRangoDeSalario(BigDecimal minimo, BigDecimal maximo) {
        return repository.findBySalarioBetweenOrderBySalarioDesc(minimo, maximo).stream()
                .map(EmpleadoResponse::desde)
                .toList();
    }

    public EmpleadoResponse buscarPorId(String id) {
        return EmpleadoResponse.desde(obtener(id));
    }

    public EmpleadoResponse crear(EmpleadoRequest datos) {
        if (repository.existsByEmail(datos.email())) {
            throw new EmailDuplicadoException(datos.email());
        }
        Empleado empleado = new Empleado(datos.nombre(), datos.apellidos(), datos.email(), datos.puesto(),
                datos.departamento(), datos.salario(), datos.fechaIngreso());
        empleado.setActivo(datos.activo() != null ? datos.activo() : true);
        return EmpleadoResponse.desde(repository.save(empleado));
    }

    public EmpleadoResponse actualizar(String id, EmpleadoRequest datos) {
        Empleado empleado = obtener(id);
        if (repository.existsByEmailAndIdNot(datos.email(), id)) {
            throw new EmailDuplicadoException(datos.email());
        }
        empleado.setNombre(datos.nombre());
        empleado.setApellidos(datos.apellidos());
        empleado.setEmail(datos.email());
        empleado.setPuesto(datos.puesto());
        empleado.setDepartamento(datos.departamento());
        empleado.setSalario(datos.salario());
        empleado.setFechaIngreso(datos.fechaIngreso());
        empleado.setActivo(datos.activo() != null ? datos.activo() : true);
        return EmpleadoResponse.desde(repository.save(empleado));
    }

    public void eliminar(String id) {
        repository.delete(obtener(id));
    }

    private void validarOrden(Pageable pageable) {
        pageable.getSort().forEach(orden -> PropertyPath.from(orden.getProperty(), Empleado.class));
    }

    private Empleado obtener(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontradoException(id));
    }
}
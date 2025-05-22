package Panaca.service.implement;

import Panaca.dto.cupon.*;
import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import Panaca.service.service.CuponService;
import Panaca.exceptions.CuponException;
import Panaca.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated

public class CuponServiceImp implements CuponService {

    private final CuponRepository cuponRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void crearCupon(CrearCuponDTO cuponDTO) throws CuponException {
        // Verifica si ya existe un cupon con el mismo codigo
        if (cuponRepository.existsByCodigo(cuponDTO.codigo())) {
            throw new CuponException("Ya existe un cupon registrada con este codigo.");
        }

        // Crear instancia de Cupon con datos del DTO
        Cupon nuevoCupon = new Cupon();
        nuevoCupon.setNombre(cuponDTO.nombre());
        nuevoCupon.setCodigo(cuponDTO.codigo());
        nuevoCupon.setFechaVencimiento(cuponDTO.fechaVencimiento());
        nuevoCupon.setDescuento(cuponDTO.porcentajeDescuento());
        nuevoCupon.setTipo(cuponDTO.tipoCupon());
        nuevoCupon.setEstado(cuponDTO.estadoCupon());

        // Verificar si la fecha de apertura está presente en el DTO
        if (cuponDTO.fechaApertura() != null) {
            nuevoCupon.setFechaApertura(cuponDTO.fechaApertura());
        } else {
            // Si no se especifica, se asigna la fecha y hora actuales
            nuevoCupon.setFechaApertura(LocalDateTime.now());
        }

        // Guardar el cupon en la base de datos
        cuponRepository.save(nuevoCupon);
    }

    @Override
    public void editarCupon(EditarCuponDTO cuponDTO, String cuponId) throws CuponException {
        // Validar que el DTO no sea nulo y que el ID no sea nulo
        if (cuponDTO == null || cuponId == null || cuponId.isEmpty()) {
            throw new CuponException("Los datos de cuenta o el id no pueden ser nulos");
        }

        // Buscar el cupon en la base de datos usando un identificador único
        Cupon cuponExistente = cuponRepository.findById(cuponId)
                .orElseThrow(() -> new CuponException("Cuenta no encontrada"));

        // Actualizar los datos del objeto Cupon
        cuponExistente.setNombre(cuponDTO.nombre());
        cuponExistente.setCodigo(cuponDTO.codigo());
        cuponExistente.setEstado(cuponDTO.estadoCupon());
        cuponExistente.setTipo(cuponDTO.tipoCupon());
        cuponExistente.setDescuento(cuponDTO.porcentajeDescuento());
        cuponExistente.setFechaVencimiento(cuponDTO.fechaVencimiento());


        if (cuponDTO.fechaApertura() != null) {
            cuponExistente.setFechaApertura(cuponDTO.fechaApertura());
        } else {
            cuponExistente.setFechaApertura(LocalDateTime.now());
        }

        // Guardar los cambios en la base de datos
        cuponRepository.save(cuponExistente);
    }

    @Override
    public void eliminarCupon(String id) throws CuponException {
        // Validar que el ID no sea nulo o vacío
        if (id == null || id.isEmpty()) {
            throw new CuponException("El ID del cupon no puede ser nulo o vacío");
        }

        // Buscar el cupon en la base de datos usando el ID proporcionado
        Cupon cuponExistente = cuponRepository.findById(id)
                .orElseThrow(() -> new CuponException("Cupon no encontrado"));

        // Eliminar el cupon
        cuponRepository.delete(cuponExistente);
    }

    @Override
    public InformacionCuponDTO obtenerInformacionCupon(String id) throws CuponException {
        // Validar que el ID no sea nulo o vacío
        if (id == null || id.isEmpty()) {
            throw new CuponException("El ID del cupon no puede ser nulo o vacío");
        }
        Cupon cuponABuscar = cuponRepository.findById(id)
                .orElseThrow(() -> new CuponException("Cupon no encontrado"));

        return new InformacionCuponDTO(
                cuponABuscar.getNombre(),
                cuponABuscar.getCodigo(),
                cuponABuscar.getDescuento(),
                cuponABuscar.getFechaApertura(),
                cuponABuscar.getFechaVencimiento(),
                cuponABuscar.getTipo(),
                cuponABuscar.getEstado()
        );
    }

    @Override
    public List<ItemsCuponDTO> obtenerCuponesFiltrados(ItemsCuponFiltroDTO filtro) {
        List<Criteria> criterios = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            criterios.add(Criteria.where("nombre").regex(filtro.nombre(), "i"));
        }

        if (filtro.fechaVencimiento() != null) {
            criterios.add(Criteria.where("fechaVencimiento").gte(filtro.fechaVencimiento()));
        }

        if (filtro.fechaApertura() != null) {
            criterios.add(Criteria.where("fechaApertura").gt(filtro.fechaApertura()));
        }

        if (filtro.descuento() != null) {
            criterios.add(Criteria.where("descuento").is(filtro.descuento()));
        }

        if (filtro.tipo() != null) {
            criterios.add(Criteria.where("tipo").is(filtro.tipo()));
        }

        if (filtro.estado() != null) {
            criterios.add(Criteria.where("estado").is(filtro.estado()));
        }

        Query query = new Query();
        if (!criterios.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criterios.toArray(new Criteria[0])));
        }

        List<Cupon> cupones = mongoTemplate.find(query, Cupon.class);

        return cupones.stream()
                .map(c -> new ItemsCuponDTO(
                        c.getNombre(),
                        c.getFechaVencimiento(),
                        c.getFechaApertura(),
                        c.getDescuento(),
                        c.getTipo(),
                        c.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Cupon> getAllDisponibles(PageRequest pageRequest){
        return cuponRepository.findByEstado(EstadoCupon.DISPONIBLE,pageRequest);
    }
    @Override
    public Page<Cupon> getAllNoDisponibles(PageRequest pageRequest){
        return cuponRepository.findByEstado(EstadoCupon.NO_DISPONIBLE,pageRequest);
    }

    @Override
    public Cupon aplicarCupon(String codigoCupon, LocalDateTime fechaCompra) throws CuponException {
        Cupon cupon = cuponRepository.findByCodigo(codigoCupon)
                .orElseThrow(() -> new CuponException("Cupón no existe."));

        if (cupon.getEstado() != EstadoCupon.DISPONIBLE) {
            throw new CuponException("El cupón no está disponible.");
        }

        if (cupon.getFechaVencimiento().isBefore(fechaCompra)) {
            throw new CuponException("El cupón ha expirado.");
        }

        if (cupon.getTipo() == TipoCupon.UNICO && cupon.isUtilizado()) {
            throw new CuponException("El cupón ya ha sido utilizado.");
        }

        if (cupon.getTipo() == TipoCupon.UNICO) {
            cupon.setUtilizado(true);
            cuponRepository.save(cupon);
        }

        return cupon;
    }

}
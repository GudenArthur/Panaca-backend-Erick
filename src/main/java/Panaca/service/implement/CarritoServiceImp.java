package Panaca.service.implement;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Evento;
import Panaca.model.vo.DetalleCarrito;
import Panaca.service.service.CarritoService;
import Panaca.exceptions.CarritoException;
import Panaca.repository.CarritoRepository;
import Panaca.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class CarritoServiceImp implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final EventoRepository eventoRepository;

    @Override
    public void crearCarrito(CrearCarritoDTO carritoDTO) {

        // Crear la instancia de Carrito usando el DTO
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(carritoDTO.idUsuario());
        carrito.setItems(new ArrayList<>());  // Inicialmente el carrito estará vacío

        // Guardar el carrito en la base de datos
        carritoRepository.save(carrito);
    }

    @Override
    public Carrito obtenerCarritoPorUsuario(String idUsuario) throws CarritoException {
        return carritoRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new CarritoException("No se encontró un carrito para este usuario."));
    }

    @Override
    public Carrito agregarItemsAlCarrito(String idUsuario, List<DetalleCarritoDTO> nuevosItemsDTO) throws CarritoException {
        if (nuevosItemsDTO == null || nuevosItemsDTO.isEmpty()) {
            throw new CarritoException("La lista de ítems no puede estar vacía.");
        }
        // Obtener el carrito actual del usuario
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        // Convertir los nuevos items de DTO a DetalleCarrito
        List<DetalleCarrito> nuevosItems = convertirItemsDTOAItems(nuevosItemsDTO);

        // Índice para mejorar rendimiento
        Map<String, DetalleCarrito> itemsExistentesMap = carrito.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getIdEvento() + "|" + item.getFechaUso(),
                        item -> item,
                        (a, b) -> a
                ));

        // Agregar los nuevos ítems al carrito existente
        for (DetalleCarrito nuevoItem : nuevosItems) {
            String clave = nuevoItem.getIdEvento() + "|" + nuevoItem.getFechaUso();
            DetalleCarrito existente = itemsExistentesMap.get(clave);
            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + nuevoItem.getCantidad());
            } else {
                carrito.getItems().add(nuevoItem);
                itemsExistentesMap.put(clave, nuevoItem);
            }
        }
        // Guardar y retornar el carrito actualizado
        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito eliminarItemDelCarrito(String idUsuario, String idEvento, LocalDate fechaUso) throws CarritoException {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        boolean itemEliminado = carrito.getItems().removeIf(item ->
                item.getIdEvento().equals(idEvento) && item.getFechaUso().equals(fechaUso)
        );

        if (!itemEliminado) {
            throw new CarritoException("No se encontró el evento en el carrito del usuario con la fecha especificada.");
        }

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito vaciarCarrito(String idUsuario) throws CarritoException {
        // Obtener el carrito del usuario
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        // Vaciar la lista de ítems
        carrito.setItems(new ArrayList<>());

        // Guardar el carrito vacío
        return carritoRepository.save(carrito);
    }

    @Override
    public List<InformacionEventoCarritoDTO> listarProductosEnCarrito(String idUsuario) throws CarritoException {
        // Obtener el carrito actual del usuario
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        // Lista para almacenar los detalles del carrito junto con la información del evento
        List<InformacionEventoCarritoDTO> detallesConEventos = new ArrayList<>();

        // Iterar sobre los ítems del carrito
        for (DetalleCarrito item : carrito.getItems()) {
            // Obtener el evento asociado al ítem
            Evento evento = eventoRepository.findById(String.valueOf(item.getIdEvento()))
                    .orElseThrow(() -> new CarritoException("El evento con ID " + item.getIdEvento() + " no existe."));

            //Convertir DetalleCarrito en DetalleCarritoDTO
            DetalleCarritoDTO detalle = new DetalleCarritoDTO(
                    item.getIdEvento(),
                    item.getCantidad(),
                    item.getFechaUso()
            );

            // Crear un DTO con la información del evento
            InformacionEventoCarritoDTO informacionEvento = new InformacionEventoCarritoDTO(
                    detalle,
                    evento.getImagenPortada(),
                    evento.getNombre()
            );

            // Agregarlo a la lista
            detallesConEventos.add(informacionEvento);
        }

        // Retornar la lista de ítems con la información de sus eventos
        return detallesConEventos;
    }

    @Override
    public double calcularTotalCarrito(String idUsuario) throws CarritoException {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        return carrito.getItems().stream()
                .mapToDouble(item -> {
                    Evento evento = eventoRepository.findById(item.getIdEvento())
                            .orElseThrow(() -> new RuntimeException("Evento no encontrado para el ID: " + item.getIdEvento()));
                    return evento.getPrecio() * item.getCantidad();
                })
                .sum();
    }

    private List<DetalleCarrito> convertirItemsDTOAItems(List<DetalleCarritoDTO> itemsCarritoDTO) {
        return itemsCarritoDTO.stream()
                .map(dto -> new DetalleCarrito(
                        dto.idEvento(),
                        dto.cantidad(),
                        dto.fechaUso() // Establecemos la fecha de agregación como el momento actual
                ))
                .collect(Collectors.toList());
    }
}
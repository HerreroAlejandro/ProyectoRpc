package org.example.ProyectoGrpc.servicio.implementacion;

import jakarta.annotation.PostConstruct;
import org.example.ProyectoGrpc.entidad.DonacionesEvento;
import org.example.ProyectoGrpc.entidad.EventoSolidario;
import org.example.ProyectoGrpc.entidad.InventarioDonaciones;
import org.example.ProyectoGrpc.entidad.Usuario;
import org.example.ProyectoGrpc.enums.RolUsuario;
import org.example.ProyectoGrpc.repositorioDao.DonacionesEventoDao;
import org.example.ProyectoGrpc.repositorioDao.EventoSolidarioDao;
import org.example.ProyectoGrpc.repositorioDao.InventarioDonacionesDao;
import org.example.ProyectoGrpc.repositorioDao.UsuarioDao;
import org.example.ProyectoGrpc.servicio.EventoSolidarioServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.myorg.kafka_module.dto.EventoDTO;
import com.myorg.kafka_module.producer.EventoProducer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class EventoSolidarioServicioImp implements EventoSolidarioServicio {

    private final EventoSolidarioDao eventoDao;
    private final InventarioDonacionesDao inventarioDao;
    private final DonacionesEventoDao donacionesEventoDao;
    private final UsuarioDao usuarioDao;
    private final EventoProducer eventoProducer;

    public EventoSolidarioServicioImp(EventoSolidarioDao eventoDao, InventarioDonacionesDao inventarioDao, DonacionesEventoDao donacionesEventoDao, UsuarioDao usuarioDao,
                                      EventoProducer eventoProducer) {
        this.eventoDao = eventoDao;
        this.inventarioDao = inventarioDao;
        this.donacionesEventoDao = donacionesEventoDao;
        this.usuarioDao = usuarioDao;
        this.eventoProducer = eventoProducer;
    }

    @PostConstruct
    public void republicarEventosExistentes() {

        List<EventoSolidario> eventos = eventoDao.listarTodos();

        System.out.println("====================================");
        System.out.println("REPUBLICANDO EVENTOS EXISTENTES");
        System.out.println("Eventos encontrados en BD: " + eventos.size());

        for (EventoSolidario evento : eventos) {

            EventoDTO eventoDTO = new EventoDTO();

            eventoDTO.setIdOrganizacion("ONG001");
            eventoDTO.setIdEvento(String.valueOf(evento.getId()));
            eventoDTO.setNombreEvento(evento.getNombreEvento());
            eventoDTO.setDescripcion(evento.getDescripcion());
            eventoDTO.setFechaHora(evento.getFechaHoraEvento());

            eventoProducer.enviarEvento(eventoDTO);

            System.out.println(
                    "Evento republicado: " +
                            evento.getId() + " - " +
                            evento.getNombreEvento()
            );
        }

        System.out.println("====================================");
    }

    @Override
    @Transactional
    public EventoSolidario altaEvento(EventoSolidario evento) {

        LocalDateTime fechaEvento = evento.getFechaHoraEvento();

        if (fechaEvento.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear eventos pasados");
        }

        // 1. Guardamos primero en nuestra BD
        eventoDao.guardar(evento);

        // 2. Armamos el mensaje que vamos a publicar en Kafka
        EventoDTO eventoDTO = new EventoDTO();

        eventoDTO.setIdOrganizacion("ONG001");
        eventoDTO.setIdEvento(String.valueOf(evento.getId()));
        eventoDTO.setNombreEvento(evento.getNombreEvento());
        eventoDTO.setDescripcion(evento.getDescripcion());
        eventoDTO.setFechaHora(evento.getFechaHoraEvento());

        // 3. Publicamos el evento en Kafka
        eventoProducer.enviarEvento(eventoDTO);

        return evento;
    }

    @Override
    @Transactional
    public EventoSolidario modificarEvento(Long id, String nombre, String descripcion, LocalDateTime fechaHora, Set<Usuario> participantes) {
        EventoSolidario evento = eventoDao.buscarPorId(id);
        if (evento == null) return null;

        evento.setNombreEvento(nombre);
        evento.setDescripcion(descripcion);
        evento.setFechaHoraEvento(fechaHora);
        evento.setParticipantesEvento(participantes);

        eventoDao.actualizar(evento);
        return evento;
    }

    @Override
    @Transactional
    public EventoSolidario modificarEvento(Long id, EventoSolidario evento) {
        return modificarEvento(
                id,
                evento.getNombreEvento(),
                evento.getDescripcion(),
                evento.getFechaHoraEvento(),
                evento.getParticipantesEvento()
        );
    }

    @Override
    @Transactional
    public void bajaEvento(Long id) {
        EventoSolidario evento = eventoDao.buscarPorId(id);

        if (evento != null && evento.getFechaHoraEvento().isAfter(LocalDateTime.now())) {

            EventoDTO eventoDTO = new EventoDTO();
            eventoDTO.setIdOrganizacion("ONG001");
            eventoDTO.setIdEvento(String.valueOf(evento.getId()));
            eventoDTO.setNombreEvento(evento.getNombreEvento());
            eventoDTO.setDescripcion(evento.getDescripcion());
            eventoDTO.setFechaHora(evento.getFechaHoraEvento());

            eventoDao.eliminar(id);

            eventoProducer.enviarBajaEvento(eventoDTO);
        }
    }

    @Override
    public EventoSolidario buscarPorId(Long id) {
        return eventoDao.buscarPorId(id);
    }

    @Override
    public List<EventoSolidario> listarTodos() {
        return eventoDao.listarTodos();
    }

    @Override
    @Transactional
    public void agregarMiembro(Long eventoId, Usuario usuario, String rolSolicitante) {
        EventoSolidario evento = eventoDao.buscarPorId(eventoId);
        if (evento == null || usuario == null) return;

        boolean puedeAgregar = rolSolicitante.equals("PRESIDENTE") ||
                rolSolicitante.equals("COORDINADOR") ||
                (rolSolicitante.equals("VOLUNTARIO") && !evento.getParticipantesEvento().contains(usuario));

        if (puedeAgregar && usuario.isActivo()) {
            evento.getParticipantesEvento().add(usuario);
            eventoDao.actualizar(evento);
        }
    }

    @Override
    @Transactional
    public void quitarMiembro(Long eventoId, Usuario usuario, String rolSolicitante) {
        EventoSolidario evento = eventoDao.buscarPorId(eventoId);
        if (evento == null || usuario == null) return;

        boolean puedeQuitar = rolSolicitante.equals("PRESIDENTE") ||
                rolSolicitante.equals("COORDINADOR") ||
                (rolSolicitante.equals("VOLUNTARIO") && evento.getParticipantesEvento().contains(usuario));

        if (puedeQuitar) {
            boolean removed = evento.getParticipantesEvento().removeIf(u -> u.getId().equals(usuario.getId()));
            if (removed) {
                eventoDao.actualizar(evento);
            }
        }
    }

    @Override
    @Transactional
    public void registrarDonacionEvento(Long eventoId, Long inventarioId, int cantidad, Long usuarioId) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        EventoSolidario evento = eventoDao.buscarPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento no encontrado");
        }
        if (evento.getFechaHoraEvento().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden registrar donaciones para eventos futuros");
        }

        InventarioDonaciones item = inventarioDao.buscarPorId(inventarioId);
        if (item == null) {
            throw new IllegalArgumentException("Inventario no encontrado");
        }
        if (cantidad > item.getCantidad()) {
            throw new IllegalArgumentException("Cantidad insuficiente en inventario");
        }

        Usuario usuario = usuarioDao.buscarPorId(usuarioId);
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("Usuario no válido");
        }
        if (!(usuario.getRol() == RolUsuario.PRESIDENTE || usuario.getRol() == RolUsuario.COORDINADOR)) {
            throw new IllegalArgumentException("Solo PRESIDENTE o COORDINADOR pueden registrar donaciones");
        }

        // Descontar del inventario primero
        item.setCantidad(item.getCantidad() - cantidad);
        inventarioDao.actualizar(item);

        // Crear registro de donación
        DonacionesEvento donacion = new DonacionesEvento();
        donacion.setEvento(evento);
        donacion.setItem(item);
        donacion.setUsuario(usuario);
        donacion.setCantidad(cantidad);
        donacionesEventoDao.guardar(donacion);
    }
}
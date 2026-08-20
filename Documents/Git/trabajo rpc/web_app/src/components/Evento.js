import React, { useEffect, useState } from "react";
import {
  listarEventos,
  listarUsuarios,
  listarInventario,
  asignarParticipantesEvento,
  quitarParticipantesEvento,
  registrarDonacionEvento,
  altaEvento,
  modificarEvento,
  bajaEvento
} from "../servicios/grpcCliente";
import "./Evento.css";

const Evento = ({ usuarioLogueado }) => {

  console.log("USUARIO LOGUEADO:", usuarioLogueado);
  
  const [eventos, setEventos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [inventario, setInventario] = useState([]);
  const [selectedEvento, setSelectedEvento] = useState(null);
  const [selectedUsuario, setSelectedUsuario] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  const [cantidadDonacion, setCantidadDonacion] = useState(0);

  // Datos para crear/modificar eventos
  const [nuevoNombre, setNuevoNombre] = useState("");
  const [nuevaDescripcion, setNuevaDescripcion] = useState("");
  const [nuevaFechaHora, setNuevaFechaHora] = useState("");

  const [editandoEvento, setEditandoEvento] = useState(null);
  const [editNombre, setEditNombre] = useState("");
  const [editDescripcion, setEditDescripcion] = useState("");
  const [editFecha, setEditFecha] = useState("");


  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const listaEventos = await listarEventos();
      console.log("Eventos recibidos del servidor:", listaEventos);
      setEventos(listaEventos);

      const listaUsuarios = await listarUsuarios();
      setUsuarios(listaUsuarios);

      const listaInventario = await listarInventario();
      setInventario(listaInventario);
    } catch (err) {
      console.error("Error cargando datos:", err);
    }
  };

  const handleCrearEvento = async () => {
    if (!nuevoNombre || !nuevaDescripcion || !nuevaFechaHora) return;
    if (
  usuarioLogueado.rol !== "PRESIDENTE" &&
  usuarioLogueado.rol !== "COORDINADOR"
) {
  alert("❌ Solo PRESIDENTE o COORDINADOR pueden crear eventos");
  return;
}
    try {
      const fechaConSegundos = `${nuevaFechaHora}:00`;
      await altaEvento({
        nombreEvento: nuevoNombre,
        descripcion: nuevaDescripcion,
        fechaHoraEvento: fechaConSegundos,
        participantesEvento: []
      });
      await cargarDatos();
      alert("✅ Evento creado!");
      setNuevoNombre("");
      setNuevaDescripcion("");
      setNuevaFechaHora("");
    } catch (err) {
      console.error(err);
      alert("❌ Error creando evento");
    }
  };

  const handleGuardarCambios = async (evento) => {
  try {
    await modificarEvento({
      id: evento.id,
      nombreEvento: editNombre,
      descripcion: editDescripcion,
      fechaHoraEvento: `${editFecha}:00`,
    });
    await cargarDatos();
    alert("✅ Evento actualizado");
    setEditandoEvento(null);
  } catch (err) {
    console.error(err);
    alert("❌ Error actualizando evento");
  }
};


  const handleEliminarEvento = async (eventoId) => {
    try {
      await bajaEvento(eventoId);
      await cargarDatos();
      alert("🗑️ Evento eliminado!");
    } catch (err) {
      console.error(err);
      alert("❌ Error eliminando evento");
    }
  };

  // Asignar participante
const handleAsignarParticipante = async (evento, usuario) => {
  if (!evento || !usuario || !usuarioLogueado) return;

  if (usuarioLogueado.rol === "VOLUNTARIO" && usuario.id !== usuarioLogueado.id) {
    alert("❌ Los voluntarios solo pueden agregarse a sí mismos");
    return;
  }

  try {
    const actualizado = await asignarParticipantesEvento(
      evento.id,
      [usuario.id],
      usuarioLogueado
    );

    // Actualizar el evento seleccionado con la lista de participantes del backend
    setSelectedEvento(prev => ({
      ...prev,
      participantesEvento: actualizado.participantesEvento || []
    }));

    await cargarDatos(); // si querés recargar toda la lista de eventos
    alert("👥 Participante asignado!");
  } catch (err) {
    console.error(err);
    alert("❌ Error asignando participante");
  }
};

// Quitar participante
const handleQuitarParticipante = async (evento, usuario) => {
  if (!evento || !usuario || !usuarioLogueado) return;

  if (usuarioLogueado.rol === "VOLUNTARIO" && usuario.id !== usuarioLogueado.id) {
    alert("❌ Los voluntarios solo pueden quitarse a sí mismos");
    return;
  }

  try {
    const actualizado = await quitarParticipantesEvento(
      evento.id,
      [usuario.id],
      usuarioLogueado
    );

    // Actualizar el evento seleccionado con la lista de participantes del backend
    setSelectedEvento(prev => ({
      ...prev,
      participantesEvento: actualizado.participantesEvento || []
    }));

    await cargarDatos();
    alert("🚫 Participante quitado!");
  } catch (err) {
    console.error(err);
    alert("❌ Error quitando participante");
  }
};

// Registrar donación
const handleRegistrarDonacion = async (evento, item, cantidad) => {
  if (!evento || !item || cantidad <= 0 || !usuarioLogueado) return;

  try {
    await registrarDonacionEvento(
      evento.id,
      item.id,
      cantidad,
      usuarioLogueado
    );
    await cargarDatos();
    alert("💝 Donación registrada!");
    setCantidadDonacion(0);
  } catch (err) {
    console.error("Error registrando donación:", err);
    alert(`❌ Error registrando donación: ${err.message}`);
  }
};

  // Formatear fecha y hora
  const formatearFecha = (ts) => {
  const date = timestampGrpcToDate(ts);
  if (!date || isNaN(date)) return "Fecha inválida";
  return date.toLocaleString("es-AR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
};

  const timestampGrpcToDate = (ts) => {
  if (!ts) return null;

  // Si viene como objeto con seconds (Protobuf Timestamp)
  if (ts.seconds !== undefined) {
    return new Date(ts.seconds * 1000);
  }

  // Si ya es string o Date
  return new Date(ts);
};

  const esEventoPasado = (evento) => {
    const fechaEvento = timestampGrpcToDate(evento.fechahoraevento);
    return fechaEvento < new Date();
  };


  return (
    <div className="evento-card">
      <h2 className="section-title">📅 Gestión de Eventos</h2>

      {/* Crear Evento */}
      <div className="crear-evento">
        <h3>➕ Crear Evento</h3>
        <input
          placeholder="Nombre"
          value={nuevoNombre}
          onChange={(e) => setNuevoNombre(e.target.value)}
        />
        <input
          placeholder="Descripción"
          value={nuevaDescripcion}
          onChange={(e) => setNuevaDescripcion(e.target.value)}
        />
        <input
          type="datetime-local"
          value={nuevaFechaHora}
          onChange={(e) => setNuevaFechaHora(e.target.value)}
        />
        <button onClick={handleCrearEvento}>Crear</button>
      </div>

      {/* Lista de eventos */}
      <ul className="evento-list">
        {eventos.map((e) => (
          <li key={e.id} className="evento-item">
            <div className="evento-header">
              <div className="evento-info">
                <h3 className="evento-nombre">🎉 {e.nombreevento}</h3>
                <p className="evento-fecha">🕒 {formatearFecha(e.fechahoraevento)}</p>
              </div>
              <div className="evento-btn">
                {editandoEvento === e.id ? (
                  <>
                    <button onClick={() => handleGuardarCambios(e)}>💾 Guardar</button>
                    <button onClick={() => setEditandoEvento(null)}>❌ Cancelar</button>
                  </>
                ) : (
                  <>
                    <button onClick={() => {
                      setEditandoEvento(e.id);
                      setEditNombre(e.nombreevento);
                      setEditDescripcion(e.descripcion);
                      setEditFecha(e.fechahoraevento?.seconds
                        ? new Date(e.fechahoraevento.seconds * 1000).toISOString().slice(0,16)
                        : e.fechahoraevento);
                    }}>
                      ✏️ Modificar
                    </button>
                    <button onClick={() => handleEliminarEvento(e.id)}>🗑️ Eliminar</button>
                  </>
                )}
              </div>
            </div>

            {editandoEvento === e.id ? (
              <div className="editar-evento-form">
                <input
                  value={editNombre}
                  onChange={(ev) => setEditNombre(ev.target.value)}
                  placeholder="Nuevo nombre"
                />
                <input
                  value={editDescripcion}
                  onChange={(ev) => setEditDescripcion(ev.target.value)}
                  placeholder="Nueva descripción"
                />
                <input
                  type="datetime-local"
                  value={editFecha}
                  onChange={(ev) => setEditFecha(ev.target.value)}
                />
              </div>
            ) : (
              <p className="evento-desc">{e.descripcion}</p>
            )}

            {e.participanteseventoList?.length > 0 && (
              <ul className="participantes-list">
                <h4>Participantes</h4>
                {e.participanteseventoList.map((p) => (
                  <li key={p.id}>👤 {p.nombre} {p.apellido}, {p.rol}</li>
                ))}
              </ul>
            )}
            

            {/* Asignar/Quitar Participantes */}
            {!esEventoPasado(e) && (
            <div className="evento-actions">
              <select
                onChange={(ev) =>
                  setSelectedUsuario(usuarios.find(u => u.id === parseInt(ev.target.value)))
                }
                defaultValue=""
              >
                <option value="" disabled>Seleccionar usuario</option>
                {usuarios.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.nombre} {u.apellido} ({u.rol})
                  </option>
                ))}
              </select>
              <button onClick={() => handleAsignarParticipante(e, selectedUsuario)}>➕ Asignar</button>
              <button onClick={() => handleQuitarParticipante(e, selectedUsuario)}>➖ Quitar</button>
            </div>
)}

            {/* Registrar donación */}
            {esEventoPasado(e) && (
              <div className="donacion-form">
                <select
                  onChange={(ev) =>
                    setSelectedItem(inventario.find(i => i.id === parseInt(ev.target.value)))
                  }
                  defaultValue=""
                >
                  <option value="" disabled>Seleccionar item</option>
                  {inventario.map((i) => (
                    <option key={i.id} value={i.id}>
                      {i.descripcion} ({i.cantidad} disponibles)
                    </option>
                  ))}
                </select>
                <input
                  type="number"
                  min="1"
                  value={cantidadDonacion}
                  onChange={(e) => setCantidadDonacion(parseInt(e.target.value))}
                  placeholder="Cantidad"
                />
                <button onClick={() => handleRegistrarDonacion(e, selectedItem, cantidadDonacion)}>💝 Registrar Donación</button>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Evento;

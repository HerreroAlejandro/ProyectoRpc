import React, { useState, useEffect } from "react";
import {
  enviarTransferencia,
  listarSolicitudes,
  listarOfertas,
} from "../servicios/donacionesCliente";
import "./DonacionesYeventosKf.css";

const TransferirDonacion = ({ idOrganizacionDonante }) => {
  const [donaciones, setDonaciones] = useState([]);
  const [categoria, setCategoria] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [cantidad, setCantidad] = useState("");
  const [mensaje, setMensaje] = useState("");

  const [solicitudesExternas, setSolicitudesExternas] = useState([]);
  const [ofertas, setOfertas] = useState([]);

  const [idSolicitudSeleccionada, setIdSolicitudSeleccionada] = useState("");
  const [idOrgReceptoraSeleccionada, setIdOrgReceptoraSeleccionada] =
    useState("");

  // --------------------------------------------------
  // CARGAR SOLICITUDES Y OFERTAS
  // --------------------------------------------------

  useEffect(() => {
    const fetchDatos = async () => {
      try {
        const solicitudes = await listarSolicitudes();
        const ofertasRecibidas = await listarOfertas();

        console.log("[TransferirDonacion] Solicitudes recibidas:", solicitudes);
        console.log("[TransferirDonacion] Ofertas recibidas:", ofertasRecibidas);

        setSolicitudesExternas(solicitudes);
        setOfertas(ofertasRecibidas);
      } catch (err) {
        console.error(
          "[TransferirDonacion] Error al listar solicitudes/ofertas:",
          err
        );
      }
    };

    fetchDatos();

    const interval = setInterval(fetchDatos, 5000);

    return () => clearInterval(interval);
  }, []);

  // --------------------------------------------------
  // AGREGAR ITEM A LA TRANSFERENCIA
  // --------------------------------------------------

  const agregarItem = () => {
    const cantidadNumerica = parseInt(cantidad);

    if (!categoria || !descripcion || cantidad === "") {
      setMensaje(
        "Debe completar categoría, descripción y cantidad para agregar un ítem."
      );
      return;
    }

    if (isNaN(cantidadNumerica) || cantidadNumerica <= 0) {
      setMensaje("La cantidad debe ser mayor a 0.");
      return;
    }

    const nuevoItem = {
      categoria,
      descripcion,
      cantidad: cantidadNumerica,
    };

    setDonaciones([...donaciones, nuevoItem]);

    setCategoria("");
    setDescripcion("");
    setCantidad("");
    setMensaje("");
  };

  // --------------------------------------------------
  // SELECCIONAR UNA SOLICITUD EXTERNA
  // --------------------------------------------------

  const seleccionarSolicitud = (solicitud) => {
    setIdSolicitudSeleccionada(solicitud.idSolicitud);
    setIdOrgReceptoraSeleccionada(solicitud.idOrganizacion);

    /*
     * Mostramos el primer artículo solicitado en el formulario
     * como ayuda, pero NO lo agregamos automáticamente
     * a la transferencia.
     */
    if (solicitud.donaciones && solicitud.donaciones.length > 0) {
      const primerItem = solicitud.donaciones[0];

      setCategoria(primerItem.categoria || "");
      setDescripcion(primerItem.descripcion || "");
      setCantidad("");
    } else {
      setCategoria("");
      setDescripcion("");
      setCantidad("");
    }

    // Muy importante:
    // la lista empieza vacía hasta que el usuario agregue
    // explícitamente la cantidad que quiere donar.
    setDonaciones([]);

    setMensaje("");
  };

  // --------------------------------------------------
  // ENVIAR TRANSFERENCIA
  // --------------------------------------------------

  const enviarTransferenciaClick = async () => {
    if (!donaciones.length) {
      setMensaje(
        "Agrega al menos un ítem con cantidad antes de transferir."
      );
      return;
    }

    if (!idSolicitudSeleccionada || !idOrgReceptoraSeleccionada) {
      setMensaje("Selecciona una solicitud antes de transferir.");
      return;
    }

    try {
      const resp = await enviarTransferencia(
        idSolicitudSeleccionada,
        idOrganizacionDonante,
        idOrgReceptoraSeleccionada,
        donaciones
      );

      console.log(
        "[TransferirDonacion] Respuesta del backend:",
        resp
      );

      setMensaje(
        `Transferencia enviada para la solicitud ${idSolicitudSeleccionada}`
      );

      setDonaciones([]);
      setIdSolicitudSeleccionada("");
      setIdOrgReceptoraSeleccionada("");
      setCategoria("");
      setDescripcion("");
      setCantidad("");
    } catch (err) {
      console.error(
        "[TransferirDonacion] Error al enviar transferencia:",
        err
      );

      setMensaje("Error al realizar transferencia");
    }
  };

  // --------------------------------------------------
  // ORDENAR SOLICITUDES
  // --------------------------------------------------

  /*
   * Las solicitudes propias siempre aparecen primero.
   * Las externas aparecen después.
   *
   * idOrganizacionDonante se convierte a String para evitar
   * problemas si viene como número desde algún lugar.
   */
  const solicitudesOrdenadas = [...solicitudesExternas].sort((a, b) => {
    const aPropia =
      String(a.idOrganizacion) === String(idOrganizacionDonante);

    const bPropia =
      String(b.idOrganizacion) === String(idOrganizacionDonante);

    if (aPropia && !bPropia) return -1;
    if (!aPropia && bPropia) return 1;

    return 0;
  });

  // --------------------------------------------------
  // RENDER
  // --------------------------------------------------

  return (
    <div className="main-container">
      <h2>Transferir Donaciones</h2>

      {/* FORMULARIO */}

      <div className="input-row">
        <input
          type="text"
          placeholder="Categoría"
          value={categoria}
          onChange={(e) => setCategoria(e.target.value)}
        />

        <input
          type="text"
          placeholder="Descripción"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
        />

        <input
          type="number"
          placeholder="Cantidad"
          value={cantidad}
          onChange={(e) => setCantidad(e.target.value)}
        />

        <button className="style-btn" onClick={agregarItem}>
          Agregar
        </button>
      </div>

      {/* ITEMS QUE EL USUARIO VA A TRANSFERIR */}

      {donaciones.length > 0 && (
        <>
          <h3>Ítems a transferir</h3>

          <ul>
            {donaciones.map((d, i) => (
              <li key={i}>
                {d.categoria} - {d.descripcion} ({d.cantidad})
              </li>
            ))}
          </ul>
        </>
      )}

      <button
        className="style-btn"
        onClick={enviarTransferenciaClick}
        disabled={
          !donaciones.length ||
          !idSolicitudSeleccionada ||
          !idOrgReceptoraSeleccionada
        }
      >
        Enviar Transferencia
      </button>

      {/* ==================================================
          SOLICITUDES
          ================================================== */}

      <h3>Solicitudes de Donaciones</h3>

      {solicitudesOrdenadas.length > 0 ? (
        <ul>
          {solicitudesOrdenadas.map((sol, i) => {
            const esPropia =
              String(sol.idOrganizacion) ===
              String(idOrganizacionDonante);

            return (
              <li
                className={`list-solicitudes ${
                  esPropia ? "propia" : "externa"
                }`}
                key={`${sol.idSolicitud}-${i}`}
              >
                <strong>
                  {sol.idSolicitud}
                  {esPropia && " (Propia)"}
                </strong>

                {" — "}

                Organización {sol.idOrganizacion}

                {" — "}

                {sol.donaciones?.length || 0} ítems

                <ul>
                  {(sol.donaciones || []).map((d, j) => (
                    <li key={j}>
                      {d.categoria} - {d.descripcion}
                      {d.cantidad !== undefined &&
                        d.cantidad !== null &&
                        d.cantidad !== 0
                        ? ` (${d.cantidad})`
                        : ""}
                    </li>
                  ))}
                </ul>

                {/* 
                  Las solicitudes propias NO se pueden transferir
                  porque son solicitudes de nuestra organización.
                */}

                {!esPropia && (
                  <button
                    className="style-btn"
                    onClick={() => seleccionarSolicitud(sol)}
                  >
                    Seleccionar para transferir
                  </button>
                )}
              </li>
            );
          })}
        </ul>
      ) : (
        <p>No hay solicitudes de donaciones.</p>
      )}

      {/* ==================================================
          OFERTAS
          ================================================== */}

      <h3>Ofertas de Donaciones</h3>

      {ofertas.length > 0 ? (
        <ul>
          {ofertas.map((oferta, i) => {
            const esPropia =
              String(oferta.idOrganizacionDonante) ===
              String(idOrganizacionDonante);

            return (
              <li
                className={`list-solicitudes ${
                  esPropia ? "propia" : "externa"
                }`}
                key={oferta.idOferta || i}
              >
                <strong>
                  {oferta.idOferta}
                  {esPropia && " (Propia)"}
                </strong>

                {" — "}

                Organización {oferta.idOrganizacionDonante}

                {" — "}

                {oferta.donaciones?.length || 0} ítems

                <ul>
                  {(oferta.donaciones || []).map((d, j) => (
                    <li key={j}>
                      {d.categoria} - {d.descripcion} ({d.cantidad})
                    </li>
                  ))}
                </ul>
              </li>
            );
          })}
        </ul>
      ) : (
        <p>No hay ofertas de donaciones disponibles.</p>
      )}

      {/* MENSAJES */}

      {mensaje && <p className="mensaje">{mensaje}</p>}
    </div>
  );
};

export default TransferirDonacion;
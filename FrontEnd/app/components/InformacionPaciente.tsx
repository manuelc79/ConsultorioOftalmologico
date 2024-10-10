'use client'

import { useEffect, useState } from 'react'
import Modal from './Modal'
import DatosPaciente from './DatosPaciente'
import BotonesAccion from './BotonesAccion'
import HistorialConsultas from './HistorialConsultas'
import EditarPacienteModal from './EditarPacienteModal'
import EliminarPacienteModal from './EliminarPacienteModal'
import NuevaConsultaModal from './NuevaConsultaModal'
import DetalleRecetaModal from './DetalleRecetaModal'
import ModificarConsultaModal from './ModificarConsultaModal'
import useConsultas from './hooks/useConsultas'
import usePaciente from './hooks/usePaciente'
import { Paciente, Consulta, Medico, Consultorio } from './types/types'

interface Props {
  paciente: Paciente;
  medico: Medico;
  consultorio: Consultorio;
  onUpdate: (pacienteActualizado: Paciente) => void;
  onDelete: () => void;
}

const consultaVacia: Consulta = {
  id: '',
  fechaConsulta: new Date().toISOString().split('T')[0],
  agudezaVisualOISC: '',
  agudezaVisualODSC: '',
  agudezaVisualOICC: '',
  agudezaVisualODCC: '',
  lentesParaLejosOI: '',
  lentesParaLejosOD: '',
  lentesParaCercaAO: '',
  observaciones: '',
  pacienteDni: '',
  medicoId: 0,
}


export default function InformacionPaciente({ paciente, medico, consultorio, onUpdate, onDelete }: Props) {
  const [showModal, setShowModal] = useState(false)
  const [modalContent, setModalContent] = useState('')
  const { consultas, fetchConsultas, error, setError } = useConsultas(paciente.dni)
  const { pacienteEditado, setPacienteEditado, actualizarPaciente, eliminarPaciente } = usePaciente(paciente, onUpdate, onDelete)
  const [nuevaConsulta, setNuevaConsulta] = useState<Consulta>(consultaVacia)
  const [consultaEnEdicion, setConsultaEnEdicion] = useState<Consulta | null>(null)
  useEffect(() => {
    fetchConsultas();
  }, [fetchConsultas]);

  const handleEditarPaciente = () => {
    setPacienteEditado({
      ...paciente
    })
    setModalContent('editarPaciente')
    setShowModal(true)
  }

  const handleEliminarPaciente = () => {
    setModalContent('eliminarPaciente')
    setShowModal(true)
  }

  const handleNuevaConsulta = () => {
    setNuevaConsulta({
      ...consultaVacia,
      pacienteDni: paciente.dni,
      medicoId: Number(localStorage.getItem('userId'))
    })
    setModalContent('nuevaConsulta')
    setShowModal(true)
  }

  const verDetalleReceta = (consulta: Consulta) => {
    setModalContent('detalleReceta')
    setNuevaConsulta(consulta)
    setShowModal(true)
  }

  const handleModificarConsulta = (consulta: Consulta) => {
    setConsultaEnEdicion(consulta)
    setModalContent('modificarConsulta')
    setShowModal(true)
  }

  const modificarConsulta = async () => {
    if (!consultaEnEdicion) return;

    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/consulta', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(consultaEnEdicion)
      })

      if (response.ok) {
        await fetchConsultas()
        setShowModal(false)
        setConsultaEnEdicion(null)
      } else {
        throw new Error('Error al modificar la consulta')
      }
    } catch (error) {
      setError('Error al modificar la consulta')
    }
  }

  const handleInputChangeConsulta = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (consultaEnEdicion) {
      setConsultaEnEdicion({
        ...consultaEnEdicion,
        [e.target.name]: e.target.value
      })
    }
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md text-gray-800">
      <div className="flex justify-between items-start">
        <DatosPaciente paciente={paciente} />
        <BotonesAccion
          onNuevaConsulta={handleNuevaConsulta}
          onEditarPaciente={handleEditarPaciente}
          onEliminarPaciente={handleEliminarPaciente}
        />
      </div>

      <HistorialConsultas
        consultas={consultas}
        onVerDetalleReceta={verDetalleReceta}
        onImprimirReceta={(consulta) => imprimirReceta(consulta, paciente, medico, consultorio)}
        onModificarConsulta={handleModificarConsulta}
      />

      {showModal && (
        <Modal onClose={() => setShowModal(false)}>
          {modalContent === 'editarPaciente' && (
            <EditarPacienteModal
              pacienteEditado={pacienteEditado}
              onInputChange={(e) => setPacienteEditado({ ...pacienteEditado, [e.target.name]: e.target.value })}
              onSubmit={actualizarPaciente}
              onClose={() => setShowModal(false)}
            />
          )}
          {modalContent === 'eliminarPaciente' && (
            <EliminarPacienteModal
              onConfirm={eliminarPaciente}
              onClose={() => setShowModal(false)}
            />
          )}
          {modalContent === 'nuevaConsulta' && (
            <NuevaConsultaModal
              nuevaConsulta={nuevaConsulta}
              onInputChange={(e) => setNuevaConsulta({ ...nuevaConsulta, [e.target.name]: e.target.value })}
              onSubmit={async () => {
                await crearConsulta(nuevaConsulta, paciente.dni, setError, fetchConsultas)
                setShowModal(false)
              }}
              onClose={() => setShowModal(false)}
            />
          )}
          {modalContent === 'detalleReceta' && (
            <DetalleRecetaModal
              consulta={nuevaConsulta}
              paciente={paciente}
              medico={medico}
              consultorio={consultorio}
              onClose={() => setShowModal(false)}
            />
          )}
          {modalContent === 'modificarConsulta' && consultaEnEdicion && (
            <ModificarConsultaModal
              consulta={consultaEnEdicion}
              onInputChange={handleInputChangeConsulta}
              onSubmit={modificarConsulta}
              onClose={() => setShowModal(false)}
            />
          )}
        </Modal>
      )}

      {error && (
        <Modal onClose={() => setError('')}>
          <p className="text-black">{error}</p>
        </Modal>
      )}
    </div>
  )
}


function imprimirReceta(consulta: Consulta, paciente: Paciente, medico: Medico, consultorio: Consultorio) {
  const recetaWindow = window.open('', '_blank');
  if (recetaWindow) {
    const logoSrc = consultorio.logo || 'image/Logo.png';

    recetaWindow.document.write(`<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Receta Oftalmológica</title>
    <style>
        @page {
            size: A4;
            margin: 0;
        }
        body {
            font-family: Arial, sans-serif;
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0;
        }
        .container {
            width: 10cm;
            height: 15cm;
            padding: 10px;
            box-sizing: border-box;
        }
        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 5px;
        }
        .eye-icon {
            font-size: 25px;
        }
        .doctor-info {
            text-align: center;
            flex-grow: 1;
            line-height: 0.5;
        }
        .emergency {
            background-color: #f0f0f0;
            padding: 5px;
            margin-bottom: 10px;
            text-align: center;
        }
        .prescription-area {
            height: 350px;
            margin-bottom: 10px;
            font-size: 16px;
            text-align: left;
        }
        hr {
            border: none;
            border-top: 2px solid black;
        }
        .footer {
            text-align: center;
            font-size: 14px;
            line-height: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="eye-icon">
                <img src="${logoSrc}" alt="Logo" style="height: 2cm;">
            </div>
            <div class="doctor-info">
                <h2 style="font-size: 14px;">DR. ${medico.nombre.toUpperCase()} ${medico.apellido.toUpperCase()}</h2>
                <p style="font-size: 12px;">${medico.especialidad.toUpperCase()}</p>
                <p style="font-size: 12px;">M.P. ${medico.numeroMatricula}</p>
            </div>
        </div>
        
        <div class="emergency">
            URGENCIAS: ☎ ${medico.telefono}
        </div>
        <hr />
        
        <div class="prescription-area">
            <p><strong>Fecha:</strong> ${new Date(consulta.fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}</p>
            <p><strong>Paciente:</strong> ${paciente.apellido} ${paciente.nombre}</p>
            ${paciente.ObraSocial ? `
            <p><strong>Obra Social:</strong> ${paciente.ObraSocial}</p>
            ${paciente.numeroObraSocial ? `<p><strong>N°:</strong> ${paciente.numeroObraSocial}</p>` : '<p><strong>N°:</strong>'}
            ` : ''}
            <p>R/p.-</p>
            <p style="font-size: 20px;"><strong>P/Lejos:</p>
		        <p style="margin-left: 80px;"> OD:</strong> ${consulta.lentesParaLejosOD}</p>
            <p style="margin-left: 80px;"><strong>OI:</strong> ${consulta.lentesParaLejosOI}</p>
            ${consulta.lentesParaCercaAO && consulta.lentesParaCercaAO.trim() !== '' ? `
            <p style="font-size: 20px;"><strong>P/Cerca:</p>
            <p style="margin-left: 80px;"> AO:</strong> ${consulta.lentesParaCercaAO}</p>
            ` : ''}
        </div>
        
        <div class="footer">
            <hr />
            <p>${consultorio.domicilio} - ☎ ${consultorio.telefono}</p>
            <p>${consultorio.localidad}</p>
        </div>
    </div>
</body>
</html>
    `);
    recetaWindow.document.close();
    setTimeout(() => {
      recetaWindow.print();
    }, 1000);
  }
}

async function crearConsulta(
  nuevaConsulta: Consulta,
  pacienteDni: string,
  setError: (error: string) => void,
  fetchConsultas: () => void
) {
  try {
    const token = localStorage.getItem('jwtToken')
    const userId = localStorage.getItem('userId')
    const consulta = {
      ...nuevaConsulta,
      pacienteDni,
      medicoId: Number(userId)
    }

    const response = await fetch('http://localhost:8085/api/consulta', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(consulta)
    })

    if (response.ok) {
      setError('Consulta creada con éxito')
      fetchConsultas()
    } else {
      const errorData = await response.json()
      if (errorData.mensaje && errorData.mensaje.includes("Este paciente ya tuvo una consulta el día de hoy")) {
        setError("Este paciente ya tuvo una consulta el día de hoy")
      } else {
        throw new Error('Error al crear consulta')
      }
    }
  } catch (error) {
    if (error instanceof Error) {
      setError(error.message)
    } else {
      setError('Ocurrió un error. Por favor, intente nuevamente.')
    }
  }
}
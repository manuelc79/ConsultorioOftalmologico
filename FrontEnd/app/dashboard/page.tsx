'use client'

import React, { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Modal from '../components/Modal'
import InformacionMedico from '../components/InformacionMedico'
import BusquedaPaciente from '../components/BusquedaPaciente'
import InformacionPaciente from '../components/InformacionPaciente'
import ListadoPacientes from '../components/ListadoPacientes'

import { CogIcon } from '@heroicons/react/24/solid'
import { Paciente, Medico } from '../components/types/types'
import { Consultorio } from '../components/types/types'

export default function PaginaPanelControl() {
  const [infoMedico, setInfoMedico] = useState<Medico | null>(null)
  const [infoPaciente, setInfoPaciente] = useState<Paciente | null>(null)
  const [showModal, setShowModal] = useState(false)
  const [modalContent, setModalContent] = useState('')
  const [showMedicoInfo, setShowMedicoInfo] = useState(false)
  const router = useRouter()

  const [listadoPacientes, setListadoPacientes] = useState<Paciente[]>([])
  const [mostrarListado, setMostrarListado] = useState(false)
  const [listadoTitulo, setListadoTitulo] = useState('Pacientes del Consultorio')
  const [consultorio, setConsultorio] = useState<Consultorio | null>(null)

  const handleMostrarListadoPacientes = (pacientes: Paciente[], titulo?: string) => {
    setListadoPacientes(pacientes)
    setMostrarListado(true)
    setInfoPaciente(null)
    setListadoTitulo(titulo || 'Pacientes del Consultorio')
  }

  useEffect(() => {
    obtenerInfoMedico()
    const fetchConsultorio = async () => {
      try {
        const token = localStorage.getItem('jwtToken')
        const userId = localStorage.getItem('userId')
        const response = await fetch(`http://localhost:8085/api/consultorio/find`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ medicoId: userId })
        })

        if (response.ok) {
          const data = await response.json()
          setConsultorio(data)
        } else {
          console.error('Error al cargar los datos del consultorio')
        }
      } catch (error) {
        console.error('Error:', error)
      }
    }

    fetchConsultorio()
  }, [])

  const obtenerInfoMedico = async () => {
    const token = localStorage.getItem('jwtToken')
    const id = localStorage.getItem('userId')

    if (!token || !id) {
      router.push('/')
      return
    }

    try {
      const response = await fetch('http://localhost:8085/api/medico/find', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ id })
      })

      if (response.ok) {
        const data = await response.json()
        setInfoMedico(data)
      } else if (response.status === 401) {
        setModalContent('Sesión expirada. Por favor, inicie sesión nuevamente.')
        setShowModal(true)
        setTimeout(() => router.push('/'), 2000)
      } else {
        throw new Error('Error al obtener información del médico')
      }
    } catch (error) {
      setModalContent('Su sesión a expirado. Por favor vuelva a iniciar sesión.')
      setShowModal(true)
      router.push('/')
    }
  }

  const handleBusquedaPaciente = (paciente: Paciente | undefined) => {
    if (paciente) { 
      setInfoPaciente(paciente)
      setMostrarListado(false)
    } else {
      setInfoPaciente(null)
    }
  }

  const handleVerDetallePaciente = async (dni: string) => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/paciente/find', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ dni })
      })

      if (response.ok) {
        const paciente = await response.json()
        setInfoPaciente(paciente)
        setMostrarListado(false)
      } else {
        throw new Error('Error al buscar paciente')
      }
    } catch (error) {
      console.error('Error al obtener detalles del paciente:', error)
    }
  }

  const handleUpdateDashboard = () => {
    obtenerInfoMedico();
    obtenerConsultorio();
  }

  const obtenerConsultorio = async () => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch('http://localhost:8085/api/consultorio/find', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ medicoId: infoMedico?.id })
      });

      if (response.ok) {
        const data = await response.json();
        setConsultorio(data);
      } else {
        console.error('Error al obtener los datos del consultorio');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">CONSULTORIO OFTALMOLÓGICO</h1>
        <div className="flex items-center">
          {infoMedico && (
            <p className="mr-4 text-lg font-semibold text-gray-700">
              Dr. {infoMedico.nombre} {infoMedico.apellido}
            </p>
          )}
          <button
            onClick={() => setShowMedicoInfo(true)}
            className="text-gray-600 hover:text-gray-800 transition-colors duration-200"
            title="Ver información del médico"
          >
            <CogIcon className="h-6 w-6" />
          </button>
        </div>
      </div>
      <div className="grid grid-cols-1 gap-8">
        <div>
          <BusquedaPaciente onPacienteEncontrado={handleBusquedaPaciente} onMostrarListadoPacientes={handleMostrarListadoPacientes} />
        </div>
        {infoPaciente && infoMedico && consultorio && (
          <div>
            <InformacionPaciente 
              paciente={infoPaciente} 
              medico={infoMedico}
              consultorio={consultorio}
              onUpdate={(pacienteActualizado: Paciente) => {
                setInfoPaciente(pacienteActualizado)
              }}
              onDelete={() => {
                setInfoPaciente(null)
                setMostrarListado(false)
              }}
            />
          </div>
        )}
        
      </div>
      {showModal && (
        <Modal onClose={() => setShowModal(false)}>
          <p className="text-gray-800">{modalContent}</p>
        </Modal>
      )}
      {showMedicoInfo && infoMedico && (
        <InformacionMedico 
          medico={infoMedico} 
          consultorio={consultorio ?? {} as Consultorio}
          onUpdate={obtenerInfoMedico} 
          onClose={() => setShowMedicoInfo(false)}
          onUpdateDashboard={handleUpdateDashboard}
        />
      )}
      {mostrarListado && (
        <div>
          <ListadoPacientes 
            pacientes={listadoPacientes} 
            onVerDetalle={handleVerDetallePaciente}
            titulo={listadoTitulo}
          />
        </div>
      )}
    </div>
  )
}
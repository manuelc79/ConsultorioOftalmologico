'use client'

import { useState } from 'react'
import { MagnifyingGlassIcon, UserIcon, XMarkIcon, ChevronDownIcon } from '@heroicons/react/24/solid'
import { Menu, Transition } from '@headlessui/react'
import Modal from './Modal'
import { Paciente } from './types/types'
import { useRouter } from 'next/navigation'

interface Props {
  onPacienteEncontrado: (paciente: Paciente | undefined) => void;
  onMostrarListadoPacientes: (pacientes: Paciente[], titulo?: string, fecha?: string) => void;
}

const pacienteVacio: Paciente = {
  apellido: '',
  nombre: '',
  telefono: '',
  dni: '',
  ObraSocial: '',
  numeroObraSocial: ''
}

export default function BusquedaPaciente({ onPacienteEncontrado, onMostrarListadoPacientes }: Props) {
  const [dni, setDni] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [modalContent, setModalContent] = useState('')
  const [nuevoPaciente, setNuevoPaciente] = useState<Paciente>(pacienteVacio)
  const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0])
  const [showDateModal, setShowDateModal] = useState(false)
  const router = useRouter();
  const [numeroObraSocialHabilitado, setNumeroObraSocialHabilitado] = useState(false);

  const buscarPaciente = async () => {
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
        onPacienteEncontrado(paciente)
      } else if (response.status === 404) {
        setModalContent('Paciente no encontrado.')
        setShowModal(true)
      } else {
        throw new Error('Error al buscar paciente')
      }
    } catch (error) {
      setModalContent('Su sesión ha expirado. Por favor vuelva a iniciar sesión.')
      setShowModal(true)
      router.push('/')
    } finally {
      setDni('') // Limpia el campo de DNI después de la búsqueda
    }
  }

  const handleNuevoPaciente = () => {
    setNuevoPaciente({ ...pacienteVacio, dni })
    setModalContent('nuevoPacienteForm')
    setShowModal(true)
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setNuevoPaciente({ ...nuevoPaciente, [name]: value });
    
    if (name === 'ObraSocial') {
      setNumeroObraSocialHabilitado(value.trim() !== '');
    }
  }

  const crearNuevoPaciente = async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/paciente', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(nuevoPaciente)
      })

      if (response.ok) {
        const paciente = await response.json()
        onPacienteEncontrado(paciente)
        setShowModal(false)
        setNuevoPaciente(pacienteVacio)
      } else if (response.status === 500) {
        setModalContent('El DNI ya está en uso. Por favor, intente con uno diferente.')
      } else {
        throw new Error('Error al crear nuevo paciente')
      }
    } catch (error) {
      setModalContent('Ocurrió un error. Por favor, intente nuevamente.')
    }
  }

  const handleListadoPacientes = async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/paciente', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (response.ok) {
        const pacientes = await response.json()
        onMostrarListadoPacientes(pacientes)
      } else {
        throw new Error('Error al obtener el listado de pacientes')
      }
    } catch (error) {
      console.error('Ocurrió un error al obtener el listado de pacientes:', error)
    }
  }

  const handleConsultasPorFecha = () => {
    setShowDateModal(true)
  }

  const buscarConsultasPorFecha = async (fechaSeleccionada: string) => {
    try {
      const token = localStorage.getItem('jwtToken')
      const medicoId = localStorage.getItem('userId')
      const response = await fetch('http://localhost:8085/api/consulta/find/fecha', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ fechaConsulta: fechaSeleccionada, medicoId: Number(medicoId) })
      })

      if (response.ok) {
        const pacientes = await response.json()
        onMostrarListadoPacientes(pacientes, `Pacientes Atendidos el ${new Date(fechaSeleccionada + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}`)
      } else if (response.status === 404) {
        const errorData = await response.json()
        setModalContent(errorData.mensaje || 'No se encontraron consultas para la fecha seleccionada')
        setShowModal(true)
      } else {
        throw new Error('Error al obtener el listado de pacientes')
      }
    } catch (error) {
      console.error('Ocurrió un error al obtener el listado de pacientes:', error)
      setModalContent('Ocurrió un error al obtener el listado de pacientes. Por favor, intente nuevamente.')
      setShowModal(true)
    } finally {
      setShowDateModal(false)
    }
  }

  return (
    <div className="mb-8">
      <div className="flex items-center mb-4 space-x-4">
        <div className="flex-grow flex items-center">
          <input
            type="text"
            value={dni}
            onChange={(e) => setDni(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
                buscarPaciente();
              }
            }}
            placeholder="Ingrese DNI del paciente"
            className="flex-grow px-3 py-2 border border-gray-300 rounded-md text-gray-800 mr-2"
          />
          <button
            onClick={buscarPaciente}
            className="bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600"
          >
            <MagnifyingGlassIcon className="h-6 w-6" />
          </button>
        </div>
        <button
          onClick={handleNuevoPaciente}
          className="bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600 flex items-center"
        >
          <UserIcon className="h-5 w-5 mr-2" />
          Nuevo Paciente
        </button>
        <Menu as="div" className="relative inline-block text-left">
          <div>
            <Menu.Button className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 flex items-center">
              Opciones
              <ChevronDownIcon className="-mr-1 ml-2 h-5 w-5" aria-hidden="true" />
            </Menu.Button>
          </div>
          <Transition
            enter="transition ease-out duration-100"
            enterFrom="transform opacity-0 scale-95"
            enterTo="transform opacity-100 scale-100"
            leave="transition ease-in duration-75"
            leaveFrom="transform opacity-100 scale-95"
            leaveTo="transform opacity-0 scale-95"
          >
            <Menu.Items className="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none">
              <div className="py-1">
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={handleListadoPacientes}
                      className={`${
                        active ? 'bg-blue-100 text-blue-900' : 'text-gray-700'
                      } block px-4 py-2 text-sm w-full text-left`}
                    >
                      Pacientes del Consultorio
                    </button>
                  )}
                </Menu.Item>
                <Menu.Item>
                  {({ active }) => (
                    <button
                      onClick={handleConsultasPorFecha}
                      className={`${
                        active ? 'bg-blue-100 text-blue-900' : 'text-gray-700'
                      } block w-full text-left px-4 py-2 text-sm`}
                    >
                      Consultas de la Fecha
                    </button>
                  )}
                </Menu.Item>
              </div>
            </Menu.Items>
          </Transition>
        </Menu>
      </div>      
      {showModal && (
        <Modal onClose={() => {
          setShowModal(false)
          setNuevoPaciente(pacienteVacio)
        }}>
          <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-2xl w-full">
            <button onClick={() => {
              setShowModal(false)
              setNuevoPaciente(pacienteVacio)
            }} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
              <XMarkIcon className="h-6 w-6" />
            </button>
            {modalContent === 'nuevoPacienteForm' ? (
              <div>
                <h2 className="text-2xl font-bold mb-4 text-gray-800">Nuevo Paciente</h2>
                <form onSubmit={(e) => { e.preventDefault(); crearNuevoPaciente(); }} className="grid grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="apellido" className="block mb-2 text-sm font-medium text-gray-700">
                      Apellido
                    </label>
                    <input
                      type="text"
                      id="apellido"
                      name="apellido"
                      value={nuevoPaciente.apellido}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="nombre" className="block mb-2 text-sm font-medium text-gray-700">
                      Nombre
                    </label>
                    <input
                      type="text"
                      id="nombre"
                      name="nombre"
                      value={nuevoPaciente.nombre}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="telefono" className="block mb-2 text-sm font-medium text-gray-700">
                      Teléfono
                    </label>
                    <input
                      type="text"
                      id="telefono"
                      name="telefono"
                      value={nuevoPaciente.telefono}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                    />
                  </div>
                  <div>
                    <label htmlFor="dni" className="block mb-2 text-sm font-medium text-gray-700">
                      DNI
                    </label>
                    <input
                      type="text"
                      id="dni"
                      name="dni"
                      value={nuevoPaciente.dni}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="ObraSocial" className="block mb-2 text-sm font-medium text-gray-700">
                      Obra Social
                    </label>
                    <input
                      type="text"
                      id="ObraSocial"
                      name="ObraSocial"
                      value={nuevoPaciente.ObraSocial}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                    />
                  </div>
                  <div>
                    <label htmlFor="numeroObraSocial" className="block mb-2 text-sm font-medium text-gray-700">
                      Número Obra Social
                    </label>
                    <input
                      type="text"
                      id="numeroObraSocial"
                      name="numeroObraSocial"
                      value={nuevoPaciente.numeroObraSocial}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                      disabled={!numeroObraSocialHabilitado}
                    />
                  </div>
                  <div className="col-span-2 flex justify-end space-x-2 mt-4">
                    <button
                      type="button"
                      onClick={() => {
                        setShowModal(false)
                        setNuevoPaciente(pacienteVacio)
                      }}
                      className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
                    >
                      Crear Paciente
                    </button>
                  </div>
                </form>
              </div>
            ) : (
              <div>
                <p className="text-gray-800 mb-4">{modalContent}</p>
                <div className="flex justify-end">
                  <button
                    onClick={() => {
                      setShowModal(false)
                      setNuevoPaciente(pacienteVacio)
                    }}
                    className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
                  >
                    Cerrar
                  </button>
                </div>
              </div>
            )}
          </div>
        </Modal>
      )}
      {showDateModal && (
        <Modal onClose={() => setShowDateModal(false)}>
          <div className="p-6 bg-white rounded-lg shadow-xl max-w-md w-full">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Seleccionar Fecha</h2>
            <input
              type="date"
              value={fecha}
              onChange={(e) => setFecha(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800 mb-4"
            />
            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setShowDateModal(false)}
                className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
              >
                Cerrar
              </button>
              <button
                onClick={() => buscarConsultasPorFecha(fecha)}
                className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
              >
                Buscar
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  )
}
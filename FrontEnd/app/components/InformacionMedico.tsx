'use client'

import { useState } from 'react'
import Modal from './Modal'
import { XMarkIcon } from '@heroicons/react/24/solid'
import { Medico } from './types/types'
import { Consultorio } from './types/types'

interface Props {
  medico: Medico;
  consultorio: Consultorio;
  onUpdate: () => void;
  onClose: () => void;
}

export default function InformacionMedico({ medico, onUpdate, onClose }: Props) {
  const [showModal, setShowModal] = useState(false)
  const [showConsultorioModal, setShowConsultorioModal] = useState(false)
  const [medicoEditado, setMedicoEditado] = useState({ ...medico, password: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [consultorio, setConsultorio] = useState<Consultorio | null>(null)
//  const [isEditing, setIsEditing] = useState(false)
  const [isEditingConsultorio, setIsEditingConsultorio] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMedicoEditado({ ...medicoEditado, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (medicoEditado.password !== medicoEditado.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/medico', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(medicoEditado)
      })

      if (response.ok) {
        onUpdate()
        setShowModal(false)
        setError('Información del médico actualizada con éxito')
      } else {
        throw new Error('Error al actualizar la información del médico')
      }
    } catch (error) {
      setError('Ocurrió un error. Por favor, intente nuevamente.')
    }
  }

  const handleConsultorioClick = async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch('http://localhost:8085/api/consultorio/find', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ medicoId: medico.id })
      })

      if (response.ok) {
        const data = await response.json()
        setConsultorio(data)
        setIsEditingConsultorio(false)
      } else if (response.status === 404) {
        setConsultorio(null)
        setIsEditingConsultorio(true)
      } else {
        throw new Error('Error al buscar el consultorio')
      }
      setShowConsultorioModal(true)
    } catch (error) {
      setError('Ocurrió un error al buscar el consultorio. Por favor, intente nuevamente.')
    }
  }

  const handleConsultorioSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!isEditingConsultorio && consultorio) {
      setIsEditingConsultorio(true)
      return
    }
    try {
      const token = localStorage.getItem('jwtToken')
      const url = consultorio?.id ? 'http://localhost:8085/api/consultorio' : 'http://localhost:8085/api/consultorio'
      const method = consultorio?.id ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ ...consultorio, medicoId: medico.id })
      })

      if (response.ok) {
        setError(consultorio?.id ? 'Consultorio modificado con éxito' : 'Consultorio guardado con éxito')
        setShowConsultorioModal(false)
        setIsEditingConsultorio(false)
      } else {
        throw new Error(consultorio?.id ? 'Error al modificar el consultorio' : 'Error al guardar el consultorio')
      }
    } catch (error) {
      setError('Ocurrió un error. Por favor, intente nuevamente.')
    }
  }

//  const handleModificarConsultorio = async () => {
//    if (isEditing) {
//      try {
//        const token = localStorage.getItem('jwtToken')
//        const response = await fetch('http://localhost:8085/api/consultorio', { 
//          method: 'PUT',
//          headers: {
//            'Content-Type': 'application/json',
//            'Authorization': `Bearer ${token}`
//          },
//          body: JSON.stringify({ ...consultorio, medicoId: medico.id })
//        })

//        if (response.ok) {
//          setError('Consultorio modificado exitosamente')
//          setShowConsultorioModal(false)
//          setIsEditing(false)
//        } else {
//          throw new Error('Error al modificar el consultorio')
//          }
//      } catch (error) {
//        setError('Hubo un error al modificar el consultorio')
//      }
//    }
//  }

  return (
    <Modal onClose={onClose}>
      <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-2xl w-full">
        <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
          <XMarkIcon className="h-6 w-6" />
        </button>
        <h2 className="text-2xl font-bold mb-4 text-gray-800">Información del Médico</h2>
        <div className="grid grid-cols-2 gap-4 mb-4">
          <p><strong>Nombre:</strong> {medico.nombre} {medico.apellido}</p>
          <p><strong>Correo Electrónico:</strong> {medico.email}</p>
          <p><strong>Especialidad:</strong> {medico.especialidad}</p>
          <p><strong>Número de Matrícula:</strong> {medico.numeroMatricula}</p>
          <p><strong>Teléfono:</strong> {medico.telefono}</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 mr-2"
        >
          Modificar Datos
        </button>
        <button
          onClick={handleConsultorioClick}
          className="bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600"
        >
          Datos Consultorio
        </button>
      </div>
      {showModal && (
        <Modal onClose={() => setShowModal(false)}>
          <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-2xl w-full">
            <button onClick={() => setShowModal(false)} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
              <XMarkIcon className="h-6 w-6" />
            </button>
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Editar Información del Médico</h2>
            <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="nombre" className="block mb-2 text-sm font-medium text-gray-700">
                  Nombre
                </label>
                <input
                  type="text"
                  id="nombre"
                  name="nombre"
                  value={medicoEditado.nombre}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                />
              </div>
              <div>
                <label htmlFor="apellido" className="block mb-2 text-sm font-medium text-gray-700">
                  Apellido
                </label>
                <input
                  type="text"
                  id="apellido"
                  name="apellido"
                  value={medicoEditado.apellido}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                />
              </div>
              <div>
                <label htmlFor="especialidad" className="block mb-2 text-sm font-medium text-gray-700">
                  Especialidad
                </label>
                <input
                  type="text"
                  id="especialidad"
                  name="especialidad"
                  value={medicoEditado.especialidad}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                />
              </div>
              <div>
                <label htmlFor="numeroMatricula" className="block mb-2 text-sm font-medium text-gray-700">
                  Número de Matrícula
                </label>
                <input
                  type="number"
                  id="numeroMatricula"
                  name="numeroMatricula"
                  value={medicoEditado.numeroMatricula}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                />
              </div>
              <div>
                <label htmlFor="telefono" className="block mb-2 text-sm font-medium text-gray-700">
                  Teléfono
                </label>
                <input
                  type="tel"
                  id="telefono"
                  name="telefono"
                  value={medicoEditado.telefono}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                />
              </div>
              <div>
                <label htmlFor="password" className="block mb-2 text-sm font-medium text-gray-700">
                  Contraseña
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={medicoEditado.password}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                />
              </div>
              <div>
                <label htmlFor="confirmPassword" className="block mb-2 text-sm font-medium text-gray-700">
                  Confirmar Contraseña
                </label>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={medicoEditado.confirmPassword}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                />
              </div>
              <div className="col-span-2 flex justify-end space-x-2 mt-4">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
                >
                  Guardar Cambios
                </button>
              </div>
            </form>
          </div>
        </Modal>
      )}
      {showConsultorioModal && (
        <Modal onClose={() => setShowConsultorioModal(false)}>
          <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-2xl w-full">
            <button onClick={() => setShowConsultorioModal(false)} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
              <XMarkIcon className="h-6 w-6" />
            </button>
            <h2 className="text-2xl font-bold mb-4 text-gray-800">
              {consultorio ? 'Datos del Consultorio' : 'Nuevo Consultorio'}
            </h2>
            <form onSubmit={handleConsultorioSubmit} className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="domicilio" className="block mb-2 text-sm font-medium text-gray-700">
                  Domicilio
                </label>
                <input
                  type="text"
                  id="domicilio"
                  name="domicilio"
                  value={consultorio?.domicilio || ''}
                  onChange={(e) => setConsultorio({ ...consultorio, domicilio: e.target.value } as Consultorio)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                  disabled={!isEditingConsultorio}
                />
              </div>
              <div>
                <label htmlFor="telefono" className="block mb-2 text-sm font-medium text-gray-700">
                  Teléfono
                </label>
                <input
                  type="tel"
                  id="telefono"
                  name="telefono"
                  value={consultorio?.telefono || ''}
                  onChange={(e) => setConsultorio({ ...consultorio, telefono: e.target.value } as Consultorio)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                  disabled={!isEditingConsultorio}
                />
              </div>
              <div>
                <label htmlFor="localidad" className="block mb-2 text-sm font-medium text-gray-700">
                  Localidad
                </label>
                <input
                  type="text"
                  id="localidad"
                  name="localidad"
                  value={consultorio?.localidad || ''}
                  onChange={(e) => setConsultorio({ ...consultorio, localidad: e.target.value } as Consultorio)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  required
                  disabled={!isEditingConsultorio}
                />
              </div>
              <div>
                <label htmlFor="logo" className="block mb-2 text-sm font-medium text-gray-700">
                  Logo
                </label>
                <input
                  type="text"
                  id="logo"
                  name="logo"
                  value={consultorio?.logo || ''}
                  onChange={(e) => setConsultorio({ ...consultorio, logo: e.target.value } as Consultorio)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
                  disabled={!isEditingConsultorio}
                />
              </div>
              <div className="col-span-2 flex justify-end space-x-2 mt-4">
                <button
                  type="button"
                  onClick={() => setShowConsultorioModal(false)}
                  className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
                >
                  Cancelar
                </button>
                {consultorio && !isEditingConsultorio ? (
                  <button
                    type="submit"
                    className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
                  >
                    Modificar
                  </button>
                ) : (
                  <button
                    type="submit"
                    className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
                  >
                    {consultorio ? 'Guardar Cambios' : 'Guardar Datos'}
                  </button>
                )}
              </div>
            </form>
          </div>
        </Modal>
      )}
      {error && (
        <Modal onClose={() => setError('')}>
          <p className="text-gray-800">{error}</p>
        </Modal>
      )}
    </Modal>
  )
}
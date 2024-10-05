'use client'

import { useState } from 'react'
import Modal from './Modal'
import { XMarkIcon } from '@heroicons/react/24/solid'
import { Medico } from './types/types'

interface Props {
  medico: Medico;
  onUpdate: () => void;
  onClose: () => void;
}

export default function InformacionMedico({ medico, onUpdate, onClose }: Props) {
  const [showModal, setShowModal] = useState(false)
  const [medicoEditado, setMedicoEditado] = useState({ ...medico, password: '', confirmPassword: '' })
  const [error, setError] = useState('')

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
          className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
        >
          Modificar Datos
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
      {error && (
        <Modal onClose={() => setError('')}>
          <p className="text-gray-800">{error}</p>
        </Modal>
      )}
    </Modal>
  )
}
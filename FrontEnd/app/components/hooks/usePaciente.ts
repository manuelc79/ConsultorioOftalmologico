import { useState } from 'react'
import { Paciente } from '../types/types'

export default function usePaciente(
  paciente: Paciente,
  onUpdate: (pacienteActualizado: Paciente) => void,
  onDelete: () => void
) {
  const [pacienteEditado, setPacienteEditado] = useState(paciente)
  const [mensaje, setMensaje] = useState('')

  const actualizarPaciente = async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const pacienteActualizado = {
        ...pacienteEditado
      }

      const response = await fetch('http://localhost:8085/api/paciente', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(pacienteActualizado)
      })

      if (response.ok) {
        onUpdate(pacienteActualizado)
        setMensaje('Paciente modificado exitosamente')
        return true
      } else {
        throw new Error('Error al actualizar paciente')
      }
    } catch (error) {
      console.error('Ocurrió un error. Por favor, intente nuevamente.')
      setMensaje('Ocurrió un error. Por favor, intente nuevamente.')
      return false
    }
  }

  const eliminarPaciente = async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch(`http://localhost:8085/api/paciente/delete`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ dni: paciente.dni })
      })

      if (response.ok) {
        onDelete()
      } else {
        throw new Error('Error al eliminar paciente')
      }
    } catch (error) {
      console.error('Ocurrió un error. Por favor, intente nuevamente.')
      setMensaje('Error al eliminar el paciente')
    }
  }

  return { pacienteEditado, setPacienteEditado, actualizarPaciente, eliminarPaciente, mensaje, setMensaje }
}
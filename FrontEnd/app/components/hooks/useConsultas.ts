import { useState, useEffect, useCallback } from 'react'
import { Consulta } from '../types/types'
import endpoints from '../api'

export default function useConsultas(pacienteDni: string) {
  const [consultas, setConsultas] = useState<Consulta[]>([])
  const [error, setError] = useState('')

  const fetchConsultas = useCallback(async () => {
    try {
      const token = localStorage.getItem('jwtToken')
      const response = await fetch(endpoints.consultaFindPaciente, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ dni: pacienteDni })
      })
      setConsultas([])
      if (response.ok) {
        const data = await response.json()
        setConsultas(data)
      }
    } catch (error) {
      setError('Ocurrió un error al obtener las consultas. Por favor, intente nuevamente.')
    }
  },[pacienteDni])

  useEffect(() => {
    if (pacienteDni) {
      fetchConsultas()
    }
  }, [pacienteDni, fetchConsultas])

  return { consultas, fetchConsultas, error, setError }
}
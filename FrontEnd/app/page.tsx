'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import Modal from './components/Modal'

export default function PaginaInicioSesion() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [showModal, setShowModal] = useState(false)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    try {
      const response = await fetch('http://localhost:8085/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      })

      if (response.ok) {
        const data = await response.json()
        localStorage.setItem('jwtToken', data.jwtToken)
        localStorage.setItem('userId', data.id)
        router.push('/dashboard')
      } else {
        setError('Credenciales inválidas')
        setShowModal(true)
      }
    } catch (error) {
      setError('Ocurrió un error. Por favor, intente nuevamente.')
      setShowModal(true)
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h1 className="text-2xl font-bold mb-4 text-center text-black">Iniciar Sesión</h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="email" className="block mb-2 text-sm font-medium text-gray-700">
              Correo Electrónico
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
              required
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="block mb-2 text-sm font-medium text-gray-700">
              Contraseña
            </label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
              required
            />
          </div>
          <button
            type="submit"
            className="w-full bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
          >
            Iniciar Sesión
          </button>
        </form>
      </div>
      {showModal && (
        <Modal onClose={() => setShowModal(false)}>
          <p>{error}</p>
        </Modal>
      )}
    </div>
  )
}
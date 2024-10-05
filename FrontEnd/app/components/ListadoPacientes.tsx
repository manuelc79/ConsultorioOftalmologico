import React, { useState } from 'react'
import { Paciente } from './types/types'
import { ChevronLeftIcon, ChevronRightIcon, EyeIcon } from '@heroicons/react/24/solid'

interface Props {
  pacientes: Paciente[]
  onVerDetalle: (dni: string) => void
  titulo: string
}

export default function ListadoPacientes({ pacientes, onVerDetalle, titulo}: Props) {
  const [currentPage, setCurrentPage] = useState(1)
  const patientsPerPage = 10
  const indexOfLastPatient = currentPage * patientsPerPage
  const indexOfFirstPatient = indexOfLastPatient - patientsPerPage
  const currentPatients = pacientes.slice(indexOfFirstPatient, indexOfLastPatient)
  const totalPages = Math.ceil(pacientes.length / patientsPerPage)

  const nextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1)
    }
  }

  const prevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1)
    }
  }

  return (
    <div className="mt-8">
      <h3 className="text-xl font-bold mb-4 text-gray-800">{titulo}</h3>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-gray-100">
              <th className="px-4 py-2 border-b text-gray-800 text-left">Apellido</th>
              <th className="px-4 py-2 border-b text-gray-800 text-left">Nombre</th>
              <th className="px-4 py-2 border-b text-gray-800 text-left">DNI</th>
              <th className="px-4 py-2 border-b text-gray-800 text-left">Teléfono</th>
              <th className="px-4 py-2 border-b text-gray-800 text-left">Obra Social</th>
              <th className="px-4 py-2 border-b text-gray-800 text-left">Ver Detalles</th>
            </tr>
          </thead>
          <tbody>
            {currentPatients.map((paciente) => (
              <tr key={paciente.dni} className="hover:bg-gray-200">
                <td className="px-4 py-2 border-b text-gray-800">{paciente.apellido}</td>
                <td className="px-4 py-2 border-b text-gray-800">{paciente.nombre}</td>
                <td className="px-4 py-2 border-b text-gray-800">{paciente.dni}</td>
                <td className="px-4 py-2 border-b text-gray-800">{paciente.telefono}</td>
                <td className="px-4 py-2 border-b text-gray-800">{paciente.ObraSocial}</td>
                <td className="px-4 py-2 border-b text-gray-800">
                  <button
                    onClick={() => onVerDetalle(paciente.dni)}
                    className="text-blue-600 hover:text-blue-800"
                    title="Ver detalles"
                  >
                    <EyeIcon className="h-5 w-5" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="mt-4 flex justify-between items-center">
        <button
          onClick={prevPage}
          disabled={currentPage === 1}
          className={`flex items-center px-4 py-2 border rounded-md ${
            currentPage === 1 ? 'bg-gray-200 text-gray-400' : 'bg-white text-gray-700 hover:bg-gray-50'
          }`}
        >
          <ChevronLeftIcon className="h-5 w-5 mr-2" />
          Anterior
        </button>
        <span className="text-gray-700">
          Página {currentPage} de {totalPages}
        </span>
        <button
          onClick={nextPage}
          disabled={currentPage === totalPages}
          className={`flex items-center px-4 py-2 border rounded-md ${
            currentPage === totalPages ? 'bg-gray-200 text-gray-400' : 'bg-white text-gray-700 hover:bg-gray-50'
          }`}
        >
          Siguiente
          <ChevronRightIcon className="h-5 w-5 ml-2" />
        </button>
      </div>
    </div>
  )
}
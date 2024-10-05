import React from "react"
import { Paciente, Consulta } from "./types/types"

interface Props {
  consultas: Consulta[];
  paciente: Paciente;
}

const ListaConsultas: React.FC<Props> = ({ consultas, paciente }) => {
  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Historia Clínica</h2>
      <p><strong>Paciente:</strong> {paciente.nombre} {paciente.apellido}</p>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr className="bg-gray-100">
              <th className="px-4 py-2 border-b" title="Fecha de la consulta">FECHA</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Izquierdo S/C">AG. V. OI S/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Derecho S/C">AG. V. OD S/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Izquierdo C/C">AG. V. OI C/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Derecho C/C">AG. V. OD C/C</th>
              <th className="px-4 py-2 border-b" title="Lentes para Lejos Ojo Izquierdo">LENTES L. OI</th>
              <th className="px-4 py-2 border-b" title="Lentes para Lejos Ojo Derecho">LENTES L. OD</th>
              <th className="px-4 py-2 border-b" title="Lentes para Cerca Ojo Izquierdo">LENTES C. OI</th>
              <th className="px-4 py-2 border-b" title="Lentes para Cerca Ojo Derecho">LENTES C. OD</th>
              <th className="px-4 py-2 border-b">OBSERVACIONES</th>
            </tr>
          </thead>
          <tbody>
            {consultas.map((consulta, index) => (
              <tr key={consulta.id} className={`hover:bg-gray-100 ${index === 0 ? 'bg-yellow-100' : ''}`}>
                <td className="px-4 py-2 border-b">{new Date(consulta.fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualOISC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualODSC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualOICC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualODCC}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaLejosOI}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaLejosOD}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaCercaAO}</td>
                <td className="px-4 py-2 border-b">{consulta.observaciones}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default ListaConsultas
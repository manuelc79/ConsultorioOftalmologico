// components/HistorialConsultas.tsx
import { Consulta } from './types/types'
import { EyeIcon, PrinterIcon, PencilIcon } from '@heroicons/react/24/solid'

interface Props {
  consultas: Consulta[];
  onVerDetalleReceta: (consulta: Consulta) => void;
  onImprimirReceta: (consulta: Consulta) => void;
  onModificarConsulta: (consulta: Consulta) => void;
}

export default function HistorialConsultas({ consultas, onVerDetalleReceta, onImprimirReceta, onModificarConsulta }: Props) {
  if (consultas.length === 0) return null

  return (
    <div className="mt-8">
      <h3 className="text-xl font-bold mb-4">Historia Clínica</h3>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300">
          <thead>
            <tr>
              <th className="px-4 py-2 border-b" title="Fecha de la consulta">FECHA</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Derecho S/C">AG. V. OD S/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Izquierdo S/C">AG. V. OI S/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Derecho C/C">AG. V. OD C/C</th>
              <th className="px-4 py-2 border-b" title="Agudeza Visual Ojo Izquierdo C/C">AG. V. OI C/C</th>
              <th className="px-4 py-2 border-b" title="Lentes para Lejos Ojo Derecho">LENTES L. OD</th>
              <th className="px-4 py-2 border-b" title="Lentes para Lejos Ojo Izquierdo">LENTES L. OI</th>
              <th className="px-4 py-2 border-b" title="Lentes para Cerca Ambos Ojos">LENTES C. AO</th>
              <th className="px-4 py-2 border-b">ACCIONES</th>
            </tr>
          </thead>
          <tbody>
            {consultas.map((consulta, index) => (
              <tr key={consulta.id} className="hover:bg-gray-100">
                <td className="px-4 py-2 border-b">{new Date(consulta.fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualODSC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualOISC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualODCC}</td>
                <td className="px-4 py-2 border-b">{consulta.agudezaVisualOICC}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaLejosOD}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaLejosOI}</td>
                <td className="px-4 py-2 border-b">{consulta.lentesParaCercaAO}</td>
                <td className="px-4 py-2 border-b">
                  <button
                    onClick={() => onVerDetalleReceta(consulta)}
                    className="text-blue-600 hover:text-blue-800 mr-2"
                    title="Ver detalle de la receta"
                  >
                    <EyeIcon className="h-5 w-5 inline" />
                  </button>
                  <button
                    onClick={() => onImprimirReceta(consulta)}
                    className="text-green-600 hover:text-green-800 mr-2"
                    title="Imprimir receta"
                  >
                    <PrinterIcon className="h-5 w-5 inline" />
                  </button>
                  {index === 0 && (
                    <button
                      onClick={() => onModificarConsulta(consulta)}
                      className="text-yellow-600 hover:text-yellow-800"
                      title="Modificar consulta"
                    >
                      <PencilIcon className="h-5 w-5 inline" />
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
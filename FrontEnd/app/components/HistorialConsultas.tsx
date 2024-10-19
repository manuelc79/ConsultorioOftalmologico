// components/HistorialConsultas.tsx
import { Consulta } from './types/types'
import { EyeIcon, PrinterIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/solid'
import { useState, useEffect } from 'react'
import EliminarConsultaModal from './EliminarConsultaModal'

interface Props {
  readonly consultas: Consulta[]; // Marcar como readonly
  readonly onVerDetalleReceta: (consulta: Consulta) => void; // Marcar como readonly
  readonly onImprimirReceta: (consulta: Consulta) => void; // Marcar como readonly
  readonly onModificarConsulta: (consulta: Consulta) => void; // Marcar como readonly
}

export default function HistorialConsultas({ consultas, onVerDetalleReceta, onImprimirReceta, onModificarConsulta }: Props) {
  const [showEliminarModal, setShowEliminarModal] = useState(false);
  const [consultaAEliminar, setConsultaAEliminar] = useState<string | null>(null);
  const [consultasActivas, setConsultasActivas] = useState<Consulta[]>(consultas);

  useEffect(() => {
    setConsultasActivas(consultas);
  }, [consultas]);

  if (consultas.length === 0) return null

  const handleEliminar = (consulta: Consulta) => {
    setConsultaAEliminar(consulta.id); // Cambiar a solo el ID
    setShowEliminarModal(true);
  };

  const confirmarEliminacion = async (consultaId: string) => {
    try {
      const token = localStorage.getItem('jwtToken');
      const response = await fetch('http://localhost:8085/api/consulta/delete', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ id: consultaId }),
      });

      if (response.ok) {
        console.log('Consulta eliminada exitosamente');
        // Actualizar la lista de consultas activas
        setConsultasActivas(prev => prev.filter(consulta => consulta.id !== consultaId));
      } else {
        const errorData = await response.json();
        console.error('Error al eliminar la consulta:', errorData);
      }
    } catch (error) {
      console.error('Error al eliminar la consulta:', error);
    }
  };

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
            {consultasActivas.map((consulta, index) => (
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
                  <button onClick={() => onVerDetalleReceta(consulta)} className="text-blue-600 hover:text-blue-800 mr-2" title="Ver Resumen Historia Clínica">
                    <EyeIcon className="h-5 w-5 inline" />
                  </button>
                  <button onClick={() => onImprimirReceta(consulta)} className="text-green-600 hover:text-green-800 mr-2" title="Imprimir receta">
                    <PrinterIcon className="h-5 w-5 inline" />
                  </button>
                  <button onClick={() => handleEliminar(consulta)} className="text-red-600 hover:text-red-800 mr-2" title="Eliminar consulta">
                    <TrashIcon className="h-5 w-5 inline" />
                  </button>
                  {index === 0 && (
                    <button onClick={() => onModificarConsulta(consulta)} className="text-yellow-600 hover:text-yellow-800" title="Modificar consulta">
                      <PencilIcon className="h-5 w-5 inline" />
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {showEliminarModal && (
        <EliminarConsultaModal
          onClose={() => setShowEliminarModal(false)}
          onConfirm={confirmarEliminacion}
          consultaId={consultaAEliminar} // Asegúrate de pasar consultaId
          fechaConsulta={consultasActivas.find(consulta => consulta.id === consultaAEliminar)?.fechaConsulta ?? ''} // Proporcionar un valor por defecto
        />
      )}
    </div>
  )
}

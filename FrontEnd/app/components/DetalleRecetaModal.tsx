import { XMarkIcon, PrinterIcon } from '@heroicons/react/24/solid'
import { Consulta } from './types/types'

interface Props {
  consulta: Consulta;
  onImprimir: () => void;
  onClose: () => void;
}

export default function DetalleRecetaModal({ consulta, onImprimir, onClose }: Props) {
  return (
    <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-4xl w-full text-black">
      <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
        <XMarkIcon className="h-6 w-6" />
      </button>
      <h2 className="text-2xl font-bold mb-4">Resumen Historia Clínica</h2>
      <p><strong>Fecha:</strong> {new Date(consulta.fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}</p>
      <p><strong>Agudeza Visual OD S/C:</strong> {consulta.agudezaVisualODSC}</p>
      <p><strong>Agudeza Visual OI S/C:</strong> {consulta.agudezaVisualOISC}</p>
      <p><strong>Agudeza Visual OD C/C:</strong> {consulta.agudezaVisualODCC}</p>
      <p><strong>Agudeza Visual OI C/C:</strong> {consulta.agudezaVisualOICC}</p>
      <p><strong>Lentes para Lejos OD:</strong> {consulta.lentesParaLejosOD}</p>
      <p><strong>Lentes para Lejos OI:</strong> {consulta.lentesParaLejosOI}</p>
      <p><strong>Lentes para Cerca AO:</strong> {consulta.lentesParaCercaAO}</p>
      <p><strong>Observaciones:</strong> {consulta.observaciones}</p>
      <div className="mt-4 flex justify-end space-x-2">
        <button
          onClick={onClose}
          className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
        >
          Cancelar
        </button>
        <button
          onClick={onImprimir}
          className="bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600 flex items-center"
        >
          <PrinterIcon className="h-5 w-5 mr-2" />
          Imprimir Receta
        </button>
      </div>
    </div>
  )
}
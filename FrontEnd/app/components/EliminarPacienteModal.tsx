import { XMarkIcon } from '@heroicons/react/24/solid'

interface Props {
  onConfirm: () => void;
  onClose: () => void;
}

export default function EliminarPacienteModal({ onConfirm, onClose }: Readonly<Props>) {
  return (
    <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-4xl w-full text-black">
      <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
        <XMarkIcon className="h-6 w-6" />
      </button>
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Eliminar Paciente</h2>
      <p className="text-gray-800">¿Está seguro de que desea eliminar este paciente?</p>
      <div className="mt-4 flex justify-end space-x-2">
        <button
          onClick={onClose}
          className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
        >
          Cancelar
        </button>
        <button
          onClick={onConfirm}
          className="bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600"
        >
          Confirmar Eliminación
        </button>
      </div>
    </div>
  )
}

import { XMarkIcon } from '@heroicons/react/24/solid'

interface Props {
  consultaId: string | null; // ID de la consulta
  fechaConsulta: string; // Nueva propiedad para la fecha de la consulta
  onConfirm: (consultaId: string) => Promise<void>;
  onClose: () => void;
}

export default function EliminarConsultaModal(props: Readonly<Props>) {
  const { consultaId, fechaConsulta, onConfirm, onClose } = props;
  if (!consultaId) return null;

  const handleConfirm = async () => {
    await onConfirm(consultaId);
    onClose();
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50">
      <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-md w-full text-black">
        <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
          <XMarkIcon className="h-6 w-6" />
        </button>
        <h2 className="text-2xl font-bold mb-4">Eliminar Consulta</h2>
        <p>¿Está seguro de que desea eliminar la consulta del {new Date(fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}?</p>
        <div className="mt-4 flex justify-end space-x-2">
          <button onClick={onClose} className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400">
            Cancelar
          </button>
          <button onClick={handleConfirm} className="bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600">
            Eliminar
          </button>
        </div>
      </div>
    </div>
  );
}

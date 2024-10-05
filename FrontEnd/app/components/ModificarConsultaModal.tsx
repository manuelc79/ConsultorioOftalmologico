// components/ModificarConsultaModal.tsx
import { XMarkIcon } from '@heroicons/react/24/solid'
import { Consulta } from './types/types'

interface Props {
  consulta: Consulta;
  onInputChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  onSubmit: () => Promise<void>;
  onClose: () => void;
}

export default function ModificarConsultaModal({ consulta, onInputChange, onSubmit, onClose }: Props) {
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit();
  };

  return (
    <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-4xl w-full text-black">
      <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
        <XMarkIcon className="h-6 w-6" />
      </button>
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Modificar Consulta</h2>
      <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-4">
      <div>
          <label htmlFor="agudezaVisualOD" className="block mb-2 text-sm font-medium text-gray-700">
            Agudeza Visual OD S/C
          </label>
          <input
            type="text"
            id="agudezaVisualODSC"
            name="agudezaVisualODSC"
            value={consulta.agudezaVisualODSC}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={25}
          />
        </div>
        <div>
          <label htmlFor="agudezaVisualOISC" className="block mb-2 text-sm font-medium text-gray-700">
            Agudeza Visual OI S/C
          </label>
          <input
            type="text"
            id="agudezaVisualOISC"    
            name="agudezaVisualOISC"
            value={consulta.agudezaVisualOISC }
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={25}
          />
        </div>
        <div>
          <label htmlFor="agudezaVisualODCC" className="block mb-2 text-sm font-medium text-gray-700">
            Agudeza Visual OD C/C
          </label>
          <input
            type="text"
            id="agudezaVisualODCC"    
            name="agudezaVisualODCC"
            value={consulta.agudezaVisualODCC }
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={25}
          />
        </div>
        <div>
          <label htmlFor="agudezaVisualOICC" className="block mb-2 text-sm font-medium text-gray-700">
            Agudeza Visual OI C/C
          </label>
          <input
            type="text"
            id="agudezaVisualOICC"    
            name="agudezaVisualOICC"
            value={consulta.agudezaVisualOICC }
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={25}
          />
        </div>
        <div>
          <label htmlFor="lentesParaLejosOD" className="block mb-2 text-sm font-medium text-gray-700">
            Lentes para Lejos OD
          </label>
          <input
            type="text"
            id="lentesParaLejosOD"
            name="lentesParaLejosOD"
            value={consulta.lentesParaLejosOD}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={5}
          />
        </div>
        <div>
          <label htmlFor="lentesParaLejosOI" className="block mb-2 text-sm font-medium text-gray-700">
            Lentes para Lejos OI
          </label>
          <input
            type="text"
            id="lentesParaLejosOI"
            name="lentesParaLejosOI"
            value={consulta.lentesParaLejosOI}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={5}
          />
        </div>      
        <div>
          <label htmlFor="lentesParaCercaAO" className="block mb-2 text-sm font-medium text-gray-700">
            Lentes para Cerca AO
          </label>
          <input
            type="text"
            id="lentesParaCercaAO"
            name="lentesParaCercaAO"
            value={consulta.lentesParaCercaAO}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            maxLength={25}
          />
        </div>
        <div className="col-span-2">
          <label htmlFor="observaciones" className="block mb-2 text-sm font-medium text-gray-700">
            Observaciones
          </label>
          <textarea
            id="observaciones"
            name="observaciones"
            value={consulta.observaciones}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            rows={3}
          ></textarea>
        </div>
        <div className="col-span-2 flex justify-end space-x-2 mt-4">
          <button
            type="button"
            onClick={onClose}
            className="bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
          >
            Guardar Cambios
          </button>
        </div>
      </form>
    </div>
  )
}
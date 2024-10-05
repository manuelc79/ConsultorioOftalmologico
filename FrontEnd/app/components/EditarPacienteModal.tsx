import { useState } from 'react'
import { XMarkIcon } from '@heroicons/react/24/solid'
import { Paciente } from './types/types'

interface Props {
  pacienteEditado: Paciente;
  onInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: () => Promise<boolean>;
  onClose: () => void;
}

export default function EditarPacienteModal({ pacienteEditado, onInputChange, onSubmit, onClose }: Props) {
  const [mensaje, setMensaje] = useState('')
  const [numeroObraSocialHabilitado, setNumeroObraSocialHabilitado] = useState(pacienteEditado.ObraSocial !== '');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    onInputChange(e);
    
    if (name === 'ObraSocial') {
      setNumeroObraSocialHabilitado(value.trim() !== '');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const exito = await onSubmit();
    if (exito) {
      setMensaje('Paciente modificado exitosamente');
      setTimeout(() => {
        onClose();
      }, 2000);
    } else {
      setMensaje('Error al modificar el paciente');
    }
  };

  return (
    <div className="relative p-6 bg-white rounded-lg shadow-xl max-w-4xl w-full text-black">
      <button onClick={onClose} className="absolute top-2 right-2 text-gray-500 hover:text-red-700">
        <XMarkIcon className="h-6 w-6" />
      </button>
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Editar Paciente</h2>
      {mensaje && (
        <div className={`mb-4 p-2 rounded ${mensaje.includes('exitosamente') ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
          {mensaje}
        </div>
      )}
      <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-4">
        <div>
          <label htmlFor="apellido" className="block mb-2 text-sm font-medium text-gray-700">
            Apellido
          </label>
          <input
            type="text"
            id="apellido"
            name="apellido"
            value={pacienteEditado.apellido}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            required
          />
        </div>
        <div>
          <label htmlFor="nombre" className="block mb-2 text-sm font-medium text-gray-700">
            Nombre
          </label>
          <input
            type="text"
            id="nombre"
            name="nombre"
            value={pacienteEditado.nombre}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            required
          />
        </div>
        <div>
          <label htmlFor="telefono" className="block mb-2 text-sm font-medium text-gray-700">
            Telefono
          </label>
          <input
            type="text"
            id="telefono"
            name="telefono"
            value={pacienteEditado.telefono}
            onChange={onInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
          />
        </div>
        <div>
          <label htmlFor="ObraSocial" className="block mb-2 text-sm font-medium text-gray-700">
            Obra Social
          </label>
          <input
            type="text"
            id="ObraSocial"
            name="ObraSocial"
            value={pacienteEditado.ObraSocial}
            onChange={handleInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
          />
        </div>
        <div>
          <label htmlFor="numeroObraSocial" className="block mb-2 text-sm font-medium text-gray-700">
            Número de Obra Social
          </label>
          <input
            type="text"
            id="numeroObraSocial"
            name="numeroObraSocial"
            value={pacienteEditado.numeroObraSocial}
            onChange={handleInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md text-gray-800"
            disabled={!numeroObraSocialHabilitado}
          />
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
            className="w-full bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600"
          >
            Guardar Cambios
          </button>
        </div>
      </form>
    </div>
  )
}
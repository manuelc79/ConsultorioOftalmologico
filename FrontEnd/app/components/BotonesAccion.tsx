import { PlusCircleIcon, PencilIcon } from '@heroicons/react/24/solid' // TrashIcon icono de eliminación

interface Props {
  onNuevaConsulta: () => void;
  onEditarPaciente: () => void;
  //onEliminarPaciente: () => void;
}

export default function BotonesAccion(props: Props) { // Marcar las props como de solo lectura
  const { onNuevaConsulta, onEditarPaciente } = props; 
  return (
    <div className="flex flex-col space-y-2">
      <button
        onClick={onNuevaConsulta}
        className="bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600 flex items-center justify-center"
      >
        <PlusCircleIcon className="h-5 w-5 mr-2" />
        <span className="hidden sm:inline">Nueva Consulta</span>
      </button>
      <button
        onClick={onEditarPaciente}
        className="bg-yellow-500 text-white py-2 px-4 rounded-md hover:bg-yellow-600 flex items-center justify-center"
      >
        <PencilIcon className="h-5 w-5 mr-2" />
        <span className="hidden sm:inline">Modificar Paciente</span>
      </button>
      {/*
      <button
        onClick={onEliminarPaciente}
        className="bg-red-500 text-white py-2 px-4 rounded-md hover:bg-red-600 flex items-center justify-center"
      >
        <TrashIcon className="h-5 w-5 mr-2" />
        <span className="hidden sm:inline">Eliminar Paciente</span>
      </button>
      */}
    </div>
  )
}

import { Paciente } from './types/types'

interface Props {
  paciente: Paciente;
}

export default function DatosPaciente({ paciente }: Props) {

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Información del Paciente</h2>
      <p><strong>Nombre:</strong> {paciente.apellido} {paciente.nombre} </p>
      <p><strong>DNI:</strong> {paciente.dni}</p>
      <p><strong>Teléfono:</strong> {paciente.telefono}</p>
      <p><strong>Obra Social:</strong> {paciente.ObraSocial}</p>
      <p><strong>Número de Obra Social:</strong> {paciente.numeroObraSocial}</p>
    </div>
  )
}
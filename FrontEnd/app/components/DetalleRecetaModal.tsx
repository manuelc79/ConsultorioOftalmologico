import { XMarkIcon, PrinterIcon } from '@heroicons/react/24/solid'
import { Consulta, Paciente, Medico, Consultorio } from './types/types'

interface Props {
  consulta: Consulta;
  paciente: Paciente;
  medico: Medico;
  consultorio: Consultorio;
  onClose: () => void;
}

export default function DetalleRecetaModal({ consulta, paciente, medico, consultorio, onClose }: Readonly<Props>) {
  const imprimirReceta = () => {
    const recetaWindow = window.open('', '_blank');
    if (recetaWindow) {
      const logoSrc = consultorio.logo || 'image/Logo.png';

      recetaWindow.document.write(`
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Resumen Historia Clínica</title>
            <style>
                @page {
                    size: A4;
                    margin: 0;
                }
                body {
                    font-family: Arial, sans-serif;
                    width: 100%;
                    height: 100%;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    margin: 0;
                }
                .container {
                    width: 10cm;
                    height: 15cm;
                    padding: 10px;
                    box-sizing: border-box;
                }
                .header {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    margin-bottom: 5px;
                }
                .eye-icon {
                    font-size: 25px;
                }
                .doctor-info {
                    text-align: center;
                    flex-grow: 1;
                    line-height: 0.5;
                }
                .emergency {
                    background-color: #f0f0f0;
                    padding: 5px;
                    margin-bottom: 10px;
                    text-align: center;
                }
                .prescription-area {
                    height: 350px;
                    margin-bottom: 10px;
                    font-size: 12px;
                    text-align: left;
                    overflow: hidden;
                }
                hr {
                    border: none;
                    border-top: 2px solid black;
                }
                .footer {
                    text-align: center;
                    font-size: 14px;
                    line-height: 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <div class="eye-icon">
                        <img src="${logoSrc}" alt="Logo" style="height: 2cm;">
                    </div>
                    <div class="doctor-info">
                        <h2 style="font-size: 14px;">DR. ${medico.nombre.toUpperCase()} ${medico.apellido.toUpperCase()}</h2>
                        <p style="font-size: 12px;">${medico.especialidad.toUpperCase()}</p>
                        <p style="font-size: 12px;">M.P. ${medico.numeroMatricula}</p>
                    </div>
                </div>
                
                <div class="emergency">
                    URGENCIAS: ☎ ${medico.telefono}
                </div>
                <hr />
                
                <div class="prescription-area">
                    <p style="text-align: center; margin-top: 0px;"><strong>RESUMEN HISTORIA CLÍNICA</strong></p>
                    <p><strong>Fecha:</strong> ${new Date(consulta.fechaConsulta + 'T00:00:00Z').toLocaleDateString('es-ES', { timeZone: 'UTC' })}</p>
                    <p><strong>Paciente:</strong> ${paciente.apellido} ${paciente.nombre}</p>
                    <p><strong>DNI:</strong> ${paciente.dni}</p>
                    <p><strong>Agudeza Visual OD S/C:</strong> ${consulta.agudezaVisualODSC}</p>
                    <p><strong>Agudeza Visual OI S/C:</strong> ${consulta.agudezaVisualOISC}</p>
                    <p><strong>Agudeza Visual OD C/C:</strong> ${consulta.agudezaVisualODCC}</p>
                    <p><strong>Agudeza Visual OI C/C:</strong> ${consulta.agudezaVisualOICC}</p>
                    <p><strong>P/Lejos OD:</strong> ${consulta.lentesParaLejosOD}</p>
                    <p style="margin-left: 45px;"><strong>OI:</strong> ${consulta.lentesParaLejosOI}</p>
                    <p><strong>P/Cerca AO:</strong> ${consulta.lentesParaCercaAO}</p>
                    <p><strong>Observaciones:</strong> ${consulta.observaciones}</p>
                </div>
                
                <div class="footer">
                    <hr />
                    <p>${consultorio.domicilio} - ☎ ${consultorio.telefono}</p>
                    <p>${consultorio.localidad}</p>
                </div>
            </div>
        </body>
        </html>
      `);
      recetaWindow.document.close();
      setTimeout(() => {
        recetaWindow.print();
      }, 1000);
    }
  };


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
          onClick={imprimirReceta}
          className="bg-green-500 text-white py-2 px-4 rounded-md hover:bg-green-600 flex items-center"
        >
          <PrinterIcon className="h-5 w-5 mr-2" />
          Imprimir
        </button>
      </div>
    </div>
  )
}

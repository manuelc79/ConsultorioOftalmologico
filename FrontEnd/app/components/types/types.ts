export interface Paciente {
    apellido: string;
    nombre: string;
    telefono: string;
    dni: string;
    ObraSocial: string;
    numeroObraSocial: string;
  }
  
  export interface Consulta {
    id: string;
    fechaConsulta: string;
    agudezaVisualOISC: string;
    agudezaVisualODSC: string;
    agudezaVisualOICC: string;
    agudezaVisualODCC: string;
    lentesParaLejosOI: string;
    lentesParaLejosOD: string;
    lentesParaCercaAO: string;
    observaciones: string;
    pacienteDni: string;
    medicoId: number;
  }

  export interface Medico {
    nombre: string;
    apellido: string;
    email: string;
    especialidad: string;
    numeroMatricula: number;
    telefono: string;
  }
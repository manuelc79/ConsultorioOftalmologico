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
    id: number;
    nombre: string;
    apellido: string;
    email: string;
    especialidad: string;
    numeroMatricula: number;
    telefono: string;
  }
  
  export interface Consultorio {
    id: number;
    domicilio: string;
    telefono: string;
    localidad: string;
    logo: string;
    medicoId: number;
  }
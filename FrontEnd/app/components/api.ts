const API_BASE_URL = 'https://consultoriooftalmologico.onrender.com/api';
//const API_BASE_URL ='http://localhost:8080/';

const endpoints = {
  //Login
  login: `${API_BASE_URL}/login`,
  //Medico
  medicoFind: `${API_BASE_URL}/medico/find`, //Busca un medico
  medicoCreate: `${API_BASE_URL}/medico`, //Crea un medico
  medicoUpdate: `${API_BASE_URL}/medico`, //Actualiza un medico
  medicoDelete: `${API_BASE_URL}/medico/delete`, //Elimina un medico
  //Consultorio
  consultorioFind: `${API_BASE_URL}/consultorio/find`, //Busca un consultorio
  consultorioCreate: `${API_BASE_URL}/consultorio`, //Crea un consultorio
  consultorioUpdate: `${API_BASE_URL}/consultorio`, //Actualiza un consultorio
  consultorioDelete: `${API_BASE_URL}/consultorio/delete`, //Elimina un consultorio
  //Paciente
  pacienteFind: `${API_BASE_URL}/paciente/find`, //Busca un paciente
  pacienteCreate: `${API_BASE_URL}/paciente`, //Crea un paciente
  pacienteUpdate: `${API_BASE_URL}/paciente`, //Actualiza un paciente
  pacienteDelete: `${API_BASE_URL}/paciente/delete`, //Elimina un paciente
  //Consulta
  consultaFindPaciente: `${API_BASE_URL}/consulta/find/paciente`, //Busca las consultas de un paciente
  consultaCreate: `${API_BASE_URL}/consulta`, //Crea una consulta
  consultaUpdate: `${API_BASE_URL}/consulta`, //Actualiza una consulta
  consultaDelete: `${API_BASE_URL}/consulta/delete`, //Elimina una consulta
  consultaFindFecha: `${API_BASE_URL}/consulta/find/fecha`, //Busca las consultas de una fecha
};

export default endpoints;


package hn.clinica.views.pacientes;

import java.util.List;

import hn.clinica.data.entity.Pacientes;

public interface PacientesViewModel {
	
	void refrescarGridPacientes(List<Pacientes> pacientes);
	

}

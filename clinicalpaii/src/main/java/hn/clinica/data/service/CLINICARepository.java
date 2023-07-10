package hn.clinica.data.service;

import hn.clinica.data.entity.ResponsePacientes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CLINICARepository {

	@Headers({
	    "Content-Type: application/json",
	    "Accept-Charset: utf-8",
	    "User-Agent: Retrofit-Sample-App"
	})
	
	@GET("/pls/apex/wencellr_pav2_98_2/clinica/pacientes/")
	Call<ResponsePacientes> obtenerPacientes();
		
}

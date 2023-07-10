package hn.clinica.data.entity;

import java.io.IOException;

import hn.clinica.data.service.RepositoryClient;
import retrofit2.Call;
import retrofit2.Response;

public class CLINICARepositoryImpl {
	
	private static CLINICARepositoryImpl instance;
	private RepositoryClient client;
	
	private CLINICARepositoryImpl (String url, Long timeout) 
	{
		this.client = new RepositoryClient(url, timeout);

	}
	
	//Implementando Patron SINGLETON
	
	public static CLINICARepositoryImpl getInstance(String url, Long timeout) 
	{
		if(instance == null) 
		{
			synchronized (CLINICARepositoryImpl.class) 
			{
				
				if (instance == null) 
				{
					instance = new CLINICARepositoryImpl(url, timeout);
					
				}
				
				
			}
			
		}
		return instance;
		
	}
	
	public ResponsePacientes getPacientes() throws IOException{
		
		Call <ResponsePacientes> call = client.getDataService().obtenerPacientes();
		Response<ResponsePacientes> response = call.execute(); // AQUI ES DONDE SE CONSULTA A LA URL DE LA BASE DE DATOS.
		if(response.isSuccessful()) {
			return response.body();
		}
		else 
		{
			return null;
			
		}
	}

}

package hn.clinica.data.service;

import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepositoryClient {
	
	private Retrofit retrofit;
	private HttpLoggingInterceptor interceptor = null;
	
	public RepositoryClient (String url, Long timeout) {
		
		this.interceptor = new HttpLoggingInterceptor();
		this.interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.connectTimeout(timeout, TimeUnit.MILLISECONDS)
				.writeTimeout(timeout, TimeUnit.MILLISECONDS)
				.readTimeout(timeout, TimeUnit.MILLISECONDS)
				.build();
		retrofit = new Retrofit.Builder()
				.client(client)
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-mm-dd-'T'HH:mm:ss.SSZ").create()))
				.build();

	}
	
	public CLINICARepository getDataService() 
	{
		
		return retrofit.create(CLINICARepository.class);
		
		
	}
	

}

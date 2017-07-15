package com.kunder.ws.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.kunder.ws.model.RequestData;

@Path("/RestService") 
public class RestService {
	@POST
	@Path("/word")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String upperCaseDataJson(RequestData requestData) throws IOException {
		String jsonOutput = null;
		try {
			if (requestData != null && requestData.getData().length() == 4) {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_OK), HttpStatus.getStatusText(HttpStatus.SC_OK),
						requestData.getData() };
				jsonOutput = buildStringJson(keys, values);
			} else {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_BAD_REQUEST),
						HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST), requestData.getData() };
				jsonOutput = buildStringJson(keys, values);
			}
		} catch (Exception e) {
			try {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR),
						HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR), requestData.getData() };
				jsonOutput = buildStringJson(keys, values);
			} catch (JSONException e1) {
			}
			e.printStackTrace();
		}
		return jsonOutput;
	}

	@GET
	@Path("/time")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUtcIsoFormat(@QueryParam("value") String hora) {
		String jsonOutput = null;
		String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S Z";
		SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
		
		try {
			if(Integer.valueOf(hora) < 0 || Integer.valueOf(hora) > 23) {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR),
						HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR), hora };
				jsonOutput = buildStringJson(keys, values);
			}
			else {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.HOUR, Integer.valueOf(hora));
				Date d = now.getTime();
	
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_OK), HttpStatus.getStatusText(HttpStatus.SC_OK),
						isoFormatter.format(d) };
				jsonOutput = buildStringJson(keys, values);
			}
		} catch (Exception e) {
			try {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR),
						HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR), hora };
				jsonOutput = buildStringJson(keys, values);
			} catch (JSONException e1) {
			}
			e.printStackTrace();
		}

		return jsonOutput;
	}

	@POST
	@Path("/wordHtml")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String upperCaseDataHtml(@FormParam("data") String data, @Context HttpServletRequest servletRequest) throws IOException {
		StringBuffer url = servletRequest.getRequestURL();
		String urlRestService = url.substring(0, url.lastIndexOf("/") + 1) + "word";
		System.out.println(urlRestService);
		String[] keys = { "data" };
		String[] values = { data };
		String inputJson = null;
		try {
			inputJson = buildStringJson(keys, values);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Servicio: " + urlRestService);
		
		System.out.println("Input Json (POST): " + inputJson);

		String outputJson = clientRestPost(urlRestService, inputJson);

		System.out.println("OutPut Json: " + outputJson);

		return "URL del Servicio (POST): " + urlRestService + "\n" + "Input Json (POST): " + inputJson + "\n" + "OutPut Json: " + outputJson;
	}
	
	/**
	 * Cliente Rest Post with Json
	 * 
	 * @param urlRestService
	 * @param inputJson
	 * @return
	 * @throws MalformedURLException 
	 * @throws ProtocolException 
	 */
	private String clientRestPost(String urlRestService, String inputJson) {
		
		StringBuffer output = new StringBuffer();
		try {		
			URL url = new URL(urlRestService);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
	
			OutputStream os = conn.getOutputStream();
			os.write(inputJson.getBytes());
			os.flush();
	
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
			System.out.println("Output from Server .... \n" + output);
			String line;
			while ((line = br.readLine()) != null) {
				if (output.length() == 0) {
					output.append(line);
				} else {
					output.append("\n");
					output.append(line);
				}
			}
			conn.disconnect();
		}
		catch (Exception e) {
			try {
				String[] keys = { "code", "description", "data" };
				String[] values = { String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR),
						HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR), e.getMessage() };
				output.append(buildStringJson(keys, values));
			} catch (JSONException e1) {
			}
		}
		
		return output.toString();
	}

	private String buildStringJson(String[] keys, String[] values) throws JSONException {
		// Construyendo el Json que voy a enviar al servicio
		JSONObject jsonObject = new JSONObject();

		for (int i = 0; i < keys.length; i++) {
			jsonObject.put(keys[i], values[i]);
		}

		return jsonObject.toString();
	}
}

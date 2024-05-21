package gov.nist.hit.core.hl7v2.service.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;

import hl7.v2.validation.vs.external.client.ExternalValueSetClient;

public class ExternalValueSetCloseableHttpClient {

	CloseableHttpClient httpClient;

	public ExternalValueSetCloseableHttpClient(String apikey) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(2000) // Set the connection timeout to 2 seconds
				.setSocketTimeout(2000) // Set the request timeout to 2 seconds
				.setConnectionRequestTimeout(2000) // Set the connection request timeout to 2 seconds
				.build();

		Header[] headers = new Header[] { new BasicHeader("X-API-KEY", apikey) };
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustAllStrategy());
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(),
				NoopHostnameVerifier.INSTANCE);

		CloseableHttpClient httpClient = HttpClientBuilder.create()
				.setDefaultRequestConfig(requestConfig)
				.disableCookieManagement()
				.setSSLSocketFactory(socketFactory)
				.addInterceptorFirst(new HttpRequestInterceptor() {					
					@Override
					public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
						String vsBinding = context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString();
//					    request.addHeader("X-API-KEY", keys.getOrElse(context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString(), ""));
					    request.addHeader("X-API-KEY", "test");

					}
				})
				.build();

		HttpGet httpGet = new HttpGet("https://api.example.com");

		try {
			httpClient.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
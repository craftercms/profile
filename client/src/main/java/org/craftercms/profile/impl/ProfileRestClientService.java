/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.XMLConfiguration;
//import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * This service creates an HttpConnection and is able to service connection requests from multiple execution threads.
 * 
 * @author Alvaro Gonzalez
 *
 */
public class ProfileRestClientService {
	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int SOCKET_TIMEOUT = 60000;
	private static Log log = LogFactory.getLog(ProfileRestClientService.class);

	private static ProfileRestClientService profileRestClientService = new ProfileRestClientService();
	
	private int connectionTimeout = CONNECTION_TIMEOUT;
	private int socketTimeout = SOCKET_TIMEOUT;
	private int sslPort = 443;
	private int port = 8080;
	private int maxPerRoute = 2;
	private int maxTotal = 20;
	private String host = "localhost";
	private int defaultMaxPerRoute = 2;

	private ProfileRestClientService() {
	}

	public static ProfileRestClientService getInstance() {
		return profileRestClientService;
	}

	public DefaultHttpClient getHttpClient() {
		return getHttpClient(connectionTimeout,socketTimeout);
	}

	private DefaultHttpClient getHttpClient(int connectionTimeOut,
			int sockeTimeOut) {
		try {

			HttpParams httpParams = new BasicHttpParams();

			setParams(httpParams, connectionTimeOut, sockeTimeOut);

			SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					return true;
				}
			}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", port, PlainSocketFactory
					.getSocketFactory()));
			registry.register(new Scheme("https", sslPort, sf));

			PoolingClientConnectionManager ccm = new PoolingClientConnectionManager(
					registry);
			HttpHost localhost = new HttpHost(host, port);
			ccm.setMaxPerRoute(new HttpRoute(localhost), maxPerRoute);
			ccm.setMaxTotal(maxTotal);
			ccm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			return new DefaultHttpClient(ccm, httpParams);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new DefaultHttpClient();
		}
	}

	/**
	 * @param httpParams
	 * @param connectionTimeOut
	 * @param sockeTimeOut
	 */
	private void setParams(HttpParams httpParams, int connectionTimeOut,
			int sockeTimeOut) {

		setHttpProtocolParams(httpParams);
		setHttpConnectionParams(httpParams, connectionTimeOut, sockeTimeOut);

		httpParams.setBooleanParameter("http.protocol.expect-continue", false);
	}

	/**
	 * @param httpParams
	 */
	private void setHttpProtocolParams(HttpParams httpParams) {
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, "utf-8");
	}

	/**
	 * @param httpParams
	 * @param connectionTimeOut
	 * @param sockeTimeOut
	 */
	private void setHttpConnectionParams(HttpParams httpParams,
			int connectionTimeOut, int sockeTimeOut) {
		HttpConnectionParams
				.setConnectionTimeout(httpParams, connectionTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, sockeTimeOut);
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getSslPort() {
		return sslPort;
	}

	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}

}

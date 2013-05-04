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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.constants.AttributeConstants;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.*;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.BadRequestException;
import org.craftercms.profile.exceptions.ResourceNotFoundException;
import org.craftercms.profile.exceptions.RestException;
import org.craftercms.profile.exceptions.UnauthorizedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class ProfileRestClientImpl implements ProfileClient {

	private ProfileRestClientService clientService = ProfileRestClientService.getInstance();
	private ObjectMapper objectMapper = new ObjectMapper();

	private int port = 8080;
	private String scheme = "http";
	private String host = "localhost";
	private String profileAppPath = "/crafter-profile";

	private static Log log = LogFactory.getLog(ProfileRestClientImpl.class);

	private final TypeReference<List<Profile>> PROFILE_LIST_TYPE = new TypeReference<List<Profile>>() { };
	private final TypeReference<List<Role>> ROLE_LIST_TYPE = new TypeReference<List<Role>>() { };
    private final TypeReference<List<Tenant>> TENANT_LIST_TYPE = new TypeReference<List<Tenant>>() { };
	private final TypeReference<Map<String, Serializable>> MAP_STRING_SERIALIZABLE_TYPE = new TypeReference<Map<String, Serializable>>() { };
	private String propertiesFile;
	private List<String> locations = new ArrayList<String>();

	public ProfileRestClientImpl() {

	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileCount(java.lang.String)
	 */
	public long getProfileCount(String appToken, String tenantName) {
		long count = 0;
		HttpEntity entity = null;

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + "count.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				count = (Long) objectMapper.readValue(entity.getContent(), Long.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfile(java.lang.String, java.lang.String)
	 */
	public Profile getProfile(String appToken, String profileId) {
		Profile profile = new Profile();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByTicket(java.lang.String, java.lang.String)
	 */
	public Profile getProfileByTicket(String appToken, String ticket) {
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/ticket/" + ticket + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by ticket='%s'", ticket), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByTicketWithAttributes(java.lang.String, java.lang.String, java.util.List)
	 */
	public Profile getProfileByTicketWithAttributes(String appToken, String ticket, List<String> attributes) {
		HttpEntity entity = null;
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (attributes != null && attributes.size() > 0) {
			for (String attribute:attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/ticket/" + ticket + "/with_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by ticket='%s'", ticket), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByTicketWithAllAttributes(java.lang.String, java.lang.String)
	 */
	public Profile getProfileByTicketWithAllAttributes(String appToken, String ticket) {
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/ticket/" + ticket + "/with_all_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by ticket='%s'", ticket), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByUsername(java.lang.String, java.lang.String)
	 */
	public Profile getProfileByUsername(String appToken, String username, String tenantName) {
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/username/" + username + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by username='%s'", username), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByUsernameWithAttributes(java.lang.String, java.lang.String, java.util.List)
	 */
	public Profile getProfileByUsernameWithAttributes(String appToken, String username, String tenantName,List<String> attributes) {
		HttpEntity entity = null;
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));

		if (attributes != null && attributes.size() > 0) {
			for (String attribute:attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/username/" + username + "/with_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by username='%s'", username), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileByUsernameWithAllAttributes(java.lang.String, java.lang.String)
	 */
	public Profile getProfileByUsernameWithAllAttributes(String appToken, String username, String tenantName) {
		HttpEntity entity = null;
		Profile profile = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/username/" + username + "/with_all_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the profile by username='%s'", username), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileWithAttributes(java.lang.String, java.lang.String, java.util.List)
	 */
	public Profile getProfileWithAttributes(String appToken, String profileId, List<String> attributes) {
		HttpEntity entity = null;
		Profile profile = new Profile();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (attributes != null) {
			for (String attribute : attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + "/with_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileWithAllAttributes(java.lang.String, java.lang.String)
	 */
	public Profile getProfileWithAllAttributes(String appToken, String profileId) {
		HttpEntity entity = null;
		Profile profile = new Profile();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + "/with_all_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#createProfile(java.lang.String, java.util.Map)
	 */
	public Profile createProfile(String appToken, Map<String, Serializable> queryParams) {
		HttpEntity entity = null;
		Profile profile = new Profile();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (queryParams != null && !queryParams.isEmpty() && queryParams.keySet() != null) {
			@SuppressWarnings("rawtypes")
			Iterator it = queryParams.keySet().iterator();
			boolean findRole = false;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.equals(ProfileConstants.ROLES)) {
					ArrayList<String> list = (ArrayList<String>)queryParams.get(key);
					if (list != null) {
						for (String s:list) {
							qparams.add(new BasicNameValuePair(ProfileConstants.ROLES, s));
						}
					} else {
						qparams.add(new BasicNameValuePair(ProfileConstants.ROLES, ProfileConstants.DEFAULT_ROLE));
					}
				} else {
					qparams.add(new BasicNameValuePair(key, (String) queryParams.get(key)));
				}
			}
			
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + "create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);

			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#createProfile(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.util.Map)
	 */
	public Profile createProfile(String appToken, String userName, String password, Boolean active, String tenantName, Map<String, Serializable> attributes) {
		attributes.put("userName", userName);
		attributes.put("password", password);
		attributes.put("active", active);
		attributes.put("tenantName", tenantName);
		return createProfile(appToken, attributes);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#updateProfile(java.lang.String, java.util.Map)
	 */
	public Profile updateProfile(String appToken, Map<String, Serializable> queryParams) {
		HttpEntity entity = null;
		Profile profile = new Profile();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (queryParams != null && !queryParams.isEmpty() && queryParams.keySet() != null) {
			@SuppressWarnings("rawtypes")
			Iterator it = queryParams.keySet().iterator();
			boolean findRole = false;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.equals(ProfileConstants.ROLES)) {
					ArrayList<String> list = (ArrayList<String>)queryParams.get(key);
					for (String s:list) {
						qparams.add(new BasicNameValuePair("roles", s));
					}
				} else {
					qparams.add(new BasicNameValuePair(key, (String) queryParams.get(key)));
				}
				
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + "update.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() == 200) {
				profile = (Profile) objectMapper.readValue(entity.getContent(), Profile.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profile;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#setAttributesForProfile(java.lang.String, java.lang.String, java.util.Map)
	 */
	public void setAttributesForProfile(String appToken, String profileId, Map<String, Serializable> queryParams) {
		HttpEntity entity = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (queryParams != null && !queryParams.isEmpty() && queryParams.keySet() != null) {
			@SuppressWarnings("rawtypes")
			Iterator it = queryParams.keySet().iterator();
			Object value;
			while (it.hasNext()) {
				String key = (String) it.next();
				value = queryParams.get(key);
				if (value instanceof String) {
					qparams.add(new BasicNameValuePair(key, (String) value));
				}
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + "set_attributes/" + profileId + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#deleteProfile(java.lang.String, java.lang.String)
	 */
	public void deleteProfile(String appToken, String profileId) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + "delete/" + profileId + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#deleteAllAttributesForProfile(java.lang.String, java.lang.String)
	 */
	public void deleteAllAttributesForProfile(String appToken, String profileId) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port,
					profileAppPath + "/api/2/profile/" + profileId + "/delete_all_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);
			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#deleteAttributesForProfile(java.lang.String, java.lang.String, java.util.List)
	 */
	public void deleteAttributesForProfile(String appToken, String profileId, List<String> attributes) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		if (attributes != null) {
			for (String attribute : attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + "/delete_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);

			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getAttributesForProfile(java.lang.String, java.lang.String, java.util.List)
	 */
	public Map<String, Serializable> getAttributesForProfile(String appToken, String profileId, List<String> attributes) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		HttpEntity entity = null;

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		if (attributes != null) {
			for (String attribute : attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + "/attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				result = (Map<String, Serializable>) objectMapper.readValue(entity.getContent(), MAP_STRING_SERIALIZABLE_TYPE);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getAllAttributesForProfile(java.lang.String, java.lang.String)
	 */
	public Map<String, Serializable> getAllAttributesForProfile(String appToken, String profileId) {
		Map<String, Serializable> result = new HashMap<String, Serializable>();
		HttpEntity entity = null;

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/" + profileId + "/all_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				result = (Map<String, Serializable>) objectMapper.readValue(entity.getContent(), MAP_STRING_SERIALIZABLE_TYPE);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfileRange(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.List)
	 */
	public List<Profile> getProfileRange(String appToken, String tenantName, int start, int end, String sortBy, String sortOrder, List<String> attributes) {
		List<Profile> profiles = new ArrayList<Profile>();
		HttpEntity entity = null;

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("sortBy", sortBy));
		qparams.add(new BasicNameValuePair("sortOrder", sortOrder));
		qparams.add(new BasicNameValuePair("start", String.valueOf(start)));
		qparams.add(new BasicNameValuePair("end", String.valueOf(end)));
		if (attributes != null && attributes.size() > 0) {
			for (String attribute:attributes) {
				qparams.add(new BasicNameValuePair("attributes", attribute));
			}
		}
		
		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/range.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profiles = (List<Profile>) objectMapper.readValue(entity.getContent(), PROFILE_LIST_TYPE);
				
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profiles;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfiles(java.lang.String, java.util.List)
	 */
	public List<Profile> getProfiles(String appToken, List<String> profileIds) {
		HttpEntity entity = null;
		List<Profile> profiles = new ArrayList<Profile>();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));

		if (profileIds != null) {
			for (String profileId : profileIds) {
				qparams.add(new BasicNameValuePair("profileIdList", profileId));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/ids.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);


			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profiles = (List<Profile>) objectMapper.readValue(entity.getContent(), PROFILE_LIST_TYPE);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profiles;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getProfilesWithAllAttributes(java.lang.String, java.util.List)
	 */
	public List<Profile> getProfilesWithAllAttributes(String appToken, List<String> profileIds) {
		HttpEntity entity = null;
		List<Profile> profiles = new ArrayList<Profile>();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));

		if (profileIds != null) {
			for (String profileId : profileIds) {
				qparams.add(new BasicNameValuePair("profileIdList", profileId));
			}
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/ids/with_attributes.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profiles = (List<Profile>) objectMapper.readValue(entity.getContent(), PROFILE_LIST_TYPE);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profiles;
	}

	public String getAppToken(String appUsername, String appPassword) throws AppAuthenticationFailedException {
		String appToken = null;
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			PasswordEncoder encoder = new Md5PasswordEncoder();
		    String hashedPassword = encoder.encodePassword(appPassword, null);
		    
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("username", appUsername));
			qparams.add(new BasicNameValuePair("password", hashedPassword));

			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/auth/app_token.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);

			HttpGet httpget = new HttpGet(uri);
			
			response = clientService.getHttpClient().execute(httpget);
			
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				appToken = (String) objectMapper.readValue(entity.getContent(), String.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (AppAuthenticationFailedException e) {
			log.error("", e);
			throw e;
		} catch (Exception e) {
			log.error("", e);
			throw new AppAuthenticationFailedException(e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return appToken;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getTicket(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getTicket(String appToken, String username, String password,String tenantName) throws UserAuthenticationFailedException {
		String ticket = null;
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			PasswordEncoder encoder = new Md5PasswordEncoder();
		    String hashedPassword = encoder.encodePassword(password, null);
			
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("appToken", appToken));
			qparams.add(new BasicNameValuePair("username", username));
			qparams.add(new BasicNameValuePair("password", hashedPassword));
			qparams.add(new BasicNameValuePair("tenantName", tenantName));

			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/auth/ticket.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);

			HttpGet httpget = new HttpGet(uri);

			response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				ticket = (String) objectMapper.readValue(entity.getContent(), String.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error("", e);
			throw new UserAuthenticationFailedException(e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return ticket;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#isTicketValid(java.lang.String, java.lang.String)
	 */
	public boolean isTicketValid(String appToken, String ticket) {
		boolean validTicket = false;
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("appToken", appToken));
			qparams.add(new BasicNameValuePair("ticket", ticket));

			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/auth/ticket/validate.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);

			HttpGet httpget = new HttpGet(uri);

			response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				validTicket = (Boolean) objectMapper.readValue(entity.getContent(), Boolean.class);
			} else if (response.getStatusLine().getStatusCode() == 500) {
				// ticket format was wrong
				log.debug(String.format("500 error : %s", response.getStatusLine().getReasonPhrase()));
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return validTicket;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#invalidateTicket(java.lang.String, java.lang.String)
	 */
	public void invalidateTicket(String appToken, String ticket) {
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("appToken", appToken));
			qparams.add(new BasicNameValuePair("ticket", ticket));

			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/auth/ticket/invalidate.json", null, null);

			HttpPost httppost = new HttpPost(uri);
			httppost.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8));

			response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();
			handleErrorStatus(response.getStatusLine(), entity);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

	}
	
	//****** TENANT services  *****//
	
	@Override
	public Tenant createTenant(String appToken, String tenantName, List<String> roles, List<String> domains, boolean createDefaultRoles) {
		HttpEntity entity = null;
		Tenant tenant = new Tenant();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		qparams.add(new BasicNameValuePair("createDefaultRoles", Boolean.toString(createDefaultRoles)));

        if(roles != null && !roles.isEmpty()){
            for(String role : roles){
                qparams.add(new BasicNameValuePair(ProfileConstants.ROLES, role));
            }
        }

        if(domains != null && !domains.isEmpty()){
            for(String domain : domains){
                qparams.add(new BasicNameValuePair(ProfileConstants.DOMAINS, domain));
            }
        }

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/" + "create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				tenant = (Tenant) objectMapper.readValue(entity.getContent(), Tenant.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return tenant;
	}

    @Override
    public Tenant updateTenant(String appToken, String id, String tenantName, List<String> roles, List<String> domains) {
        HttpEntity entity = null;
        Tenant tenant = new Tenant();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("_id", id));
        qparams.add(new BasicNameValuePair("appToken", appToken));
        qparams.add(new BasicNameValuePair("tenantName", tenantName));

        if(roles != null && !roles.isEmpty()){
            for(String role : roles){
                qparams.add(new BasicNameValuePair(ProfileConstants.ROLES, role));
            }
        }

        if(domains != null && !domains.isEmpty()){
            for(String domain : domains){
                qparams.add(new BasicNameValuePair(ProfileConstants.DOMAINS, domain));
            }
        }

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/" + "update.json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpPost httppost = new HttpPost(uri);

            HttpResponse response = clientService.getHttpClient().execute(httppost);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                tenant = (Tenant) objectMapper.readValue(entity.getContent(), Tenant.class);
            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return tenant;
    }

    @Override
	public void deleteTenant(String appToken, String tenantName) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/" + "delete/" + tenantName + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}
	
	@Override
	public Tenant getTenantByName(String appToken, String tenantName) {
		Tenant tenant = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/get/" + tenantName + ".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				tenant = (Tenant) objectMapper.readValue(entity.getContent(), Tenant.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the tenant by tenant name='%s'", tenantName), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return tenant;
	}
	
	@Override
	public Tenant getTenantById(String appToken, String tenantName) {
		Tenant tenant = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/"+ tenantName +"/get_id.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				tenant = (Tenant) objectMapper.readValue(entity.getContent(), Tenant.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the tenant by tenant id='%s'", tenantName), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return tenant;
	}
	
	@Override
	public Tenant getTenantByTicket(String appToken, String ticket) {
		Tenant tenant = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/ticket/"+ ticket +".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				tenant = (Tenant) objectMapper.readValue(entity.getContent(), Tenant.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (Exception e) {
			log.error(String.format("could not get the tenant by ticket ='%s'", ticket), e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return tenant;
	}

    @Override
    public boolean exitsTenant(String appToken, String tenantName) {
        boolean exist = false;

        HttpEntity entity = null;

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("appToken", appToken));

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/exists/"+ tenantName + ".json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpGet httpget = new HttpGet(uri);

            HttpResponse response = clientService.getHttpClient().execute(httpget);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                exist = (Boolean) objectMapper.readValue(entity.getContent(), Boolean.class);
            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return exist;
    }

    @Override
    public long getTenantCount(String appToken) {
        long count = 0;
        HttpEntity entity = null;

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("appToken", appToken));

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/" + "count.json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpGet httpget = new HttpGet(uri);


            HttpResponse response = clientService.getHttpClient().execute(httpget);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                count = (Long) objectMapper.readValue(entity.getContent(), Long.class);
            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return count;
    }

    @Override
    public List<Tenant> getTenantRange(String appToken, String sortBy, String sortOrder, int start, int end) {
        List<Tenant> tenants = new ArrayList<Tenant>();
        HttpEntity entity = null;

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("appToken", appToken));
        qparams.add(new BasicNameValuePair("sortBy", sortBy));
        qparams.add(new BasicNameValuePair("sortOrder", sortOrder));
        qparams.add(new BasicNameValuePair("start", String.valueOf(start)));
        qparams.add(new BasicNameValuePair("end", String.valueOf(end)));

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/range.json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpGet httpget = new HttpGet(uri);

            HttpResponse response = clientService.getHttpClient().execute(httpget);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                tenants = (List<Tenant>) objectMapper.readValue(entity.getContent(), TENANT_LIST_TYPE);

            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return tenants;
    }

    @Override
    public List<Tenant> getAllTenants(String appToken) {
        List<Tenant> tenantList = null;
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("appToken", appToken));
        HttpEntity entity = null;

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/tenant/get_all_tenants"+".json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpGet httpget = new HttpGet(uri);

            HttpResponse response = clientService.getHttpClient().execute(httpget);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                tenantList = (List<Tenant>) objectMapper.readValue(entity.getContent(), TENANT_LIST_TYPE);
            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (Exception e) {
            log.error(String.format("Could not get tenant List"), e);
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return tenantList;
    }

    /*** END TENANT services *******/
	
	/***************BASE SCHEMA SERVICES****************/
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#deleteAttributeForSchema(java.lang.String, java.util.String)
	 */
	public void deleteAttributeForSchema(String appToken, String tenantName, String attributeName) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		HttpEntity entity = null;

		if (attributeName != null && !attributeName.isEmpty()) {
			qparams.add(new BasicNameValuePair(AttributeConstants.NAME, attributeName));
		}

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/schema/delete_attribute.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);

			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#setAttributeForSchema(java.lang.String, org.craftercms.profile.domain.Attribute)
	 */
	public void setAttributeForSchema(String appToken, String tenantName, Attribute attribute) {
		HttpEntity entity = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		if (attribute != null) {
            qparams.add(new BasicNameValuePair(AttributeConstants.NAME, attribute.getName()));
            qparams.add(new BasicNameValuePair(AttributeConstants.LABEL, attribute.getLabel()));
            qparams.add(new BasicNameValuePair(AttributeConstants.ORDER, String.valueOf(attribute.getOrder())));
            qparams.add(new BasicNameValuePair(AttributeConstants.TYPE, attribute.getType()));
            qparams.add(new BasicNameValuePair(AttributeConstants.CONSTRAINT, attribute.getConstraint()));
            qparams.add(new BasicNameValuePair(AttributeConstants.REQUIRED, String.valueOf(attribute.isRequired())));
		}
		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/schema/" + "set_attribute.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getSchema(java.lang.String)
	 */
	public Schema getSchema(String appToken, String tenantName) {
		Schema schema = new Schema();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/schema/get_schema/"+ tenantName +".json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				schema = (Schema) objectMapper.readValue(entity.getContent(), Schema.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return schema;
	}
	
	/***************END BASE SCHEMA SERVICES*****************/

	/**
	 * Gets HttpClient instance
	 * @return current HttpClient instance
	 */
//	public HttpClient getHttpclient() {
//		return httpclient;
//	}

	/**
	 * Sets a new HttpClient instance
	 * @param httpclient the new HttpClient
	 */
//	public void setHttpclient(HttpClient httpclient) {
//		this.httpclient = httpclient;
//	}

	/**
	 * Gets object mapper
	 * @return object mapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * Sets an object mapper
	 * @param objectMapper The new object mapper instance.
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets the scheme
	 * 
	 * @return scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * Sets the scheme. Default is http
	 * @param scheme new value
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * Gets the host
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host name. Default is localhost
	 * @param host the new host
	 */
	public void setHost(String host) {
		this.host = host;
		clientService.setHost(host);
	}

	/**
	 * Gets the port. Default is 8080
	 * @return the port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port
	 * 
	 * @param port number will be used
	 */
	public void setPort(int port) {
		this.port = port;
		clientService.setPort(port);
	}
	
	/**
	 * Sets the port
	 * 
	 * @param port number will be used
	 */
	public void setSslPort(int sslPort) {
		
		clientService.setSslPort(sslPort);
	}
	
	/**
	 * max total connection 
	 * 
	 * @param maxTotal total connection
	 */
	public void setMaxTotal(int maxTotal) {
		clientService.setMaxTotal(maxTotal);
	}
	
	/**
	 * Sets the max per route
	 * 
	 * @param maxPerRoute number will be used
	 */
	public void setMaxPerRoute(int maxPerRoute) {
		clientService.setMaxPerRoute(maxPerRoute);
	}
	
	/**
	 * Sets default max per route
	 * @param defaultMaxPerRoute
	 */
	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		clientService.setDefaultMaxPerRoute(defaultMaxPerRoute);
	}

	/**
	 * Gets the profile App path. Default is /crafter-profile
	 * 
	 * @return profile app path
	 */
	public String getProfileAppPath() {
		return profileAppPath;
	}

	/**
	 * Sets the profileAppPath
	 * @param profileAppPath profile path
	 */
	public void setProfileAppPath(String profileAppPath) {
		this.profileAppPath = profileAppPath;
	}

	private void handleErrorStatus(StatusLine statusLine, HttpEntity entity) throws RestException {
		@SuppressWarnings("rawtypes")
		Map map = null;
		String message = "";

		try {
			map = objectMapper.readValue(entity.getContent(), Map.class);
			message = (map != null) ? (String) map.get("message") : "";
		} catch (Exception e) {
			// ignore the error
		}

		String errorMsg = String.format("Received HTTP status code '%d' reason '%s' errorMsg : %s", statusLine.getStatusCode(),
				statusLine.getReasonPhrase(), message);

		switch (statusLine.getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_ACCEPTED:
		case HttpStatus.SC_CREATED:
		case HttpStatus.SC_NO_CONTENT:
			// no errors to throw.
			break;
		case HttpStatus.SC_NOT_FOUND:
			throw new ResourceNotFoundException(errorMsg);
		case HttpStatus.SC_UNAUTHORIZED:
			throw new UnauthorizedException(errorMsg);
		case HttpStatus.SC_BAD_REQUEST:
			throw new BadRequestException(errorMsg);
		default:
			throw new RestException(errorMsg);
		}
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#setPropertiesFile(java.lang.String)
	 */
	public void setPropertiesFile(String fileUrl) {
		this.propertiesFile = fileUrl;
		
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.craftercms.profile.httpclient.ProfileRestClient#getPropertiesFile()
	 */
	public String getPropertiesFile() {
		return this.propertiesFile;
	}
	
	private InputStream initPropertiesInputStream() {
		if (this.propertiesFile!=null && !this.propertiesFile.equals("")) {
			return this.getClass().getResourceAsStream(propertiesFile);
		}
		return this.getClass().getResourceAsStream("/profile-client-custom.properties");
	}

	/*****************ROLES SERVICES******************************/
	@Override
	public List<Role> getAllRoles(String appToken, String tenantName) {
		List<Role> roles = new ArrayList<Role>();
		
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		HttpEntity entity = null;

		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/role/get_roles.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				roles = (List<Role>) objectMapper.readValue(entity.getContent(), ROLE_LIST_TYPE);
				
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return roles;
	}

    @Override
    public List<Role> getAllRoles(String appToken) {
        List<Role> roles = new ArrayList<Role>();


        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("appToken", appToken));
        HttpEntity entity = null;

        try {
            URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/role/get_all_roles.json",
                    URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
            HttpGet httpget = new HttpGet(uri);

            HttpResponse response = clientService.getHttpClient().execute(httpget);
            entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                roles = (List<Role>) objectMapper.readValue(entity.getContent(), ROLE_LIST_TYPE);

            } else {
                handleErrorStatus(response.getStatusLine(), entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("Could not consume entity", e);
            }
        }

        return roles;
    }


    @Override
	public Role createRole(String appToken, String roleName, String tenantName) {
		HttpEntity entity = null;
		Role role = new Role();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("roleName", roleName));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		
		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/role/" + "create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				role = (Role) objectMapper.readValue(entity.getContent(), Role.class);
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return role;
	}

	@Override
	public void deleteRole(String appToken, String roleName,String tenantName) {
		HttpEntity entity = null;
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("roleName", roleName));
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		
		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/role/" + "delete.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpPost httppost = new HttpPost(uri);

			HttpResponse response = clientService.getHttpClient().execute(httppost);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				handleErrorStatus(response.getStatusLine(), response.getEntity());
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
		
	}
	
	public List<Profile> getProfilesByRoleName(String appToken, String roleName, String tenantName) {
		List<Profile> profiles = new ArrayList<Profile>();
		HttpEntity entity = null;

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("tenantName", tenantName));
		qparams.add(new BasicNameValuePair("appToken", appToken));
		qparams.add(new BasicNameValuePair("roleName", roleName));
		
		
		try {
			URI uri = URIUtils.createURI(scheme, host, port, profileAppPath + "/api/2/profile/profile_role.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = clientService.getHttpClient().execute(httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				profiles = (List<Profile>) objectMapper.readValue(entity.getContent(), PROFILE_LIST_TYPE);
				
			} else {
				handleErrorStatus(response.getStatusLine(), entity);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}

		return profiles;
		
	}

	/*****************END ROLES SERVICES******************************/

/*
		This is for quick testing...
	public static void main(String[] args) {
		ProfileRestClientImpl client = new ProfileRestClientImpl();
		client.setPort(8080);

		try {
			String appToken = client.getAppToken("craftersocial", "craftersocial");
			log.debug(String.format("appToken = '%s'", appToken));

			String ticket = client.getTicket(appToken, "vjalilov", "vagif");
			log.debug(String.format("ticket = '%s'", ticket));

			boolean validTicket = client.isTicketValid(appToken, ticket);
			log.debug(String.format("isTicketValid = '%b'", validTicket));

			Profile profile = client.getProfileByTicket(appToken, ticket, Arrays.asList(new String[]{"title", "company", "non-existent"}));
			log.debug(String.format("got profile %s", (profile == null) ? "null" : profile.toString()));

			client.invalidateTicket(appToken, ticket);
			log.debug("invalidated ticket");

			validTicket = client.isTicketValid(appToken, ticket);
			log.debug(String.format("isTicketValid = '%b'", validTicket));

			profile = client.getProfileByTicket(appToken, ticket, null);
			log.debug(String.format("got profile %s", (profile == null) ? "null" : profile.toString()));

		} catch (AppAuthenticationFailedException e) {
			e.printStackTrace();
		} catch (UserAuthenticationFailedException e) {
			e.printStackTrace();
		}
	}
*/
}
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
package org.craftercms.profile.domain;

import java.io.Serializable;
import java.util.Date;

import org.craftercms.profile.security.PersistentTenantRememberMeToken;
import org.springframework.data.annotation.Id;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

public class Ticket implements Serializable {
	private static final long serialVersionUID = 3599727071983459084L;
	
	@Id
	private String series;
	private String username;
	private String tokenValue;
	private Date date;
	private String tenantName;

	public Ticket() {
	}
	
	public Ticket(String username, String series, String tokenValue, Date date) {
		super();
		this.username = username;
		this.series = series;
		this.tokenValue = tokenValue;
		this.date = date;
	}

	public Ticket(PersistentTenantRememberMeToken token) {
		this.username = token.getUsername();
		this.series = token.getSeries();
		this.tokenValue = token.getTokenValue();
		this.date = token.getDate();
		this.tenantName = token.getTenantName();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public PersistentRememberMeToken toPersistentRememberMeToken() {
		return new PersistentTenantRememberMeToken(username, series, tokenValue, date, tenantName);
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
}

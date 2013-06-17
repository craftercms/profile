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
package org.craftercms.profile.security.util;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.StringUtils;

public  final class TicketUtils {
	private static final String DELIMITER = ":";
	
	private TicketUtils() {
	}

	public static String getTicketSeries(String ticket) {
		return decodeTicket(ticket)[0];
	}

	public static String[] decodeTicket(String ticket) {
		String[] tokens = new String[]{null,null};
		String decodeTicket = ticket;
		for (int j = 0; j < decodeTicket.length() % 4; j++) {
			decodeTicket = decodeTicket + "=";
		}

		if (Base64.isBase64(decodeTicket.getBytes())) {

			String cookieAsPlainText = new String(Base64.decode(decodeTicket.getBytes()));

			tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

			if ((tokens[0].equalsIgnoreCase("http") || tokens[0].equalsIgnoreCase("https")) && tokens[1].startsWith("//")) {
				// Assume we've accidentally split a URL (OpenID identifier)
				String[] newTokens = new String[tokens.length - 1];
				newTokens[0] = tokens[0] + ":" + tokens[1];
				System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
				tokens = newTokens;
			}
		}

		return tokens;
	}
}

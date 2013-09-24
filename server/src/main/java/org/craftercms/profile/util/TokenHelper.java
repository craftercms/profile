package org.craftercms.profile.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.profile.security.util.crypto.CipherPasswordChangeToken;
import org.springframework.util.StringUtils;

public class TokenHelper {
	
	private static TokenHelper currentInstance = null;
	
	private TokenHelper() {
		
	}
	
	public static TokenHelper getInstance() {
		if (currentInstance == null) {
			currentInstance = new TokenHelper();
		}
		return currentInstance;
	}
	
	
	/**
     * Checks is the token has not expired.
     * 
     * @param dateStr creation date of this token. It shouldn't be older than the expiry time(minutes)
     * 
     * @return <code>true</code> if the dateStr is not older than the expiry value in minutes, <code>false</code> otherwise
     * 
     * @throws ParseException If an error occurs when the date is parsed.
     */
    public boolean isValidTokenDate(String dateStr, int expiryMinutes) throws ParseException {
        Date tokenDate = DateFormat.getDateTimeInstance().parse(dateStr);
        Calendar expiryDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        expiryDate.setTime(tokenDate);
        expiryDate.add(Calendar.MINUTE, expiryMinutes); 

        return currentDate.before(expiryDate);
    }
    
    public String getRawValue(String tenantName, String username) {
        return tenantName + CipherPasswordChangeToken.SEP + username + CipherPasswordChangeToken.SEP + DateFormat
            .getDateTimeInstance().format(new Date());
    }

    /**
     * Split token using | character to split them
     * 
     * @param tokens Token value
     * 
     * @return the array containing each value of the token
     */
    public String[] splitTokens(String tokens) {
        String[] data = new String[3];
        String[] result = StringUtils.split(tokens, CipherPasswordChangeToken.SEP);
        data[0] = result[0];
        result = StringUtils.split(result[1], CipherPasswordChangeToken.SEP);
        data[1] = result[0];
        if (result.length > 1) {
            data[2] = result[1];
        }
        return data;
    }
    
    public String createBaseUrl(HttpServletRequest request,
			String urlToUpdate) {
    	if (request == null || urlToUpdate == null || urlToUpdate.equals("")) {
    		return "";
    	}
    	String url = urlToUpdate;
    	int index = request.getRequestURL().indexOf(request.getRequestURI());
    	if (index >= 0) {
    		String baseUri = request.getRequestURL().substring(0, index);
    		if (baseUri.endsWith("/") && urlToUpdate.startsWith("/")) {
    			url = baseUri + urlToUpdate.substring(1);
    		} else if (baseUri.endsWith("/")) {
    			url = baseUri + urlToUpdate;
    		} else if (urlToUpdate.startsWith("/")) {
	    		url = baseUri + urlToUpdate;
	    	} else {
	    		url = baseUri + "/" + urlToUpdate;
	    	}
    	}
    	return url;
	}

}

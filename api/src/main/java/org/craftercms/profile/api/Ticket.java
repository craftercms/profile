package org.craftercms.profile.api;

import java.util.Date;

/**
 * An authentication ticket for a profile.
 *
 * @author avasquez
 */
public class Ticket {

    private String id;
    private String username;
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

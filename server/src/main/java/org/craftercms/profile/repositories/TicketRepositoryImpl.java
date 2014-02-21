package org.craftercms.profile.repositories;

import java.util.Date;

import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.exceptions.TicketException;
import org.craftercms.profile.security.util.TicketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic Ticket Repository Implementation.
 */

public class TicketRepositoryImpl extends JongoRepository<Ticket> implements TicketRepository {

    public static final String PROFILE_TICKET_BY_USERNAME = "profile.ticket.ByUsername";
    public static final String PROFILE_TICKET_EXPIRATION_DATE_OLDER = "profile.ticket.expirationDateOlder";
    public static final String PROFILE_TICKET_GET_BY_ID = "profile.ticket.getById";
    private Logger log = LoggerFactory.getLogger(TicketRepositoryImpl.class);

    /**
     * Creates A instance of a Jongo Repository.
     */
    public TicketRepositoryImpl() throws MongoDataException {
        super();
    }

    @Override
    public void removeUserTickets(final String username) throws TicketException {
        log.debug("Removing Tickets for {}", username);
        String query = getQueryFor(PROFILE_TICKET_BY_USERNAME);
        try {
            remove(query, username);
        } catch (MongoDataException e) {
            log.error("Unable to delete tickets for user " + username, e);
            throw new TicketException("Unable to delete tickets for user", e);
        }
    }

    @Override
    public void removeTicketsOlderThan(final long expirationSeconds) throws TicketException {
        Date expireBefore = new Date(System.currentTimeMillis() - (expirationSeconds * 1000));
        log.debug("About to remove tickets older than {}", expireBefore);
        String query = getQueryFor(PROFILE_TICKET_EXPIRATION_DATE_OLDER);
        try {
            remove(query, expireBefore);
        } catch (MongoDataException ex) {
            log.error("Unable to Delete tickets older than " + expireBefore, ex);
            throw new TicketException("Unable to delete tickets ", ex);
        }
    }

    @Override
    public Ticket findByTicket(final String ticketStr) throws TicketException {
        String series = TicketUtils.getTicketSeries(ticketStr);
        return findBySeries(series);
    }

    @Override
    public Ticket findByUsername(final String username) throws TicketException {
        log.debug("Finding tickets for user {}", username);
        try {
            return findOne(getQueryFor(PROFILE_TICKET_BY_USERNAME), username);
        } catch (MongoDataException ex) {
            log.error("Unable to find tickets for user", ex);
            throw new TicketException("Unable to find ");
        }
    }

    @Override
    public Ticket findBySeries(final String series) throws TicketException {
        log.debug("Finding tickets for series {}", series);
        if (series != null) {
            try {
                return super.findOne(getQueryFor(PROFILE_TICKET_GET_BY_ID), series);
            } catch (MongoDataException e) {
                log.debug("Error finding ticket by id", e);
                throw new TicketException("Unable to find ticket", e);
            }
        } else {
            log.error("Unable to get series from ticket {}", series);
            return null;
        }
    }
}

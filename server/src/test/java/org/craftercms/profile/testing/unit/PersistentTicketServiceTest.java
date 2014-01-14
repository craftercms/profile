package org.craftercms.profile.testing.unit;

import java.util.Date;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.profile.domain.Ticket;
import org.craftercms.profile.repositories.TicketRepository;
import org.craftercms.profile.security.PersistentTenantRememberMeToken;
import org.craftercms.profile.services.impl.PersistentTicketServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
/// TODO FIX THIS !!!
@RunWith(MockitoJUnitRunner.class)
public class PersistentTicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private PersistentTicketServiceImpl persistentTicketService;

    private String seriesId = "Gc0fnMy79LYbboju4Nf5g";

    private Ticket ticket;

    @Before
    public void startup() {
        ticket = getTicket();
        try {
            when(ticketRepository.findBySeries(seriesId)).thenReturn(ticket);
        } catch (MongoDataException e) {

        }

    }

    @Test
    public void testGetTokenForSeries() {
        PersistentRememberMeToken t = persistentTicketService.getTokenForSeries(seriesId);
        assertNotNull(t);
    }

    @Test
    public void testRemoveUserTokens() {
        persistentTicketService.removeUserTokens("test");
        Mockito.verify(ticketRepository).removeUserTickets("test");

    }

    @Test
    public void testCreateNewTokens() {
        PersistentTenantRememberMeToken token = new PersistentTenantRememberMeToken("test", seriesId,
            "hH4FxVA+LWzimU9mtQsJJw==", new Date(), "test");
        persistentTicketService.createNewToken(token);
        try {
            Mockito.verify(ticketRepository).save(Mockito.<Ticket>any());
        } catch (MongoDataException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpdateTokens() {

        persistentTicketService.updateToken(this.seriesId, "LWzimU9mtQsJJw==", new Date());
        try {
            Mockito.verify(ticketRepository).save(Mockito.<Ticket>any());
        } catch (MongoDataException e) {
            e.printStackTrace();
        }

    }

    private Ticket getTicket() {
        Ticket ticket = new Ticket();
        ticket.setDate(new Date());
        ticket.setSeries(seriesId);
        ticket.setTenantName("test");
        ticket.setTokenValue("hH4FxVA+LWzimU9mtQsJJw==");
        ticket.setUsername("test");

        return ticket;
    }
}

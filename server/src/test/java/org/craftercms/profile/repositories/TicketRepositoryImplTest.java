package org.craftercms.profile.repositories;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.CommandResult;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.craftercms.commons.mongo.JongoQueries;
import org.craftercms.commons.mongo.MongoDataException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test of Ticket Repository.
 */
public class TicketRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(TicketRepositoryImplTest.class);
    private static final String MOCK_QUERIES = "{mock:true}";
    private TicketRepositoryImpl ticketRepository;
    private MongoCollection collection;

    @Before
    public void setUp() throws Exception {
        ticketRepository = new TicketRepositoryImpl();
        Jongo jongo = mock(Jongo.class);
        JongoQueries queries = mock(JongoQueries.class);
        collection = mock(MongoCollection.class);
        when(queries.get(Mockito.anyString())).thenReturn(MOCK_QUERIES);
        when(jongo.getCollection(Mockito.anyString())).thenReturn(collection);
        ticketRepository.setJongo(jongo);
        ticketRepository.setQueries(queries);
    }

    @Test
    public void testRemoveUserTickets() throws Exception {
        WriteResult writeResult = createWriteResult(false);
        when(collection.remove(Mockito.anyString(), Mockito.anyVararg())).thenReturn(writeResult);
        ticketRepository.removeUserTickets("testUser");
    }

    @Test(expected = MongoDataException.class)
    public void testRemoveUserTicketsError() throws Exception {
        WriteResult writeResult = createWriteResult(true);
        when(collection.remove(Mockito.anyString(), Mockito.anyVararg())).thenReturn(writeResult);
        ticketRepository.removeUserTickets("testUser");
    }

    @Test
    public void testRemoveTicketsOlderThan() throws Exception {
        final WriteResult writeResult = createWriteResult(false);
        // Create a Date from now - 1 year
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        //Adds to a list 2 dates, Now-1 year and Now
        final List<Date> dateList = Arrays.asList(new Date(cal.getTimeInMillis()), new Date());
        // Most of the test is here, check that given date by the removeOlderThan is
        // correct and can "delete" older.
        when(collection.remove(Mockito.anyString(), Mockito.anyVararg())).then(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                // Get the date param send by Repository to Jongo
                // This is the date calculated by removeOlderThan
                final Date d = (Date)invocation.getArguments()[1];
                // Simple Filter the original list to get only dates After the calculated by the method.
                List<Date> undeletedDates = ListUtils.select(dateList, new Predicate<Date>() {
                    @Override
                    public boolean evaluate(final Date object) {
                        return object.after(d);
                    }
                });
                Assert.assertEquals(1, undeletedDates.size());
                return writeResult;
            }
        });
        ticketRepository.removeTicketsOlderThan(5000);
    }

    @Test
    public void testFindByTicket() throws Exception {

    }

    @Test
    public void testFindByUsername() throws Exception {

    }

    @Test
    public void testFindBySeries() throws Exception {

    }

    private WriteResult createWriteResult(final boolean isError) {
        CommandResult commandResult = mock(CommandResult.class);
        WriteResult result = mock(WriteResult.class);
        if (isError) {
            when(commandResult.ok()).thenReturn(false);
            when(commandResult.getErrorMessage()).thenReturn("Mock Error Message");
            when(commandResult.getException()).thenReturn(new MongoException("Mock Error Message"));
        } else {
            when(commandResult.ok()).thenReturn(true);
        }
        when(result.getLastError()).thenReturn(commandResult);
        return result;
    }
}

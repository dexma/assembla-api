package test.com.assembla;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;

import com.assembla.CustomReport;
import com.assembla.Document;
import com.assembla.Tag;
import com.assembla.Ticket;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaResponse;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.exception.AssemblaAPIException;
import com.assembla.service.TicketService;
import com.assembla.service.TicketService.TicketReport;
import com.assembla.service.TicketService.TicketStatusFilter;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest extends ServiceTest {
	
	private TicketService ticketService;
	
	@Before
	public void setup() {
		ticketService = new TicketService(assemblaClient, TEST_SPACE_ID);
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new Ticket(), Ticket.class));
		when(assemblaClient.doPut(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new Ticket(), Ticket.class));
	}
	
	@Test
	public void getTicketByIdTest() {
		//Given a request to get a ticket by Id 123
		ticketService.getTicketById("123");
		//When we make a request then it's equal to this request
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/id/123.json", Ticket.class);
		verify(assemblaClient).doGet(request);
	}
	
	@Test(expected=AssemblaAPIException.class)
	public void getTicketByIdNotFoundTest() {
		//Given a request to get a ticket by id 123, when this ticket does not exist, then throw an exception
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(Ticket.class));
		ticketService.getTicketById("123");
	}

	@Test
	public void getTicketByNumberTest() {
		//Given a request to get ticket by number 123
		ticketService.getTicketByNumber(123);
		//When we make a request then it's equal to this request
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/123.json", Ticket.class);
		verify(assemblaClient).doGet(request);
	}
	
	@Test(expected=AssemblaAPIException.class)
	public void getTicketByNumberNotFoundTest() {
		//Given a request to get a ticket by number 123, when this ticket does not exist, then throw an exception
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(Ticket.class));
		ticketService.getTicketByNumber(123);
	}
	
	@Test
	public void getTicketsNoMilestoneTest() {
		//Given a request to get all tickets without a mile stone , when we look at the iterator returned
		PagedIterator<Ticket> tickets = ticketService.getTicketsByNoMilestone();
		// Then we expect it to wrap a paged request equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets/no_milestone.json", Ticket[].class);
		assertEquals(request, tickets.getRequest());
	}
	
	@Testgit
	public void getTicketByMilestoneTest() {
		//Given a request to get all tickets without a mile stone , when we look at the iterator returned
		PagedIterator<Ticket> tickets = ticketService.getTicketsByMilestone("123");
		// Then we expect it to wrap a paged request equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets/milestone/123.json", Ticket[].class);
		assertEquals(request, tickets.getRequest());
	}
	
	@Test
	public void getTicketByMilestoneWithTicketFilterTest() {
		//Given a request to get all tickets without a mile stone , when we look at the iterator returned
		PagedIterator<Ticket> tickets = ticketService.getTicketsByMilestone("123", TicketStatusFilter.ALL);
		// Then we expect it to wrap a paged request equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets/milestone/123.json", Ticket[].class);
		request.addParam(AssemblaConstants.TICKET_STATUS_PARAMETER, TicketStatusFilter.ALL.toString().toLowerCase());
		assertEquals(request, tickets.getRequest());
	}
	
	
	@Test
	public void getAllTicketsTest() {
		//Given a request to get all tickets
		PagedIterator<Ticket> tickets = ticketService.getAllTickets();
		//When we look at the request made on client, then we expect it to be equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets.json", Ticket[].class);
		assertEquals(request, tickets.getRequest());
	}	
	
	@Test
	public void getAllTicketsWithReportIdTest() {
		//Given a request to get all tickets
		PagedIterator<Ticket> tickets = ticketService.getAllTickets(TicketReport.ALL);
		//When we look at the request made on client, then we expect it to be equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets.json", Ticket[].class);
		request.addParam(AssemblaConstants.REPORT_PARAMETER, TicketReport.ALL.getReportId());
		assertEquals(request, tickets.getRequest());
	}	
	
	@Test
	public void getAllTicketsAssignedToUser() {
		//Given a request to get all tickets
		PagedIterator<Ticket> tickets = ticketService.getAllFollowedTickets();
		//When we look at the request made on client, then we expect it to be equal to this request
		PagedAssemblaRequest request = new PagedAssemblaRequest("/spaces/test_space_id/tickets/my_followed.json", Ticket[].class);
		assertEquals(request, tickets.getRequest());
	}	
	
	@Test
	public void getAllActiveTickets() {
		//Given a request to get all user's active tickets
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new Ticket[10], Ticket[].class));
		List<Ticket> tickets = ticketService.getAllActiveTickets();
		//When we look at the request that's made it should be equal to this request
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/my_active.json", Ticket[].class	);
		
		verify(assemblaClient).doGet(request);
		assertEquals("Size of list returned is equal to size of value wrapped in reponse" ,10, tickets.size());
	}
	
	@Test
	public void getAllTagsForTicket() {
		//Given a request to get tags for ticket 100
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new Tag[10], Tag[].class));
		List<Tag> tags = ticketService.getAllTagsForTicket(100);
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/100/tags.json", Tag[].class);
		//When we make the request it's equal to this request
		verify(assemblaClient).doGet(request);
		assertEquals("Size of List returned is size of array wrapped in response", 10, tags.size());
	}
	
	@Test
	public void getDocumentsForTicketTest() {
		//Given request to get attachments for a ticket 100
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new Document[10], Document[].class));
		List<Document> documents = ticketService.getAttachmentsForTicket(100);
		//When we make the request then it should be equal to this request and we should return a list of Documents
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/100/attachments.json", Document[].class);
		verify(assemblaClient).doGet(request);
		assertEquals("Size of List returned is size of array wrapped in the response", 10, documents.size());
	}
	
	@Test
	public void updateExistingTicketTest() {
		//Given a ticket with number 10
		Ticket ticket = new Ticket();
		ticket.setNumber(10);
		//When we set status to new and call update method 
		ticket.setStatus("New");
		//Then we expect a request to be made which is equal to this request  and return the updated ticket
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/10.json");
		request.withBody(ticket);
		
		ticketService.updateTicket(ticket);
		verify(assemblaClient).doPut(request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateExistingTicketBadArgs() {
		//Given a request to update ticket without a number
		ticketService.updateTicket(new Ticket());
	}
	
	@Test
	public void getCustomReportTest() {
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new CustomReport(), CustomReport.class));
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/custom_reports.json", CustomReport.class);
		CustomReport report = ticketService.getCustomReports();
		verify(assemblaClient).doGet(request);
	}
	
	@Test
	public void deleteTicketTest() {
		//Given ticket 100, when we delete this ticket, then expect to see a request like this created
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse());
		Ticket ticket = new Ticket();
		ticket.setNumber(100);
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/100.json");
		ticketService.deleteTicket(ticket);
		//MAKE TEST FOR EQUALS HASH ETC
		verify(assemblaClient).doDelete(request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deleteTicketNull() {
		ticketService.deleteTicket(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void deleteTicketNoNumber() {
		ticketService.deleteTicket(new Ticket());
	}
	
	@Test
	public void createNewTicketTest()	{
		Ticket expectedValue = new Ticket();
		when(assemblaClient.doPost(Matchers.any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(expectedValue, Ticket.class));
		
		Ticket ticketToCreate = new Ticket();;
		ticketToCreate.setDescription("Test ticket body");
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets.json", Ticket.class);
		request.withBody(ticketToCreate);
		
		Ticket createdTicket = ticketService.createTicket(ticketToCreate);
		verify(assemblaClient).doPost(request);
		assertNotNull("Created ticket is null", createdTicket);
		assertEquals("Ticket not the same as one returned", expectedValue, createdTicket);
	}
	
	@Test
	public void createTicketNotCreated() {
		when(assemblaClient.doPost(Matchers.any(AssemblaRequest.class))).thenThrow(new AssemblaAPIException("Error making request"));
		Ticket ticket = ticketService.createTicket(new Ticket());
		assertNull("Ticket should be null", ticket);
	}
	
	
}

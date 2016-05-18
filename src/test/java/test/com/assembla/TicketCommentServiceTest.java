package test.com.assembla;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assembla.client.AssemblaConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;

import com.assembla.Ticket;
import com.assembla.TicketComment;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaResponse;
import com.assembla.client.PagedIterator;
import com.assembla.exception.AssemblaAPIException;
import com.assembla.service.TicketCommentService;

@RunWith(MockitoJUnitRunner.class)
public class TicketCommentServiceTest extends ServiceTest {

	private TicketCommentService ticketCommentService;

	@Before
	public void setup() {
		ticketCommentService = new TicketCommentService(assemblaClient, TEST_SPACE_ID);
	}
	
	@Test
	public void getTicketCommentsTest() {
		when(assemblaClient.doGet(any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new TicketComment[10], TicketComment[].class));
		Ticket ticket = new Ticket();
		ticket.setNumber(100);
		PagedIterator<TicketComment> it =ticketCommentService.getTicketComments(ticket);
		assertEquals("Request.getFullURI is incorrect","/spaces/test_space_id/tickets/100/ticket_comments"
				+ ".json?page="+AssemblaConstants.DEFAULT_PAGE+"&per_page="+ AssemblaConstants.DEFAULT_PAGE_SIZE,it
				.getRequest().getFullURI());
		assertNotNull("PagedIterator == null", it);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getTicketCommentsNullTicket() {
		ticketCommentService.getTicketComments(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getTicketCommentsNoNumber() {
		ticketCommentService.getTicketComments(new Ticket());
	}
	
	@Test
	public void getTicketCommentTest() {
		when(assemblaClient.doGet(any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(new TicketComment() ,TicketComment.class));
		int id = 200;
		Ticket ticket = new Ticket();
		ticket.setNumber(1000);
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/1000/ticket_comments/200.json", TicketComment.class	);
		TicketComment tc = ticketCommentService.getTicketComment(ticket, id);
		verify(assemblaClient).doGet(request);
	}
	
	@Test(expected=AssemblaAPIException.class)
	public void getTicketNoCommentExistsWithId() {
		when(assemblaClient.doGet(any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(TicketComment.class));
		int id = 200;
		Ticket ticket = new Ticket();
		ticket.setNumber(1000);
		TicketComment tc = ticketCommentService.getTicketComment(ticket, id);
	}
	
	@Test
	public void createTicketCommentTest() {
		TicketComment value = new TicketComment();
		when(assemblaClient.doPost(any(AssemblaRequest.class))).thenReturn(new AssemblaResponse(value ,TicketComment.class));
		
		Ticket ticket = new Ticket();
		ticket.setNumber(100);
		
		TicketComment newTicketComment = value;
		newTicketComment.setComment("Test comment");
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/100/ticket_comments.json", TicketComment.class);
		request.withBody(newTicketComment);
		
		TicketComment comment = ticketCommentService.createTicketComment(ticket, newTicketComment);
		
		verify(assemblaClient).doPost(request);
		assertNotNull("Comment should not be null", comment);
		assertEquals("Comment should equal one returned from client", value, comment);
	}
	
	@Test
	public void createTicketCommentTestNotCreated() {
		when(assemblaClient.doPost(Matchers.any(AssemblaRequest.class))).thenThrow(new AssemblaAPIException("Error making request"));
		TicketComment ticketComment = ticketCommentService.createTicketComment(new Ticket(), new TicketComment());
		assertNull("Ticket comment not created, so should be null", ticketComment);
	}
	
	@Test
	public void updateTicektCommentTest() {
		when(assemblaClient.doPut(any(AssemblaRequest.class))).thenReturn(new AssemblaResponse());
		TicketComment ticketComment = new TicketComment();
		ticketComment.setId(200);
		ticketComment.setComment("New comment");
		Ticket ticket = new Ticket();
		ticket.setNumber(1000);

		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/tickets/1000/ticket_comments/200.json", null	);
		request.withBody(ticketComment);
		
		ticketCommentService.updateTicketComment(ticket, ticketComment);
		verify(assemblaClient).doPut(request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void updateTicketCommentNullTicketTest() {
		ticketCommentService.updateTicketComment(null, new TicketComment());
	}

	@Test(expected=IllegalArgumentException.class)
	public void updateTicketCommentNullTicketCommentTest() {
		Ticket ticket = new Ticket();
		ticket.setNumber(100);
		ticketCommentService.updateTicketComment(ticket, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void ticketCommentNoId() {
		Ticket ticket = new Ticket();
		ticket.setNumber(100);
		TicketComment comment = new TicketComment();
		ticketCommentService.updateTicketComment(ticket , comment);
	}


}

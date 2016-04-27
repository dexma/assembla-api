package com.assembla.service;
import static java.lang.String.format;

import java.util.List;

import com.assembla.CustomReport;
import com.assembla.Document;
import com.assembla.Tag;
import com.assembla.Ticket;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;

public class TicketService extends AbstractBaseService {

	public TicketService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	public Ticket getTicketById(String id) {
		String uri = format(AssemblaConstants.TICKET_BY_ID, super.getSpaceId(), id);
		AssemblaRequest request = new AssemblaRequest(uri, Ticket.class);
		return super.get(request, format("No ticket with id %s exists", id));
	}

	public Ticket getTicketByNumber(int number) {
		String uri = format(AssemblaConstants.TICKET_BY_NUMBER, super.getSpaceId(), number);
		AssemblaRequest request = new AssemblaRequest(uri, Ticket.class);
		return super.get(request, format("No ticket with id %d exists", number));
	}

	public PagedIterator<Ticket> getTicketsByNoMilestone() {
		String uri = format(AssemblaConstants.TICKETS_BY_NO_MILESTONE, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Ticket[].class);
		return new PagedIterator<>(request, client);
	}

	public PagedIterator<Ticket> getAllTickets() {
		String uri = format(AssemblaConstants.TICKETS_BY_SPACE, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Ticket[].class);
		return new PagedIterator<>(request, client);
	}

	public PagedIterator<Ticket> getAllFollowedTickets() {
		String uri = format(AssemblaConstants.TICKETS_FOLLOWED, super.getSpaceId());
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, Ticket[].class);
		return new PagedIterator<>(request, client);
	}

	public List<Ticket> getAllActiveTickets() {
		String uri = format(AssemblaConstants.TICKETS_BY_STATUS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, Ticket[].class);
		return super.getList(request);
	}

	public List<Tag> getAllTagsForTicket(int ticketNumber) {
		String uri = format(AssemblaConstants.TAGS_FOR_TICKET, super.getSpaceId(), ticketNumber	);
		AssemblaRequest request = new AssemblaRequest(uri, Tag[].class);
		return super.getList(request);
	}

	public List<Document> getAttachmentsForTicket(int ticketNumber) {
		String uri = format(AssemblaConstants.ATTACHMENTS_FOR_TICKET, super.getSpaceId(), ticketNumber);
		AssemblaRequest request = new AssemblaRequest(uri, Document[].class);
		return super.getList(request);
	}

	public void updateTicket(Ticket ticket) {
		if(ticket == null || ticket.getNumber() == null) {
			throw new IllegalArgumentException("Ticket must not be null and must have a valid number");
		}
		String uri = format(AssemblaConstants.TICKET_BY_NUMBER, super.getSpaceId(), ticket.getNumber());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(ticket);
		client.doPut(request);
	}

	public CustomReport getCustomReports() {
		String uri = format(AssemblaConstants.TICKET_CUSTOM_REPORTS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, CustomReport.class);
		return super.get(request, "Error getting custom reports");
	}

	public void deleteTicket(Ticket ticket) {
		if(ticket == null || ticket.getNumber() == null) {
			throw new IllegalArgumentException("Ticket must not be null and have a number");
		}
		String uri = format(AssemblaConstants.TICKET_DELETE, super.getSpaceId(), ticket.getNumber());
		AssemblaRequest request = new AssemblaRequest(uri);
		client.doDelete(request);
	}

	public Ticket createTicket(Ticket ticket) {
		String uri = format(AssemblaConstants.TICKETS_BY_SPACE, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, Ticket.class);
		request.withBody(ticket);
		return super.post(request);
	}
	
}
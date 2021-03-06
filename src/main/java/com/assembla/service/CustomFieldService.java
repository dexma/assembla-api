package com.assembla.service;

import static java.lang.String.format;

import java.util.List;

import com.assembla.CustomField;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ObjectUtils;

public class CustomFieldService extends AbstractBaseService {

	public CustomFieldService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	public List<CustomField> getAll() {
		String uri = format(AssemblaConstants.CUSTOM_FIELDS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, CustomField[].class);
		return super.getList(request);
	}

	public CustomField get(int id) {
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), id);
		AssemblaRequest request = new AssemblaRequest(uri, CustomField.class);
		return super.get(request, format("No custom field with id : %d exists", id));
	}

	public CustomField create(CustomField customField) {
		ObjectUtils.notNull(customField, "customerField == null");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS, super.getSpaceId());
		AssemblaRequest request = new AssemblaRequest(uri, CustomField.class);
		request.withBody(customField);
		return super.post(request);
	}

	public void update(CustomField customField) {
		ObjectUtils.notNull(customField, "customerField == null");
		ObjectUtils.notNull(customField.getId(), "customField requires an id");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), customField.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(customField);
		client.doPut(request);
	}

	public void delete(CustomField customField) {
		ObjectUtils.notNull(customField, "customerField == null");
		ObjectUtils.notNull(customField.getId(), "customField requires an id");
		String uri = format(AssemblaConstants.CUSTOM_FIELDS_BY_ID, super.getSpaceId(), customField.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		client.doDelete(request);
	}

}

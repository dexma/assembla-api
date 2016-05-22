package com.assembla.service;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.StreamUtil;

public class DocumentService extends AbstractBaseService {

	public DocumentService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}
	
	public File getFile(String fileId) {
		String uri = format(AssemblaConstants.FILE_FOR_ATTACHMENT, super.getSpaceId(), fileId);
		AssemblaRequest request = new AssemblaRequest(uri, InputStream.class);
		InputStream stream = super.get(request, format("No document with id %s exists.", fileId));
		try {
			return StreamUtil.stream2file(stream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	} 

}

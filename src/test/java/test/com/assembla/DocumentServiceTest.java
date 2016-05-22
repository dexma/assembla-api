package test.com.assembla;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;

import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaResponse;
import com.assembla.service.DocumentService;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest extends ServiceTest {

	private DocumentService documentService;

	@Before
	public void setup() {
		documentService = new DocumentService(assemblaClient, TEST_SPACE_ID);
		when(assemblaClient.doGet(Matchers.any(AssemblaRequest.class)))
				.thenReturn(new AssemblaResponse(new ByteArrayInputStream("dummy".getBytes()), InputStream.class));
	}

	@Test
	public void getFileByDocumentIdTest() throws IOException {
		// Given a request to get document by id 123
		documentService.getFile("123");
		// When we make a request then it's equal to this request
		AssemblaRequest request = new AssemblaRequest("/spaces/test_space_id/documents/123/download",
				InputStream.class);
		verify(assemblaClient).doGet(request);
	}

}

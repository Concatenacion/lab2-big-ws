package translator.web.ws;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import translator.Application;
import translator.domain.Language;
import translator.web.ws.schema.GetTranslationRequest;
import translator.web.ws.schema.GetTranslationResponse;
import translator.web.ws.schema.GetTranslationWithLanguageDetectionRequest;
import translator.web.ws.schema.GetTranslationWithLanguageDetectionResponse;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT, classes = Application.class)
public class TranslatorEndpointTest {

	private Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

	@LocalServerPort
	private int port;

	@Before
	public void init() throws Exception {
		marshaller.setPackagesToScan(
				ClassUtils.getPackageName(GetTranslationRequest.class),
				ClassUtils.getPackageName(GetTranslationWithLanguageDetectionRequest.class));
		marshaller.afterPropertiesSet();
	}

	@Test
	public void testSendAndReceiveWithLanguageDetection() {
		GetTranslationWithLanguageDetectionRequest request = new GetTranslationWithLanguageDetectionRequest();
		request.setLangFrom(Language.ENGLISH.asStr());
		request.setText("En un lugar de la Mancha, de cuyo nombre no quiero acordarme...");
		Object response = new WebServiceTemplate(marshaller).marshalSendAndReceive("http://localhost:"
				+ port + "/ws", request);
		assertNotNull(response);
		assertThat(response, instanceOf(GetTranslationWithLanguageDetectionResponse.class));
		GetTranslationWithLanguageDetectionResponse translation = (GetTranslationWithLanguageDetectionResponse) response;
assertThat(translation.getTranslation(), is("In a place in La Mancha, whose name I do not want to remember ..."));
	}

}

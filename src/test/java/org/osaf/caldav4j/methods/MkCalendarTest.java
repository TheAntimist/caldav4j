package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.functional.support.CalDavFixture;
import org.osaf.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author rpolli
 *
 * TODO This whole class can be fixturized and shortened. The only reason not to do so
 * 				is to keep things simple when testing bare methods.
 */
@Ignore
public class MkCalendarTest extends BaseTestCase {

	private static final Logger log = LoggerFactory.getLogger(MkCalendarTest.class);

	private List<String> addedItems = new ArrayList<String>();
	@Override
    @Before
	//skip collection creation while initializing
	public void setUp() throws Exception {
		fixture = new CalDavFixture();
		fixture.setUp(caldavCredential, caldavDialect, true);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		log.debug("Removing base collection created during this test.");    	
		for (String p : addedItems) {
			fixture.delete(p, false);			
		}
	}

	/**
	 * this should return something like
	 * @see http://tools.ietf.org/html/rfc4791#section-5.3.1.2
	 */
	@Test
	public void testPrintMkCalendar() throws UnsupportedEncodingException {
		MkCalendarMethod mk = new MkCalendarMethod(caldavCredential.home + caldavCredential.collection);

		mk.addDisplayName("My display Name");
		mk.addDescription("this is my default calendar", "en");
		mk.addDescription("this is my default calendar");

		//generateRequestBody, returns a byte array.
		log.info(new String(mk.generateRequestBody(), "UTF-8"));

	}

	@Test
	@Ignore
	public void testCreateSubCollection() throws Exception {
		String collectionPath = fixture.getCollectionPath();
		addedItems.add("root1/");

		MkColMethod mk = new MkColMethod(collectionPath + "root1/");
		fixture.executeMethod(CaldavStatus.SC_CREATED, mk, true, null, true);

		mk.setPath(collectionPath + "root1/sub/");
		fixture.executeMethod(CaldavStatus.SC_CREATED, mk, false, null, true );
	}

	@Test
	public void testCreateRemoveCalendarCollection() throws Exception{
		String collectionPath = caldavCredential.home + caldavCredential.collection;

		MkCalendarMethod mk = new MkCalendarMethod(collectionPath);
		//mk.setPath(collectionPath);
		mk.addDisplayName("My display Name");
		mk.addDescription("this is my default calendar", "en");

		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();
		http.executeMethod(hostConfig, mk);

		int statusCode = mk.getStatusCode();        
		// whatever successful status code the caldav server returns,
		//   the base collection is created, and should be removed.
		//   TODO CaldavFixture may handle it automagically
		if ((statusCode < 300) && (statusCode >=200)) {
			addedItems.add("");			
		}
		// if resource already exists, remove it on teardown

		switch (statusCode) {
		case CaldavStatus.SC_METHOD_NOT_ALLOWED:
		case CaldavStatus.SC_FORBIDDEN:
			addedItems.add("");			
			break;
		default:
			break;
		}

		/// Test if the caldav server return the right status code
		assertEquals("Status code for mk:", CaldavStatus.SC_CREATED, statusCode);

		//now let's try and get it, make sure it's there
		GetMethod get = fixture.getMethodFactory().createGetMethod();
		get.setPath(collectionPath);
		http.executeMethod(hostConfig, get);
		statusCode = get.getStatusCode();
		assertEquals("Status code for get:", CaldavStatus.SC_OK, statusCode);


		DeleteMethod delete = new DeleteMethod(collectionPath);

		http.executeMethod(hostConfig, delete);

		statusCode = delete.getStatusCode();
		assertEquals("Status code for delete:", CaldavStatus.SC_NO_CONTENT, statusCode);
		addedItems.remove("");

		//Now make sure that it goes away
		get = fixture.getMethodFactory().createGetMethod();
		get.setPath( collectionPath);
		http.executeMethod(hostConfig, get);
		statusCode = get.getStatusCode();
		assertEquals("Status code for get:", CaldavStatus.SC_NOT_FOUND, statusCode);


	}
}
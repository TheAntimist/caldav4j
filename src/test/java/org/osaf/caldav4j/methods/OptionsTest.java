package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore // run thru functional OptionITCase
public class OptionsTest extends BaseTestCase {

	public static final String OUTBOX = "/Outbox/";
	public static final String INBOX = "/Inbox/";
    // private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

    @Before
    @Override
    //do not need the initialization in the base class
    public void setUp() throws Exception {

    }

	/**
	   >> Request <<

	   OPTIONS /lisa/calendar/outbox/ HTTP/1.1
	   Host: cal.example.com

	   >> Response <<

	   HTTP/1.1 204 No Content
	   Date: Thu, 31 Mar 2005 09:00:00 GMT
	   Allow: OPTIONS, GET, HEAD, POST, DELETE, TRACE,
	   Allow: PROPFIND, PROPPATCH, LOCK, UNLOCK, REPORT, ACL
	   DAV: 1, 2, 3, access-control
	   DAV: calendar-access, calendar-auto-schedule
	   */
	@Test
	public void testOptions() {
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        
        for (String s : new String[] {INBOX, OUTBOX} ) {
        	
	
	        OptionsMethod options = new OptionsMethod(caldavCredential.home + s);

	        try {
				http.executeMethod(hostConfig,options);
				if (options.succeeded()) {
					log.info(options.getResponseHeader("Allow").toString());
					for (Header h : options.getResponseHeaders("DAV")) {
						if (h != null) {
							 if (h.getValue().contains("calendar-access")) { 
								 log.info(h.toString());
							 } else if (h.getValue().contains("calendar-schedule") || h.getValue().contains("calendar-auto-schedule")) {
								 log.info(h.toString());
							 } else {
								 assertTrue(false);
							 }
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail(e.getMessage());
			} 
        }
        
	}
	
}

package org.osaf.caldav4j.util;

import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.Principal;


/**
 * Ace methods for easy Principal settings
 * @author rpolli
 *
 */
public class AceUtils {

	/**
	 * Retrieve a Caldav Principal from a jackrabbit-webdav Ace.
	 * If  ace.getprincipal is set to "property", it returns directly the underlying property
	 * @param ace
	 * @return
	 */
	public static Principal getDavPrincipal(AclProperty.Ace ace) {

		return ace.getPrincipal();
	}
}

/*
 * Copyright 2006 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package org.osaf.caldav4j.model.response;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.CaldavStatus;

import java.io.StringReader;

/**
 * 
 * @author pventura_at_babel.it changed getCalendar
 *
 */
public class CalendarDataProperty {

	public static final String ELEMENT_CALENDAR_DATA = "calendar-data";

    private static ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>(){
        @Override
        protected CalendarBuilder initialValue(){
            return new CalendarBuilder();
        }
    };

    public static Calendar getCalendarfromProperty(DavProperty property) {
        if (property == null) return null;

        Calendar calendar = null;
        String text = property.getValue().toString();

        //text might contain lines breaked only with \n. RFC states that long lines must be delimited by CRLF.
        //@see{http://www.apps.ietf.org/rfc/rfc2445.html#sec-4.1 }
        //this fix the problem occurred when lines are breaked only with \n
        text = text.replaceAll("\n", "\r\n").replaceAll("\r\r\n", "\r\n");

        CalendarBuilder calendarBuilder = calendarBuilderThreadLocal.get();

        StringReader stringReader = new StringReader(text);
        try {
            calendar = calendarBuilder.build(stringReader);
        } catch (ParserException e) {
          try {
              CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
              CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
              CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);

              calendar = calendarBuilder.build(stringReader);
          } catch (Exception e2){
              e2.printStackTrace();
          }

        } catch (Exception e) {
            e.printStackTrace();
        }
        calendarBuilderThreadLocal.remove();
        return calendar;
    }

    public static Calendar getCalendarfromResponse(MultiStatusResponse response){
        return getCalendarfromProperty(response.getProperties(CaldavStatus.SC_OK).get(CalDAVConstants.DNAME_CALENDAR_DATA));
    }


    public static String getEtagfromResponse(MultiStatusResponse response){
        return getEtagfromProperty(response.getProperties(CaldavStatus.SC_OK).get(DavPropertyName.GETETAG));
    }

    public static String getEtagfromProperty(DavProperty property){
        if(property == null || !property.getName().equals(DavPropertyName.GETETAG)) return null;
        return property.getValue().toString();
    }
}

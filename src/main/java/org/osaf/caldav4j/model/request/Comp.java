/*
 * Copyright 2005 Open Source Applications Foundation
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
 */

package org.osaf.caldav4j.model.request;

import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.*;

/**
 *  <!ELEMENT comp ((allcomp, (allprop | prop*)) |
 *                  (comp*, (allprop | prop*)))>
 *
 * <!ATTLIST comp name CDATA #REQUIRED>
 * 
 * <!ELEMENT allcomp EMPTY> 
 * 
 * <!ELEMENT allprop EMPTY>
 * 
 * <!ELEMENT prop EMPTY>
 * 
 * <!ATTLIST prop name CDATA #REQUIRED
 *                novalue (yes|no) "no">
 *                
 * @author bobbyrullo
 * 
 */
public class Comp extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "comp";
    public static final String ELEM_ALLPROP = "allprop";
    public static final String ELEM_PROP = "prop";
    public static final String ELEM_ALLCOMP = "allcomp";
   
    public static final String ATTR_NAME = "name";

    private List<Comp> comps = new ArrayList<Comp>();
    private List<CalDAVProp> props = new ArrayList<CalDAVProp>();
    private boolean allComp = false;
    private boolean allProp = false;
    private String name = null;


    public Comp(String name, boolean allComp,
                boolean allProp, List<Comp> comps, List<CalDAVProp> props) {

        this.name = name;

        if (allComp){
            this.allComp = true;
        } else if(comps != null) {
            this.comps.addAll(comps);
        }

        if (allProp){
            this.allProp = true;
        } else if (props != null){
            this.props.addAll(props);
        }
    }

    public Comp(String name, List<Comp> comps, List<CalDAVProp> props){
        this(name, false, false, comps, props);
    }

    public Comp(String name, boolean allComp, boolean allProp) {
        this.name = name;
        this.allComp = allComp;
        this.allProp = allProp;
    }

    public Comp() {

    }

    /*
    Compatibility Constructors
     */

    public Comp(String name){
        this(name, false, false);
    }

    public Comp(String caldavNamespaceQualifier, String name, boolean allComp,
                boolean allProp, List<Comp> comps, List<CalDAVProp> props) {
            this(name, allComp, allProp, comps, props);
    }

    public boolean isAllComp() {
        return allComp;
    }

    public void setAllComp(boolean allComp) {
        this.allComp = allComp;
    }

    public boolean isAllProp() {
        return allProp;
    }

    public void setAllProp(boolean allProp) {
        this.allProp = allProp;
    }

    public List<Comp> getComps() {
        return comps;
    }

    public void setComps(List<Comp> comps) {
        this.comps = comps;
    }

    public void addComp(Comp comp){ this.comps.add(comp); }

    public void addComp(String name) { this.comps.add(new Comp(name)); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CalDAVProp> getProps() {
        return props;
    }

    public void setProps(List<CalDAVProp> props) {
        this.props = props;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<? extends XmlSerializable> getChildren() {
        ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();
        if (allComp){
            children.add(new PropProperty(ELEM_ALLCOMP, CalDAVConstants.NAMESPACE_CALDAV));
        } else if (comps != null){
            children.addAll(comps);
        }

        if (allProp){
            children.add(new PropProperty(ELEM_ALLPROP, CalDAVConstants.NAMESPACE_CALDAV));
        } else if (props != null) {
            children.addAll(props);
        }

        return children;
    }
    
    protected String getTextContent() {
        return null;
    }
    
    protected Map<String, String> getAttributes() {
        Map<String, String> m = new HashMap<String, String>();
        m.put(ATTR_NAME, name);
        return m;
    }
    
    public void addProp(CalDAVProp prop){
        props.add(prop);
    }
    
    public void addProp(String propName, boolean novalue) {
        props.add(new CalDAVProp(propName, novalue));
    }
    
    public void addProp(String propName) {
        props.add(new CalDAVProp(propName));
    }
    
    /**
     *  <!ELEMENT comp ((allcomp, (allprop | prop*)) |
     *                  (comp*, (allprop | prop*)))>
     *
     * <!ATTLIST comp name CDATA #REQUIRED>
     * 
     * <!ELEMENT allcomp EMPTY> 
     * 
     * <!ELEMENT allprop EMPTY>
     * 
     * <!ELEMENT prop EMPTY>
     * 
     * <!ATTLIST prop name CDATA #REQUIRED
     *                novalue (yes|no) "no">
     * 
     */
    public void validate() throws DOMValidationException{
        if (name == null){
            throwValidationException("Name is a required property");
        }
        
        if (allComp && comps != null && comps.size() > 0 ){
            throwValidationException("allComp and comp* are mutually exclusive");
        }
        
        if (comps != null){
            validate(comps);
        }
        
        if (allProp && props != null && props.size() > 0){
            throwValidationException("allProp and prop* are mutually exclusive");
        }
        
        if (props != null){
            validate(props);
        }
        
        
    }
}

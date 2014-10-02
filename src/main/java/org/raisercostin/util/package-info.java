@XmlJavaTypeAdapters({ @XmlJavaTypeAdapter(type = DateTime.class, value = JodaDateTimeJaxbAdapter.class) })
package org.raisercostin.util;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.joda.time.DateTime;


/*
 * Copyright (c) 2010-2014 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.stardog.examples.functions;

import com.complexible.common.protocols.server.Server;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.Stardog;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.protocols.snarl.SNARLProtocolConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.TupleQueryResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public class TestRFilterFunction {
	private static Server SERVER = null;
	private static final String DB = "testRFilter";

	@BeforeClass
	public static void beforeClass() throws Exception {
		SERVER = Stardog.buildServer()
		                .bind(SNARLProtocolConstants.EMBEDDED_ADDRESS)
		                .start();

		final AdminConnection aConn = AdminConnectionConfiguration.toEmbeddedServer()
		                                                          .credentials("admin", "admin")
		                                                          .connect();

		try {
			if (aConn.list().contains(DB)) {
				aConn.drop(DB);
			}

			aConn.createMemory(DB);
		}
		finally {
			aConn.close();
		}
	}

	@AfterClass
	public static void afterClass() {
		if (SERVER != null) {
			SERVER.stop();
		}
	}

	@Test
	public void testRFilter() throws Exception {
		final Connection aConn = ConnectionConfiguration.to(DB)
		                                                .credentials("admin", "admin")
		                                                .connect();

		try {

			final String aQuery = "prefix stardog: <" + Namespaces.STARDOG + ">" +
			                      "select ?str where { filter(stardog:R(\"wilcox.test\") > 0) }";

			final TupleQueryResult aResult = aConn.select(aQuery).execute();
			try {
				assertTrue("Should have a result", aResult.hasNext());

				final String aValue = aResult.next().getValue("str").stringValue();

				assertEquals("This Sentence Does Not Use Title Case.", aValue);

				assertFalse("Should have no more results", aResult.hasNext());
			}
			finally {
				aResult.close();
			}
		}
		finally {
			aConn.close();
		}
	}
}

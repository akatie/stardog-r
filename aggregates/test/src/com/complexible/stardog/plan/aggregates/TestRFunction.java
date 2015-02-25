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

package com.complexible.stardog.plan.aggregates;

import java.io.File;

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
import org.openrdf.query.BindingSet;
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
public class TestRFunction {
	private static Server SERVER = null;

	private static final String DB = "testRAggregates";

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
			aConn.memory(DB).create(new File("/home/amp/src/linked-edit-rules/data/people.ttl"));			
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
	public void TestMean() throws Exception {
		final Connection aConn = ConnectionConfiguration.to(DB)
		                                                .credentials("admin", "admin")
		                                                .connect();

		try {

			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
									"PREFIX agg: <urn:aggregate> " +
									"SELECT (agg:stardog:mean(?o) AS ?c) " +
//									"SELECT * " +	
									"WHERE { ?s leri:height ?o } ";
			System.out.println("Executing query: " + aQuery);

			final TupleQueryResult aResult = aConn.select(aQuery).execute();
			try {
//				assertTrue("Should have a result", aResult.hasNext());
				System.out.println("Query result:");
				while (aResult.hasNext()) {
					System.out.println(aResult.next().getValue("c").stringValue());
				}
				

//				final String aValue = aResult.next().getValue("s").stringValue();		
//				
//				assertEquals("0D", aValue);
			}
			finally {
				aResult.close();
			}
		}
		finally {
			aConn.close();
		}
	}
	
	@Test
	public void TestMedian() throws Exception {
		final Connection aConn = ConnectionConfiguration.to(DB)
		                                                .credentials("admin", "admin")
		                                                .connect();

		try {

			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
									"PREFIX agg: <urn:aggregate> " +
									"SELECT (agg:stardog:median(?o) AS ?c) " +
//									"SELECT * " +	
									"WHERE { ?s leri:height ?o } ";
			System.out.println("Executing query: " + aQuery);

			final TupleQueryResult aResult = aConn.select(aQuery).execute();
			try {
//				assertTrue("Should have a result", aResult.hasNext());
				System.out.println("Query result:");
				while (aResult.hasNext()) {
					System.out.println(aResult.next().getValue("c").stringValue());
				}
				

//				final String aValue = aResult.next().getValue("s").stringValue();		
//				
//				assertEquals("0D", aValue);
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

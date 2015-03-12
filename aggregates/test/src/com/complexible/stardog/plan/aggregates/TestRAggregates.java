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

/**
 * <p></p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   1.0
 * @version 1.0
 */
public class TestRAggregates {
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
									"WHERE { ?s leri:height ?o } ";
			System.out.println("Executing query: " + aQuery);

			final TupleQueryResult aResult = aConn.select(aQuery).execute();
			
			try {
				System.out.println("Query result:");
				while (aResult.hasNext()) {
					System.out.println(aResult.next().getValue("c").stringValue());
				}
			}
			finally {
				aResult.close();
			}
		}
		finally {
			aConn.close();
		}
	}
	
//	@Test
//	public void TestMedian() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
//									"PREFIX agg: <urn:aggregate> " +
//									"SELECT (agg:stardog:median(?o) AS ?c) " +
//									"WHERE { ?s leri:height ?o } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestMaxAggregate() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
//									"PREFIX agg: <urn:aggregate> " +
//									"SELECT (agg:stardog:max(?o) AS ?c) " +
//									"WHERE { ?s leri:height ?o } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestMinAggregate() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
//									"PREFIX agg: <urn:aggregate> " +
//									"SELECT (agg:stardog:min(?o) AS ?c) " +
//									"WHERE { ?s leri:height ?o } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestSd() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
//									"PREFIX agg: <urn:aggregate> " +
//									"SELECT (agg:stardog:sd(?o) AS ?c) " +
//									"WHERE { ?s leri:height ?o } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestVar() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +
//									"PREFIX agg: <urn:aggregate> " +
//									"SELECT (agg:stardog:var(?o) AS ?c) " +
//									"WHERE { ?s leri:height ?o } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestAbs() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:abs(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestSqrt() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:sqrt(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestCeiling() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:ceiling(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestFloor() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:floor(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestTrunc() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:trunc(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//
//	@Test
//	public void TestLog() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:log(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestLog10() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:log10(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestExp() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:exp(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestRound() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:round(stardog:sin(?o), 4) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestSignif() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:signif(stardog:sin(?o), 4) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestCos() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:cos(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestSin() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:sin(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
//	
//	@Test
//	public void TestTan() throws Exception {
//		final Connection aConn = ConnectionConfiguration.to(DB)
//		                                                .credentials("admin", "admin")
//		                                                .connect();
//		try {
//
//			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
//									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
//									"SELECT ?c " +
//									"WHERE { ?s leri:height ?o . BIND (stardog:tan(?o) AS ?c) } ";
//			System.out.println("Executing query: " + aQuery);
//
//			final TupleQueryResult aResult = aConn.select(aQuery).execute();
//			
//			try {
//				System.out.println("Query result:");
//				while (aResult.hasNext()) {
//					System.out.println(aResult.next().getValue("c").stringValue());
//				}
//			}
//			finally {
//				aResult.close();
//			}
//		}
//		finally {
//			aConn.close();
//		}
//	}
	
	@Test
	public void TestCov() throws Exception {
		final Connection aConn = ConnectionConfiguration.to(DB)
		                                                .credentials("admin", "admin")
		                                                .connect();
		try {

			final String aQuery = "PREFIX stardog: <tag:stardog:api:> " +
									"PREFIX sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#> " +
									"PREFIX leri: <http://lod.cedar-project.nl:8888/linked-edit-rules/resource/> " +			
									"SELECT (stardog:cov(?height, ?age) AS ?c) " +
									"WHERE { ?s leri:height ?height ; sdmx-dimension:age ?age } ";
			System.out.println("Executing query: " + aQuery);

			final TupleQueryResult aResult = aConn.select(aQuery).execute();
			
			try {
				System.out.println("Query result:");
				while (aResult.hasNext()) {
					System.out.println(aResult.next().getValue("c").stringValue());
				}
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

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

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.Function;
import com.complexible.stardog.StardogException;

import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.QueryEvaluationException;

import static com.complexible.common.rdf.model.Values.literal;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * <p>SameDistribution SPARQL custom function - runs wilcox.test in R</p>
 *
 * @author  Albert Meronyo-Penyuela
 * @since   1.0
 * @version 1.0
 *
 */
public final class R extends AbstractFunction {
    // slice samples
    private List<Double> sampleA = new ArrayList<Double>();
    private List<Double> sampleB = new ArrayList<Double>();

    // this function from a SPARQL query: `bind(stardog:titleCase(?var) as ?tc)`
    protected R() {
	super(4, Namespaces.STARDOG+"R");
    }

    @Override
    protected Value internalEvaluate(final Value... theArgs) {
	// URI for slice
	URI sliceURI = ValueFactoryImpl.getInstance()
	    .createURI("http://purl.org/linked-data/cube#Slice");

	// Clear samples
	sampleA = new ArrayList<Double>();
	sampleB = new ArrayList<Double>();

	// Using SNARL API to open connection to the db to run our own SPARQL queries
	Connection aConn = null;
	try {
	    aConn = ConnectionConfiguration
		.to("people")
		.credentials("admin", "admin")
		.server("snarl://localhost:5820")
		.connect();

	    // Check argument is a slice
	    SelectQuery aQuery = aConn.select("select ?type where { ?s ?p ?type }");
	    aQuery.parameter("s", theArgs[1]);
	    aQuery.parameter("type", sliceURI);
	    TupleQueryResult aResult = aQuery.execute();

	    try {
		System.out.println("Checking slice qb:Slice type...");
		QueryResultIO.write(aResult, TextTableQueryResultWriter.FORMAT, System.out);
		aResult.close();
	    } catch (IOException e) {
		System.err.println(e.getMessage());
	    } catch (TupleQueryResultHandlerException e) {
		System.err.println(e.getMessage());
	    } catch (QueryEvaluationException e) {
		System.err.println(e.getMessage());
	    }

	    // Go for dimension values of all observations of the slice
	    SelectQuery valQuery = aConn.select("select ?val where { ?s qb:observation ?obs . ?obs ?dim ?val . }");
	    valQuery.parameter("s", theArgs[1]);
	    valQuery.parameter("dim", theArgs[3]);
	    TupleQueryResult valResult = valQuery.execute();

	    try {
		while (valResult.hasNext()) {
		    sampleA.add(Double.parseDouble(valResult.next().getValue("val").stringValue()));
		}
		for (int i=0; i < sampleA.size(); i++) {
		    System.out.println(sampleA.get(i));
		}
	    } catch (QueryEvaluationException e) {
		System.err.println(e.getMessage());
	    }

	    valQuery.parameter("s", theArgs[2]);
	    valResult = valQuery.execute();
	    try {
		while (valResult.hasNext()) {
		    sampleB.add(Double.parseDouble(valResult.next().getValue("val").stringValue()));
		}
		for (int i=0; i < sampleB.size(); i++) {
		    System.out.println(sampleB.get(i));
		}
		valResult.close();
	    } catch (QueryEvaluationException e) {
		System.err.println(e.getMessage());
	    }

	    // Closing the SNARL connection
	    aConn.close();
	} catch (StardogException e) {
	    System.err.println(e.getMessage());
	}
	
	// If parameter is not a slice, return error
	
	// Start JRI R session
	Rengine re = Rengine.getMainEngine();
	if(re == null)
	    re = new Rengine(new String[] {"--vanilla"}, false, null);

	
	double[] aSampleA = new double[sampleA.size()];
	double[] aSampleB = new double[sampleB.size()];
	for (int i=0; i < sampleA.size(); i++) {
	    aSampleA[i] = sampleA.get(i);
	}
	for (int i=0; i < sampleB.size(); i++) {
	    aSampleB[i] = sampleB.get(i);
	}

	re.assign("x", aSampleA);
	re.assign("y", aSampleB);
	// Care for R injection!
	REXP result = re.eval("(" + theArgs[0] + "(x,y)$p.value)");
	System.out.println(result);
	re.end();

	return literal(result.asDouble());
    }

	@Override
	public Function copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(ExpressionVisitor arg0) {
		// TODO Auto-generated method stub
		
	}
}

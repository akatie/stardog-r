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

import com.complexible.common.base.Strings2;
import com.complexible.common.rdf.model.Namespaces;
import com.complexible.common.rdf.model.StardogValueFactory;
import com.complexible.common.rdf.query.resultio.TextTableQueryResultWriter;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.SelectQuery;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.FunctionEvaluationException;
import com.complexible.stardog.StardogException;

import org.openrdf.model.Value;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.QueryEvaluationException;

import static com.complexible.common.rdf.model.Values.literal;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

import java.util.Vector;
import java.io.IOException;

/**
 * <p>SameDistribution SPARQL custom function - runs wilcox.test in R</p>
 *
 * @author  Albert Meronyo-Penyuela
 * @since   1.0
 * @version 1.0
 *
 */
public final class SameDistribution extends AbstractFunction {
    // accumulation of bindings
    private Vector<Value> accBindings = new Vector<Value>();

    // this function from a SPARQL query: `bind(stardog:titleCase(?var) as ?tc)`
    protected SameDistribution() {
	super(1 /* takes a single argument */, Namespaces.STARDOG+"sameDistribution");
    }

    @Override
    protected Value internalEvaluate(final Value... theArgs) throws FunctionEvaluationException {
	// Using SNARL API to open connection to the db to run our own SPARQL queries
	Connection aConn = null;
	try {
	    aConn = ConnectionConfiguration
		.to("people")
		.credentials("admin", "admin")
		.connect();

	    SelectQuery aQuery = aConn.select("select * where { ?s ?p ?o }");
	    aQuery.limit(10);
	    TupleQueryResult aResult = aQuery.execute();

	    try {
		System.out.println("The first ten results...");
		QueryResultIO.write(aResult, TextTableQueryResultWriter.FORMAT, System.out);
		aResult.close();
	    } catch (IOException e) {
		System.err.println(e.getMessage());
	    } catch (TupleQueryResultHandlerException e) {
		System.err.println(e.getMessage());
	    } catch (QueryEvaluationException e) {
		System.err.println(e.getMessage());
	    }

	    aConn.close();
	} catch (StardogException e) {
	    System.err.println(e.getMessage());
	}
	
	// If parameter is not a slice, return error
	
	// Start JRI R session
	Rengine re = Rengine.getMainEngine();
	if(re == null)
	    re = new Rengine(new String[] {"--vanilla"}, false, null);

	re.assign("x", new double[] {1.5, 2.5, 3.5});
	REXP result = re.eval("(sum(x))");
	re.end();

	accBindings.add(theArgs[0]);

	System.out.println("== Starting accBindings dump ==");
	for (int i=0; i < accBindings.size(); i++)
	    System.out.println(accBindings.get(i));
	System.out.println("== Finishing accBindings dump ==");
	
	return literal(result.asDouble());
    }
}

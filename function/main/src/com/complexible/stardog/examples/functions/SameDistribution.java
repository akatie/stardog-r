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
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.FunctionEvaluationException;
import org.openrdf.model.Value;

import static com.complexible.common.rdf.model.Values.literal;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

/**
 * <p>SameDistribution SPARQL custom function - runs wilcox.test in R</p>
 *
 * @author  Albert Meronyo-Penyuela
 * @since   1.0
 * @version 1.0
 *
 */
public final class SameDistribution extends AbstractFunction {
    // this function from a SPARQL query: `bind(stardog:titleCase(?var) as ?tc)`
    protected SameDistribution() {
	super(1 /* takes a single argument */, Namespaces.STARDOG+"sameDistribution");
    }

    @Override
    protected Value internalEvaluate(final Value... theArgs) throws FunctionEvaluationException {
	// Start JRI R session
	Rengine re = Rengine.getMainEngine();
	if(re == null)
	    re = new Rengine(new String[] {"--vanilla"}, false, null);

	re.assign("x", new double[] {1.5, 2.5, 3.5});
	REXP result = re.eval("(sum(x))");
	re.end();
	
	// We know that we have a string, so let's just title case it and return it.
	return literal(result.asDouble());
    }
}

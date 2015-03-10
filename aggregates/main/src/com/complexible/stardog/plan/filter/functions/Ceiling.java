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

package com.complexible.stardog.plan.filter.functions;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.Function;
import com.complexible.stardog.plan.filter.functions.numeric.MathFunction;

import org.openrdf.model.Value;

import static com.complexible.common.rdf.model.Values.literal;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;

/**
 * <p>Ceiling function through R interface</p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 *
 */
public final class Ceiling extends AbstractFunction implements MathFunction {

    protected Ceiling() {
    	super(1, Namespaces.STARDOG + "ceiling");
    }
    
    private Ceiling(final Ceiling theExpr) {
    	super(theExpr);
    }

    @Override
    protected Value internalEvaluate(final Value... theArgs) {
		Rengine re = Rengine.getMainEngine();
		if(re == null)
		    re = new Rengine(new String[] {"--vanilla"}, false, null);
		
		double [] x = {Double.parseDouble(theArgs[0].stringValue())};
		re.assign("x", x);
		REXP result = re.eval("ceiling(x)");
		re.end();
	
		return literal(result.asDouble());
    }

	@Override
	public Function copy() {
		return new Ceiling(this);
	}

	@Override
	public void accept(ExpressionVisitor theVisitor) {
		theVisitor.visit(this);
		
	}

	@Override
	public String getOpString() {
		return "Ceiling";
	}
}
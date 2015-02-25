// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.util.ArrayList;
import java.util.List;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.rosuda.JRI.Rengine;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p></p>
 *
 * @author  Michael Grove, Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public final class Min extends AbstractAggregate {	
	private Rengine re = null;
	private List<Double> rCurr = null; 
	
	public Min() {		
		super(Namespaces.STARDOG + "min");
	}

	protected Min(final Min theAgg) {
		super(theAgg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		super.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		super.setArgs(theArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Value _getValue() throws ExpressionEvaluationException {
		if (rCurr == null) {
			return literal("0D");
		}
		else {
			re = Rengine.getMainEngine();
			if(re == null)
			    re = new Rengine(new String[] {"--vanilla"}, false, null);
			double[] y = new double[rCurr.size()];
			for (int i = 0; i < rCurr.size(); i++) {
				y[i] = rCurr.get(i);
			}
			re.assign("y", y);
			rCurr = null;
			return literal(re.eval("min(y)").asDouble());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aggregate(final Value theValue, final long theMultiplicity) throws ExpressionEvaluationException {
		if (!(theValue instanceof Literal)) {
            throw new ExpressionEvaluationException("Invalid argument to " + getName() + " argument MUST be a literal value, was: " + theValue);
		}
		if (rCurr == null) {
			rCurr = new ArrayList<Double>();
			rCurr.add(Double.parseDouble(theValue.stringValue()));
		} else {
			// Start JRI R session
			re = Rengine.getMainEngine();
			if(re == null)
			    re = new Rengine(new String[] {"--vanilla"}, false, null);
			rCurr.add(Double.parseDouble(theValue.stringValue()));			
			re.end();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Min copy() {
		return new Min(this);
	}
}

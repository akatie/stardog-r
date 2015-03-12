// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.rosuda.JRI.Rengine;

import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;

/**
 * <p></p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public abstract class RAggregate extends AbstractAggregate {
	protected Rengine re = null;
	protected List<Double> rCurr = null; 

	public RAggregate(String theName) {
		super(theName);
	}
	
	public RAggregate(RAggregate theAgg) {
		super(theAgg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(new String[] {"--vanilla"}, false, null);
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
		}
		rCurr.add(Double.parseDouble(theValue.stringValue()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		super.setArgs(theArgs);
	}

}

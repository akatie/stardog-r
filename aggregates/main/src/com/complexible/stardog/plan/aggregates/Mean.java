// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.rosuda.JRI.Rengine;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p>Mean custom aggregate through the R interface</p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public final class Mean extends RAggregate {	

	public Mean() {		
		super(Namespaces.STARDOG + "mean");
	}

	protected Mean(final Mean theAgg) {
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
	protected Value _getValue() throws ExpressionEvaluationException {
		if (rCurr == null) {
			return literal("0D");
		}
		else {
			re = Rengine.getMainEngine();
			if (re == null) {
				re = new Rengine(new String[] {"--vanilla"}, false, null);
			}
			double[] y = new double[rCurr.size()];
			for (int i = 0; i < rCurr.size(); i++) {
				y[i] = rCurr.get(i);
			}
			re.assign("y", y);
			rCurr = null;
			Literal result = literal(re.eval("mean(y)").asDouble());
			re.end();
			return result;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mean copy() {
		return new Mean(this);
	}
}

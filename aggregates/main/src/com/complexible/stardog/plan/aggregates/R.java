// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.util.List;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.functions.numeric.Multiply;
import com.complexible.stardog.plan.filter.functions.numeric.Root;
import com.google.common.base.Preconditions;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 3.0
 */
public final class R extends AbstractAggregate {
	private Root mRoot;
	private Multiply mProduct;
	private Count mCount;

	private Value mCurr = null;

	public R() {
		super(Namespaces.STARDOG + "R");
		System.out.println("Initializing aggregate class...");
	}

	protected R(final R theAgg) {
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
		return literal("0D");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aggregate(final Value theValue, final long theMultiplicity) throws ExpressionEvaluationException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public R copy() {
		return new R(this);
	}
}

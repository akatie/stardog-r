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
public final class GMean extends AbstractAggregate {
	private Root mRoot;
	private Multiply mProduct;

	private Count mCount;

	private Value mCurr = null;

	public GMean() {
		super(Namespaces.STARDOG + "gmean");

		mCount = new Count();
		mRoot = new Root();
		mProduct = new Multiply();
	}

	protected GMean(final GMean theAgg) {
		super(theAgg);

		mCount = new Count();
		mRoot = new Root();
		mProduct = new Multiply();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		super.initialize();

		mCount.initialize();
		mRoot.initialize();
		mProduct.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		Preconditions.checkArgument(theArgs.size() == 1, "Geometric mean aggregate function takes only one argument, %d found", theArgs.size());
		super.setArgs(theArgs);

		mCount.setArgs(theArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Value _getValue() throws ExpressionEvaluationException {
		if (mCurr == null) {
			return literal("0D");
		}
		else {
			return mRoot.evaluate(mCurr, mCount.get());
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

		mCount.aggregate(theValue, theMultiplicity);

		if (mCurr == null) {
			mCurr = theValue;
		}
		else {
			mCurr = mProduct.evaluate(theMultiplicity == 1
			                          ? theValue
			                          : mProduct.evaluate(theValue, literal(theMultiplicity)), mCurr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GMean copy() {
		return new GMean(this);
	}
}

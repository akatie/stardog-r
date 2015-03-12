// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.util.ArrayList;
import java.util.List;

import com.complexible.common.rdf.model.Namespaces;
import com.complexible.stardog.plan.SortType;
import com.complexible.stardog.plan.filter.AbstractExpression;
import com.complexible.stardog.plan.filter.Expression;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.ValueSolution;
import com.google.common.base.Preconditions;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.rosuda.JRI.Rengine;

import static com.complexible.common.rdf.model.Values.literal;

/**
 * <p>Covariance custom aggregate through the R interface</p>
 *
 * @author  Albert Meroño-Peñuela
 * @since   3.0
 * @version 3.0
 */
public final class Cov extends AbstractExpression implements Aggregate {	
	protected Rengine re = null;
	protected List<Double> rCurr = null; 
	
	public Cov() {
		super(Namespaces.STARDOG + "cov");
	}

	protected Cov(final Cov theAgg) {
		super(theAgg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArgs(final List<Expression> theArgs) {
		Preconditions.checkArgument(theArgs.size() == 2, "Covariance aggregate function takes two arguments, %d found", theArgs.size());
		System.out.println("I'M SETTING THE ARGS");
		for (Expression e : theArgs) {
			System.out.println(e.toString());
		}
		super.setArgs(theArgs);
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected Value _getValue() throws ExpressionEvaluationException {
//		if (rCurr == null) {
//			return literal("0D");
//		}
//		else {
//			re = Rengine.getMainEngine();
//			if (re == null) {
//				re = new Rengine(new String[] {"--vanilla"}, false, null);
//			}
//			double[] y = new double[rCurr.size()];
//			for (int i = 0; i < rCurr.size(); i++) {
//				y[i] = rCurr.get(i);
//			}
//			re.assign("y", y);
//			rCurr = null;
//			Literal result = literal(re.eval("mean(y)").asDouble());
//			re.end();
//			return result;
//		}
//	}
	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected void aggregate(final Value theValue, final long theMultiplicity) throws ExpressionEvaluationException {
//		System.out.println("theValue: " + theValue.stringValue());
//		System.out.println("theMultiplicity: " + theMultiplicity);
//		if (!(theValue instanceof Literal)) {
//            throw new ExpressionEvaluationException("Invalid argument to " + getName() + " argument MUST be a literal value, was: " + theValue);
//		}
//		if (rCurr == null) {
//			rCurr = new ArrayList<Double>();
//		}
//		rCurr.add(Double.parseDouble(theValue.stringValue()));
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cov copy() {
		return new Cov(this);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(ExpressionVisitor theVisitor) {
		theVisitor.visit(this);
	}

	@Override
	public Value evaluate(ValueSolution theSolution) throws ExpressionEvaluationException {
		System.out.println("Processing now: " + theSolution.toString());
		return theSolution.get(0);
	}

	@Override
	public Value get() throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDistinct() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDistinct(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sortType(SortType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(new String[] {"--vanilla"}, false, null);
		}
	}
}

// Copyright (c) 2010 - 2015 , Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.stardog.plan.aggregates;

import java.util.ArrayList;
import java.util.Arrays;
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
	protected List<Double> rCurr0 = null;
	protected List<Double> rCurr1 = null;
	
	public Cov() {
		super();
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
		super.setArgs(theArgs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cov copy() {
		return new Cov(this);
	}

	@Override
	public String getName() {
		return Namespaces.STARDOG + "cov";
	}

	@Override
	public List<String> getNames() {
		return new ArrayList<String>(Arrays.asList(getName()));
	}

	@Override
	public void accept(ExpressionVisitor theVisitor) {
		theVisitor.visit(this);
	}

	@Override
	public Value evaluate(ValueSolution theSolution) throws ExpressionEvaluationException {		
//		if (!(theValue instanceof Literal)) {
//            throw new ExpressionEvaluationException("Invalid argument to " + getName() + " argument MUST be a literal value, was: " + theValue);
//		}
		if (rCurr0 == null) {
			rCurr0 = new ArrayList<Double>();
		}
		if (rCurr1 == null) {
			rCurr1 = new ArrayList<Double>();
		}		
		rCurr0.add(Double.parseDouble(theSolution.get(0).stringValue()));
		for (Double r0 : rCurr0) {
			System.out.println(r0);
		}
		rCurr1.add(Double.parseDouble(theSolution.get(1).stringValue()));
		for (Double r1 : rCurr1) {
			System.out.println(r1);
		}
		
		return literal("0D");
	}

	@Override
	public Value get() throws ExpressionEvaluationException {
		System.out.println("ENTERING GET");
		if (rCurr0 == null || rCurr1 == null) {
			return literal("0D");
		}
		else {
			re = Rengine.getMainEngine();
			if (re == null) {
				re = new Rengine(new String[] {"--vanilla"}, false, null);
			}
			double[] y = new double[rCurr0.size()];
			for (int i = 0; i < rCurr0.size(); i++) {
				y[i] = rCurr0.get(i);
			}
			double[] z = new double[rCurr1.size()];
			for (int i = 0; i < rCurr1.size(); i++) {
				z[i] = rCurr1.get(i);
			}
			re.assign("y", y);
			re.assign("z", z);
			rCurr0 = null;
			rCurr1 = null;
			Literal result = literal(re.eval("cov(y,z)").asDouble());
			re.end();
			return result;
		}
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

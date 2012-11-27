/*--
 * Copyright 2012 Ren� M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidstack.script.operators;

import java.math.BigDecimal;
import java.util.HashMap;

import solidstack.io.SourceLocation;
import solidstack.lang.Assert;
import solidstack.script.expressions.Expression;
import solidstack.script.scopes.AbstractScope;
import solidstack.script.scopes.CombinedScope;


abstract public class Operator implements Expression
{
	static private final HashMap<String, Integer> precedences;

	// TODO Make private
	protected String operator;
	protected Expression left;
	protected Expression middle;
	protected Expression right;

	static
	{
		precedences = new HashMap<String, Integer>();

		precedences.put( "[", 1 ); // array index
		precedences.put( "(", 1 ); // method call
		precedences.put( ".", 1 ); // member access
		precedences.put( "#", 1 ); // static access

		precedences.put( "@++", 2 ); // postfix increment
		precedences.put( "@--", 2 ); // postfix decrement

		precedences.put( "++@", 3 ); // prefix increment
		precedences.put( "--@", 3 ); // prefix decrement
		precedences.put( "+@", 3 ); // unary plus
		precedences.put( "-@", 3 ); // unary minus
//		precedences.put( "~", 3 ); // bitwise NOT
		precedences.put( "!@", 3 ); // boolean NOT
//		precedences.put( "(type)", 3 ); // type cast
		precedences.put( "as", 3 ); // type cast
//		precedences.put( "new", 3 ); // object creation

		precedences.put( "*", 4 ); // multiplication
		precedences.put( "/", 4 ); // division
		precedences.put( "%", 4 ); // remainder

		precedences.put( "+", 5 ); // addition
		precedences.put( "-", 5 ); // subtraction

//		precedences.put( "<<", 6 ); // signed bit shift left
//		precedences.put( ">>", 6 ); // signed bit shift right
//		precedences.put( ">>>", 6 ); // unsigned bit shift right

		// In Groovy: .. and ... are between here

		precedences.put( "<", 7 ); // less than
		precedences.put( ">", 7 ); // greater than
		precedences.put( "<=", 7 ); // less than or equal
		precedences.put( ">=", 7 ); // greater than or equal
//		precedences.put( "instanceof", 7 ); // reference test
//
		precedences.put( "==", 8 ); // equal to
		precedences.put( "!=", 8 ); // not equal to

//		precedences.put( "&", 9 ); // bitwise AND
//		precedences.put( "^", 10 ); // bitwise XOR
//		precedences.put( "|", 11 ); // bitwise OR
		precedences.put( "&&", 12 ); // boolean AND
		precedences.put( "||", 13 ); // boolean OR

//		precedences.put( "?", 14 ); // conditional

		precedences.put( ":", 15 ); // label TODO 15 ok?
		precedences.put( "->", 15 ); // lambda TODO Equal to assignment precedence? Do we want that?
		precedences.put( "=", 15 ); // assignment
//		precedences.put( "*=", 15 ); // assignment
//		precedences.put( "/=", 15 ); // assignment
//		precedences.put( "+=", 15 ); // assignment
//		precedences.put( "-=", 15 ); // assignment
//		precedences.put( "%=", 15 ); // assignment
//		precedences.put( "<<=", 15 ); // assignment
//		precedences.put( ">>=", 15 ); // assignment
//		precedences.put( ">>>=", 15 ); // assignment
//		precedences.put( "&=", 15 ); // assignment
//		precedences.put( "^=", 15 ); // assignment
//		precedences.put( "|=", 15 ); // assignment

		precedences.put( ",", 16 ); // tuple TODO Decide about this precedence
	}

	static public Operator operator( String name, Expression left, Expression right )
	{
		// TODO The ifs are not all necessary, for example * is always just *
		switch( name.charAt( 0 ) )
		{
			case '*':
				if( name.equals( "*" ) )
					return new Multiply( name, left, right );
				break;

			case '+':
				if( name.equals( "+" ) )
					return new Plus( name, left, right );
				break;

			case '-':
				if( name.equals( "-" ) )
					return new Minus( name, left, right );
				if( name.equals( "->" ) )
					return new Function( name, left, right );
				break;

			case '=':
				if( name.equals( "=" ) )
					return new Assign( name, left, right );
				if( name.equals( "==" ) )
					return new Equals( name, left, right );
				break;

			case '!':
				if( name.equals( "!=" ) )
					return new NotEquals( name, left, right );
				break;

			case '<':
				if( name.equals( "<" ) )
					return new LessThan( name, left, right );
				if( name.equals( "<=" ) )
					return new LessOrEqualTo( name, left, right );
				break;

			case '>':
				if( name.equals( ">" ) )
					return new GreaterThan( name, left, right );
				if( name.equals( ">=" ) )
					return new GreaterOrEqualTo( name, left, right );
				break;

			case '@':
				if( name.equals( "@++" ) )
					return new PostInc( name, left, right );
				if( name.equals( "@--" ) )
					return new PostDecr( name, left, right );
				break;

			case '&':
				if( name.equals( "&&" ) )
					return new And( name, left, right );
				break;

			case '|':
				if( name.equals( "||" ) )
					return new Or( name, left, right );
				break;

			case '(':
				if( name.equals( "(" ) )
					return new Apply( name, left, right );
				break;

			case '[':
				if( name.equals( "[" ) )
					return new Index( name, left, right );
				break;

			case '.':
				if( name.equals( "." ) )
					return new Member( name, left, right );
				break;

			case '#':
				if( name.equals( "#" ) )
					return new StaticMember( name, left, right );
				break;

			case ':':
				if( name.equals( ":" ) )
					return new Label( name, left, right );
				break;

			case ',':
				if( name.equals( "," ) )
					return new BuildTuple( ",", left, right );
				break;

			case 'a':
				if( name.equals( "as" ) )
					return new As( "as", left, right );
				break;
		}
		Assert.fail( "Unknown operator " + name );
		return null;
	}

	static public Operator preOp( SourceLocation location, String name, Expression right )
	{
		// TODO The ifs are not all necessary, for example * is always just *
		switch( name.charAt( 0 ) )
		{
			case '+':
				if( name.equals( "++@" ) )
					return new PreInc( location, name, right );
				break;

			case '-':
				if( name.equals( "-@" ) )
					return new Negate( location, name, right );
				if( name.equals( "--@" ) )
					return new PreDecr( location, name, right );
				break;

			case '!':
				if( name.equals( "!@" ) )
					return new Not( location, name, right );
				break;
		}
		Assert.fail( "Unknown operator " + name );
		return null;
	}

	static protected Object add( Object left, Object right )
	{
		// TODO Type conversions
		if( left instanceof BigDecimal )
		{
			Assert.isInstanceOf( right, BigDecimal.class );
			return ( (BigDecimal)left ).add( (BigDecimal)right );
		}
		if( left instanceof Integer )
		{
			if( right instanceof Integer )
				return (Integer)left + (Integer)right;
			Assert.isInstanceOf( right, BigDecimal.class );
			left = new BigDecimal( (Integer)left );
			return ( (BigDecimal)left ).add( (BigDecimal)right );
		}
		if( left instanceof AbstractScope )
		{
			Assert.isInstanceOf( right, AbstractScope.class );
			return new CombinedScope( (AbstractScope)left, (AbstractScope)right );
		}
		Assert.isInstanceOf( left, String.class, "Not expecting " + left.getClass() );
		if( !( right instanceof String ) )
			right = right.toString();
		return (String)left + (String)right;
	}

	static protected Object mul( Object left, Object right )
	{
		// TODO Type conversions
		if( left instanceof Integer )
		{
			Assert.isInstanceOf( right, Integer.class );
			return (Integer)left * (Integer)right;
		}
		Assert.isInstanceOf( left, BigDecimal.class );
		Assert.isInstanceOf( right, BigDecimal.class );
		return ( (BigDecimal)left ).multiply( (BigDecimal)right );
	}

	static protected Object minus( Object left, Object right )
	{
		if( left instanceof Integer )
		{
			Assert.isInstanceOf( right, Integer.class );
			return (Integer)left - (Integer)right;
		}
		Assert.isInstanceOf( left, BigDecimal.class );
		Assert.isInstanceOf( right, BigDecimal.class );
		return ( (BigDecimal)left ).subtract( (BigDecimal)right );
	}

	static public Object negate( Object value )
	{
		if( value instanceof Integer )
			return - (Integer)value;
		Assert.isInstanceOf( value, BigDecimal.class );
		return ( (BigDecimal)value ).negate();
	}

	static protected int compare( Object left, Object right )
	{
		if( left instanceof Integer )
		{
			Assert.isInstanceOf( right, Integer.class );
			return ( (Integer)left ).compareTo( (Integer)right );
		}
		if( left instanceof BigDecimal )
		{
			Assert.isInstanceOf( right, BigDecimal.class );
			return ( (BigDecimal)left ).compareTo( (BigDecimal)right );
		}
		Assert.isInstanceOf( left, String.class );
		Assert.isInstanceOf( right, String.class );
		return ( (String)left ).compareTo( (String)right );
	}

	static public Object abs( Object value )
	{
		if( value instanceof Integer )
			return Math.abs( (Integer)value );
		Assert.isInstanceOf( value, BigDecimal.class );
		return ( (BigDecimal)value ).abs();
	}

	protected Operator( String operator, Expression left, Expression right )
	{
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public Operator append( String operator, Expression expression )
	{
		Assert.isTrue( precedences.containsKey( operator ), "Unexpected operator " + operator );
		Assert.isTrue( precedences.containsKey( this.operator ), "Unexpected operator " + this.operator );

		int prec = precedences.get( operator );
		Assert.isTrue( prec > 0 );

		int myprec = precedences.get( this.operator );
		Assert.isTrue( myprec > 0 );

		// 14 (label) and 15 (assignment) go from right to left
		if( myprec < prec )
			return Operator.operator( operator, this, expression ); // this has precedence

		if( myprec == prec )
		{
			if( myprec < 14 )
				return Operator.operator( operator, this, expression ); // this has precedence
			if( myprec == 16 )
			{
				BuildTuple tuple = (BuildTuple)this;
				tuple.append( expression );
				return tuple;
			}
		}

		Expression last = getLast();
		// appended operator has precedence
		if( last instanceof Operator )
			setLast( ( (Operator)last ).append( operator, expression ) );
		else
			setLast( Operator.operator( operator, last, expression ) );
		return this;
	}

	protected Expression getLast()
	{
		return this.right;
	}

	protected void setLast( Expression expression )
	{
		this.right = expression;
	}

	public SourceLocation getLocation()
	{
		return this.left.getLocation();
	}
}
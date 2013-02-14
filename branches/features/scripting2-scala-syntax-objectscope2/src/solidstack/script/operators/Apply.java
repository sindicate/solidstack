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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solidstack.script.JavaException;
import solidstack.script.Returning;
import solidstack.script.ThreadContext;
import solidstack.script.ThrowException;
import solidstack.script.expressions.Expression;
import solidstack.script.expressions.Identifier;
import solidstack.script.java.Java;
import solidstack.script.objects.FunctionObject;
import solidstack.script.objects.Type;
import solidstack.script.objects.Util;
import funny.Symbol;


public class Apply extends Operator
{
	public Apply( String name, Expression left, Expression right )
	{
		super( name, left, right );
	}

	public Object evaluate( ThreadContext thread )
	{
		if( this.left instanceof New )
		{
			Object left = Util.deref( ( (New)this.left ).evaluateForApply( thread ) );
			if( !( left instanceof Type ) )
				throw new ThrowException( "The new operator needs a type argument, not a " + left.getClass().getName(), thread.cloneStack( getLocation() ) );

			Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY;

			Class<?> cls = ( (Type)left ).theClass();
			try
			{
				thread.pushStack( getLocation() );
				try
				{
					return Java.construct( cls, Util.toJavaParameters( thread, pars ) );
				}
				finally
				{
					thread.popStack();
				}
			}
			catch( InvocationTargetException e )
			{
				Throwable t = e.getCause();
				if( t instanceof Returning )
					throw (Returning)t;
				throw new JavaException( t, thread.cloneStack( getLocation() ) );
			}
			catch( Exception e )
			{
				throw new ThrowException( e.getMessage(), thread.cloneStack( getLocation() ) );
			}
		}

//		Object left = this.left.evaluateRef( thread );
//
//		if( left instanceof ObjectMember )
//		{
//			ObjectMember ref = (ObjectMember)left;
//			Object object = ref.getObject();
//			String name = ref.getKey().toString();
//			Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY; // TODO Shouldn't this happen outside the try catch?
//			pars = Util.toJavaParameters( thread, pars );
//			thread.pushStack( getLocation() );
//			try
//			{
//				if( object instanceof Type )
//					return Java.invokeStatic( ( (Type)object ).theClass(), name, pars );
//				return Java.invoke( object, name, pars );
//			}
//			catch( InvocationTargetException e )
//			{
//				Throwable t = e.getCause();
//				if( t instanceof Returning )
//					throw (Returning)t;
//				throw new JavaException( t, thread.cloneStack( getLocation() ) );
//			}
//			catch( Returning e )
//			{
//				throw e;
//			}
//			catch( Exception e )
//			{
//				throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), thread.cloneStack( getLocation() ) );
////				throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
//			}
//			finally
//			{
//				thread.popStack();
//			}
//		}
//
//		if( left instanceof ObjectRef )
//		{
//			ObjectRef ref = (ObjectRef)left;
//			Object object = ref.getObject();
//			String name = ref.getKey().toString();
//			Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY; // TODO Shouldn't this happen outside the try catch?
//			pars = Util.toJavaParameters( thread, pars );
//			thread.pushStack( getLocation() );
//			try
//			{
//				if( object instanceof Type )
//					return Java.invokeStatic( ( (Type)object ).theClass(), name, pars );
//				return Java.invoke( object, name, pars );
//			}
//			catch( InvocationTargetException e )
//			{
//				Throwable t = e.getCause();
//				if( t instanceof Returning )
//					throw (Returning)t;
//				throw new JavaException( t, thread.cloneStack( getLocation() ) );
//			}
//			catch( Returning e )
//			{
//				throw e;
//			}
//			catch( Exception e )
//			{
//				throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), thread.cloneStack( getLocation() ) );
////				throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
//			}
//			finally
//			{
//				thread.popStack();
//			}
//		}

		if( this.left instanceof Member )
			return ( (Member)this.left ).apply( thread, this.right );

		Object left = this.left.evaluate( thread );
		if( left == null )
			throw new ThrowException( "Function is null", thread.cloneStack( getLocation() ) );

		if( left instanceof Type )
		{
			Class<?> cls = ( (Type)left ).theClass();
			Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY; // TODO Shouldn't this happen outside the try catch?
			pars = Util.toJavaParameters( thread, pars );
			thread.pushStack( getLocation() );
			try
			{
				return Java.invokeStatic( cls, "apply", pars );
			}
			catch( InvocationTargetException e )
			{
				Throwable t = e.getCause();
				if( t instanceof Returning )
					throw (Returning)t;
				throw new JavaException( t, thread.cloneStack( getLocation() ) );
			}
			catch( Returning e )
			{
				throw e;
			}
			catch( Exception e )
			{
				throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), thread.cloneStack( getLocation() ) );
//				throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
			}
			finally
			{
				thread.popStack();
			}
		}

		if( left instanceof FunctionObject )
		{
			FunctionObject f = (FunctionObject)left;
			List<Expression> vals;
			if( this.right instanceof BuildTuple )
				vals = ( (BuildTuple)this.right ).getExpressions();
			else if( this.right != null )
				vals = Arrays.asList( this.right );
			else
				vals = Collections.emptyList();
			if( !vals.isEmpty() && vals.get( 0 ) instanceof Assign )
			{
				Map<Symbol, Object> pars = new HashMap<Symbol, Object>();
				for( Expression expression : vals )
				{
					if( !( expression instanceof Assign ) )
						throw new ThrowException( "All parameters must be named", thread.cloneStack( expression.getLocation() ) );
					Assign assign = (Assign)expression;
					if( !( assign.left instanceof Identifier ) )
						throw new ThrowException( "Parameter must be named with a variable identifier", thread.cloneStack( assign.left.getLocation() ) );
					pars.put( ( (Identifier)assign.left ).getSymbol(), assign.right.evaluate( thread ) ); // TODO Error message
				}
				thread.pushStack( getLocation() );
				try
				{
					return f.call( thread, pars );
				}
				finally
				{
					thread.popStack();
				}
			}

			for( Expression expression : vals )
				if( expression instanceof Assign )
					throw new ThrowException( "All parameters must be named", thread.cloneStack( expression.getLocation() ) );
			Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY;
			thread.pushStack( getLocation() );
			try
			{
				return f.call( thread, pars );
			}
			finally
			{
				thread.popStack();
			}
		}

		Object[] pars = this.right != null ? Util.toArray( this.right.evaluate( thread ) ) : Util.EMPTY_ARRAY; // TODO Shouldn't this happen outside the try catch?
		pars = Util.toJavaParameters( thread, pars );
		thread.pushStack( getLocation() );
		try
		{
			return Java.invoke( left, "apply", pars );
		}
		catch( InvocationTargetException e )
		{
			Throwable t = e.getCause();
			if( t instanceof Returning )
				throw (Returning)t;
			throw new JavaException( t, thread.cloneStack( getLocation() ) );
		}
		catch( Returning e )
		{
			throw e;
		}
		catch( Exception e )
		{
			throw new ThrowException( e.getMessage() != null ? e.getMessage() : e.toString(), thread.cloneStack( getLocation() ) );
//			throw new JavaException( e, thread.cloneStack( getLocation() ) ); // TODO Debug flag or something?
		}
		finally
		{
			thread.popStack();
		}
	}

	@Override
	public void writeTo( StringBuilder out )
	{
		this.left.writeTo( out );
		out.append( '(' );
		if( this.right != null )
			this.right.writeTo( out );
		out.append( ')' );
	}
}
/*--
 * Copyright 2012 René M. de Bloois
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

package solidstack.script.scopes;

import java.util.Map;

import funny.Symbol;




public interface Scope
{
	// By symbol

	<T> void var( Symbol symbol, T value );
	<T> void val( Symbol symbol, T value );

	<T> T find( Symbol symbol );
	<T> T get( Symbol symbol );

	<T> void setOrVar( Symbol symbol, T value );
	<T> void set( Symbol symbol, T value );

	<T> T apply( Symbol symbol, Object... args );
	<T> T apply( Symbol symbol, Map<String, Object> args );

	// By name

	<T> void var( String name, T value );
	<T> void val( String name, T value );

	<T> T find( String name );
	<T> T get( String name );

	<T> void setOrVar( String name, T value );
	<T> void set( String name, T value );

	<T> T apply( String name, Object... args );
	<T> T apply( String name, Map<String, Object> args );
}

/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.core.structure.loop

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.excilys.ebi.gatling.core.session.Session
import com.excilys.ebi.gatling.core.structure.{ AbstractStructureBuilder, ChainBuilder }
import com.excilys.ebi.gatling.core.structure.loop.handler.{ ConditionalLoopHandlerBuilder, DurationLoopHandlerBuilder, TimesLoopHandlerBuilder }
import com.excilys.ebi.gatling.core.util.StringHelper.parseEvaluatable

import akka.util.Duration

/**
 * This class serves as DSL description of a loop
 *
 * @constructor constructs a new LoopBuilder
 * @param structureBuilder the structure builder on which loop has been called
 * @param chain the chain that should be repeated
 * @param counterName the optionnal counter name
 */
@deprecated("Will be removed in 1.4.0", "1.3.0")
class LoopBuilder[B <: AbstractStructureBuilder[B]](structureBuilder: B, chain: ChainBuilder, counterName: Option[String]) {

	/**
	 * This method defines a counter name for the currently described loop
	 *
	 * @param counterName the name of the counter
	 */
	def counterName(counterName: String) = new LoopBuilder[B](structureBuilder, chain, Some(counterName))

	/**
	 * This method sets the number of iterations that should be done by the loop
	 *
	 * @param times the number of iterations
	 */
	@deprecated("""Will be removed in Gatling 1.4.0. Use "repeat(times) { chain }" instead""", "1.3.0")
	def times(timesValue: Int): B = new TimesLoopHandlerBuilder(structureBuilder, chain, timesValue, counterName).build

	@deprecated("""Will be removed in Gatling 1.4.0. Use "repeat(times) { chain }" instead""", "1.3.0")
	def times(timesValue: String): B = {
		val sessionFunction = parseEvaluatable(timesValue)
		times((s: Session) => sessionFunction(s).toInt)
	}

	@deprecated("""Will be removed in Gatling 1.4.0. Use "repeat(times) { chain }" instead""", "1.3.0")
	def times(timesValue: Session => Int): B = {
		counterName match {
			case Some(counter) => asLongAs((s: Session) => s.getCounterValue(counter) < timesValue(s))
			case None =>
				val counter = counterName.getOrElse(UUID.randomUUID.toString)
				counterName(counter).asLongAs((s: Session) => s.getCounterValue(counter) < timesValue(s))
		}
	}

	/**
	 * This method sets the duration of the loop
	 *
	 * @param durationValue the value of the duration
	 * @param durationUnit the unit of the duration
	 */
	@deprecated("""Will be removed in Gatling 1.4.0. Use "during(duration) { chain }" instead""", "1.3.0")
	def during(durationValue: Int, durationUnit: TimeUnit): B = new DurationLoopHandlerBuilder(structureBuilder, chain, Duration(durationValue, durationUnit), counterName).build

	/**
	 * This method sets the duration of the loop in seconds
	 *
	 * @param durationValue the value of the duration in seconds
	 */
	@deprecated("""Will be removed in Gatling 1.4.0. Use "during(duration) { chain }" instead""", "1.3.0")
	def during(durationValue: Int): B = during(durationValue, TimeUnit.SECONDS)

	/**
	 * This method sets the condition that will stop the loop
	 *
	 * @param conditionFunction the condition function
	 */
	@deprecated("""Will be removed in Gatling 1.4.0. Use "asLongAs(condition) { chain }" instead""", "1.3.0")
	def asLongAs(conditionFunction: Session => Boolean): B = new ConditionalLoopHandlerBuilder(structureBuilder, chain, conditionFunction, counterName).build

	/**
	 * This method sets the equality condition that will stop the loop
	 *
	 * @param sessionKey the key of the value in the session
	 * @param value the value to which the session value is compared
	 */
	@deprecated("""Will be removed in Gatling 1.4.0. Use "asLongAs(condition) { chain }" instead""", "1.3.0")
	def asLongAs(sessionKey: String, value: String): B = asLongAs((session: Session) => parseEvaluatable(sessionKey)(session) == value)
}
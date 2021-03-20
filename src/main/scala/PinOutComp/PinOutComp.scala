/**
 * Examples of SpinalHDL Design on Sparkfun Alchitry CU.
 * Copyright (C) 2021 Vadim MUKHTAROV
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * For information on this project: tuppi.ovh@gmail.com.
 */

package PinOutComp

import spinal.core._
import spinal.lib._
import spinal.lib.io._


// Hardware definition
abstract class PinOutComp extends Component {
  // io
  val LED0 = out Bool() 
  val LED1 = out Bool() 
  val LED2 = out Bool()
  val LED3 = out Bool()
  val LED4 = out Bool() 
  val LED5 = out Bool()
  val LED6 = out Bool()
  val LED7 = out Bool()
  val USB_TX = out Bool()
  val USB_RX = in Bool()
}

// Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one.
// This configuration can be reused everywhere
object MySpinalConfig extends SpinalConfig(
  defaultConfigForClockDomains = ClockDomainConfig(
    clockEdge        = RISING,
    resetKind        = ASYNC,
    resetActiveLevel = LOW
  )
)



/**
 * Example of SpinalHDL Design on Sparkfun Alchitry CU.
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

package toplevel

import spinal.core._
import spinal.lib._

import blink._

// Hardware definition
class MyTopLevel extends Component {
  // io
  val LED0 = out Bool() 
  val LED1 = out Bool() 
  val LED2 = out Bool()
  val LED3 = out Bool()
  val LED4 = out Bool() 
  val LED5 = out Bool()
  val LED6 = out Bool()
  val LED7 = out Bool() 
}

// Simple Blink Project 
class SimpleBlinkDesign extends MyTopLevel {
  // components
  val blink = new Blink()
  // connect io
  LED0 := blink.io.led
  LED1 := False
  LED2 := False
  LED3 := False
  LED4 := False
  LED5 := False
  LED6 := False
  LED7 := False
}

// Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one.
// This configuration can be reused everywhere
object MySpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC))

// Generate the MyTopLevel's Verilog using the above custom configuration.
object MyTopLevelVerilogWithCustomConfig {
  def main(args: Array[String]) {
    if (args.length > 0) {
      args(0) match {
        case "SimpleBlinkDesign" => MySpinalConfig.generateVerilog(new SimpleBlinkDesign)
        case _ => throw new Error("Design doesn't exist")
      }
    } else {
      throw new Error("Design is not specified")
    }
  }
}

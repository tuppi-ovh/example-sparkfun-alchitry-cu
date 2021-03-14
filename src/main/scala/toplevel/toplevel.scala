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
  val io = new Bundle {
    val led = out Bool()
  }

  val blink = new Blink()

  // connect IO
  io.led := blink.io.led
}

// Generate the MyTopLevel's Verilog
//object MyTopLevelVerilog {
//  def main(args: Array[String]) {
//    SpinalVerilog(new MyTopLevel)
//  }
//}

// Generate the MyTopLevel's VHDL
//object MyTopLevelVhdl {
//  def main(args: Array[String]) {
//    SpinalVhdl(new MyTopLevel)
//  }
//}

// Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one.
// This configuration can be reused everywhere
object MySpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC))

// Generate the MyTopLevel's Verilog using the above custom configuration.
object MyTopLevelVerilogWithCustomConfig {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new MyTopLevel)
  }
}
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

package apb

import scala.collection.mutable._
import spinal.core._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc._
import PinOutComp._
import SimpleBlinkDesign.SimpleBlinkDesign


class ApbComp(ledWidth : Int) extends Component {
  // io
  val io = new Bundle {
    val leds = out UInt (ledWidth bits)
  }

  // apb leds
  val ledCtrl = Apb3Gpio(gpioWidth = ledWidth, withReadSync = true)
  ledCtrl.io.gpio.asOutput()
  for (i <- 0 until ledWidth) {
    io.leds(i) := ledCtrl.io.gpio.asBits(i)
  }

  // apb master
  val apbMaster = Apb3Dummy(config = Apb3Config(addressWidth = 20, dataWidth = 32))

  // apb slaves
  val apbSlaves = ArrayBuffer[(Apb3, SizeMapping)]()
  apbSlaves += ledCtrl.io.apb -> (0x00000, 4 KiB)

  val apbDecoder = Apb3Decoder(
    master = apbMaster.io.apb,
    slaves = apbSlaves
  )
}

// Apb Blink Design
class ApbBlinkDesign extends PinOutComp {
  // components
  val apb = new ApbComp(8)
  // connect io
  LED0 := apb.io.leds.asBits(0)
  LED1 := False
  LED2 := False
  LED3 := False
  LED4 := False
  LED5 := False
  LED6 := False
  LED7 := False
}

// Generate the MyTopLevel's Verilog using the above custom configuration.
object ApbBlinkDesignVerilog {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new SimpleBlinkDesign)
  }
}
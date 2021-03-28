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

package HelloWorldDesign

import scala.collection.mutable._
import spinal.core._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc._
import spinal.lib.com.uart._

import PinOutComp._
import MasterComp._


class HelloWorldDesign(waitTicks : Int) extends PinOutComp {
  // config
  val ledWidth = 8
  val dataWidth = 32
  val addrWidth = 20 // from 0x00000 to 0xFFFFF
  val uartCtrlConfig = UartCtrlMemoryMappedConfig(
    uartCtrlConfig = UartCtrlGenerics(
      dataWidthMax      = 8,
      clockDividerWidth = 20,
      preSamplingSize   = 1,
      samplingSize      = 3,
      postSamplingSize  = 1
    ),
    initConfig = UartCtrlInitConfig(
      baudrate = 38400,
      dataLength = 7,  //7 => 8 bits
      parity = UartParityType.NONE,
      stop = UartStopType.ONE
    ),
    busCanWriteClockDividerConfig = false,
    busCanWriteFrameConfig = false,
    txFifoDepth = 16,
    rxFifoDepth = 16
  )

  // regs
  val leds = Reg(UInt(ledWidth bits))

  // apb leds
  val ledCtrl = Apb3Gpio(gpioWidth = ledWidth, withReadSync = true)
  ledCtrl.io.gpio.read := 0

  // apb uart
  val uartCtrl = Apb3UartCtrl(uartCtrlConfig)
  //uartCtrl.uartCtrl.io.config.setClockDivider(115.2 kHz, clkFrequency=100 MHz)

  // gpio data
  var data = List((0x00004, 0x00)) // GPIO Write Init
  data = (0x00008, 0xFF) :: data // GPIO Write Enable
  for (i <- 0 until 11) {
    data = (0x00004, i % 2) :: data // GPIO Write x 10 values
  }
  // dummy data
  for (i <- 0 until 10) {
    data = (0xFFFFF, 0) :: data
  }
  // uart data
  var str = "Hello, world! \n\r"
  for (s <- str) {
    data = (0x10000, s.toInt) :: data // UART print "Hello, world!"
  }
  // dummy data
  for (i <- 0 until 10) {
    data = (0xFFFFF, 0) :: data
  }

  // apb master
  val config = Apb3Config(addressWidth=addrWidth, dataWidth=dataWidth)
  val apbMaster = new MasterComp(config=config, waitTicks=waitTicks, data=data.reverse)

  // apb slaves
  val apbSlaves = ArrayBuffer[(Apb3, SizeMapping)]()
  apbSlaves += ledCtrl.io.apb -> (0x00000, 4 KiB)
  apbSlaves += uartCtrl.io.apb -> (0x10000, 4 KiB)

  val apbDecoder = Apb3Decoder(
    master = apbMaster.io.apb,
    slaves = apbSlaves
  )

  // connect io
  LED0 := ledCtrl.io.gpio.write.asBits(0) // heart beat
  LED1 := False
  LED2 := False
  LED3 := False
  LED4 := False
  LED5 := False
  LED6 := !USB_TX // receive activity
  LED7 := !uartCtrl.io.uart.txd // sending activity
  USB_RX := uartCtrl.io.uart.txd // RX for FTDI chip

  uartCtrl.io.uart.rxd := True // to not block the txd
}

// Generate the MyTopLevel's Verilog using the above custom configuration.
object HelloWorldDesignVerilog {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new HelloWorldDesign(waitTicks=10000000))
  }
}
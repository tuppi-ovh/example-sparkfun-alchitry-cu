/**
 * Examples of SpinalHDL Design on Sparkfun Alchitry CU.
 * Copyright (C) 2021 tuppi-ovh
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

package SramDesign

import scala.collection.mutable._
import spinal.core._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc._
import spinal.lib.com.uart._
import PinOutComp._
import MasterBmbComp._
import spinal.lib.bus.amba3.ahblite._
import spinal.lib.bus.bmb.{BmbAccessCapabilities, _}
import spinal.lib.generator.{Handle, Unset}
import spinal.lib.misc.Clint

import scala.collection.mutable
import scala.reflect.internal.util.NoPosition.source


class SramDesign(waitTicks : Int) extends PinOutComp {

  // Handles
  //val m = Handle[Bmb]
  //val s = Handle[Bmb]
  //val cap = Handle[BmbAccessCapabilities]
  //val req = Handle[BmbAccessParameter]
  //val mp = Handle[AddressMapping]
  val requirements = Handle[BmbAccessParameter]
  val capabilities = Handle[BmbAccessCapabilities]
  val cmdData = Handle[List[(Int, Int, Int)]]

  // config
  val ledWidth = 8
  val dataWidth = 32
  val addrWidth = 20 // from 0x00000 to 0xFFFFF

  // bmb interconnect
  val bmbInterconnect = BmbInterconnectGenerator()
  bmbInterconnect.setDefaultArbitration(BmbInterconnectGenerator.STATIC_PRIORITY)

  // bmb master
  val bmbMaster = new MasterBmbComp(
    accessRequirements = requirements,
    waitTicks = waitTicks,
    cmdData = cmdData
  )
  bmbInterconnect.addMaster(accessRequirements=requirements, bus=Handle(bmbMaster.logic.io.bmb))

  // bmb ram
  //var bmbRam = BmbOnChipRam(
  //  p = requirements.toBmbParameter(),
  //  size = 64 KiB
  //)

  // apb
  val apbConfig = Apb3Config(addressWidth = addrWidth, dataWidth = dataWidth)
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

  // bmb to apb bridge
  val bmbApbBridge = Handle(BmbToApb3Bridge(
    apb3Config = apbConfig,
    bmbParameter = requirements.toBmbParameter(),
    pipelineBridge = false
  ))
  // slave
  bmbInterconnect.addSlave(
    accessRequirements = requirements,
    accessCapabilities = capabilities,
    bus = Handle(bmbApbBridge.io.input),
    mapping = Handle(SingleMapping(0x00000))
  )

  println("here 3")

  val apbArea = Handle(new Area {
    // apb leds
    val ledCtrl = Apb3Gpio(gpioWidth = ledWidth, withReadSync = true)
    ledCtrl.io.gpio.read := 0

    // apb uart
    val uartCtrl = Apb3UartCtrl(uartCtrlConfig)

    // apb slaves
    val apbSlaves = ArrayBuffer[(Apb3, SizeMapping)]()
    apbSlaves += ledCtrl.io.apb -> (0x00000, 4 KiB)
    apbSlaves += uartCtrl.io.apb -> (0x10000, 4 KiB)

    val apbDecoder = Apb3Decoder(
      master = bmbApbBridge.io.output, //apbMaster.io.apb,
      slaves = apbSlaves
    )

    //bmbMaster.logic.io.bmb <> bmbApbBridge.io.input
  })

  println("here")

  bmbInterconnect.addConnection(Handle(bmbMaster.logic.io.bmb), Handle(bmbApbBridge.io.input))

  // load experimental data ==========
  // gpio data
  var data = List((0x00004, 0x00, 1)) // GPIO Write Init
  data = (0x00008, 0xFF, 1) :: data // GPIO Write Enable
  for (i <- 0 until 11) {
    data = (0x00004, i % 2, 1) :: data // GPIO Write x 10 values
  }
  // dummy data
  for (i <- 0 until 10) {
    data = (0xFFFFF, 0, 1) :: data
  }
  // uart data
  var str = "Hello, world! \n\r"
  for (s <- str) {
    data = (0x10000, s.toInt, 1) :: data // UART print "Hello, world!"
  }
  // dummy data
  for (i <- 0 until 10) {
    data = (0xFFFFF, 0, 1) :: data
  }


  // log
  println("SramDesign before load")


  // load
  //m.load(bmbMaster.logic.io.bmb)
  //s.load(apbArea.bmbApbBridge.io.input)
  //mp.load(SingleMapping(0x00000))
  cmdData.load(data.reverse)
  requirements.load(
    BmbAccessParameter(
      addressWidth = addrWidth,
      dataWidth = dataWidth
    ).withSingleSource(
      BmbSourceParameter(
        contextWidth = 0,
        lengthWidth = log2Up(dataWidth / 8),
        alignment = BmbParameter.BurstAlignement.WORD
      )
    )
  )
  capabilities.load(
    BmbAccessCapabilities(addressWidth=addrWidth, dataWidth=dataWidth)
  )

  // log
  println("SramDesign after load")

  // connect io ======================
  LED0 := apbArea.ledCtrl.io.gpio.write.asBits(0) // heart beat
  LED1 := False
  LED2 := False
  LED3 := False
  LED4 := False
  LED5 := False
  LED6 := !USB_TX // receive activity
  LED7 := !apbArea.uartCtrl.io.uart.txd // sending activity
  USB_RX := apbArea.uartCtrl.io.uart.txd // RX for FTDI chip

  apbArea.uartCtrl.io.uart.rxd := True // to not block the txd
}

// Generate the MyTopLevel's Verilog using the above custom configuration.
object SramDesignVerilog {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new SramDesign(waitTicks=10000000))
  }
}
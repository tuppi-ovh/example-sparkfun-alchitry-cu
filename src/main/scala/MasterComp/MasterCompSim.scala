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

package MasterComp

import spinal.core.sim._
import spinal.lib.bus.amba3.apb.Apb3Config

object MasterCompSim {
  def main(args: Array[String]) {
    val config = Apb3Config(addressWidth = 20, dataWidth = 32)

    // master data
    var data = List((0x00004, 0x00)) // GPIO Write Init
    data = (0x00008, 0xFF) :: data // GPIO Write Enable
    for (i <- 0 until 10) {
      data = (0x00004, 1 << i) :: data // GPIO Write
    }

    SimConfig.withWave.doSim(new MasterComp(config, 10, data.reverse)) { dut =>
      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      //
      dut.io.apb.PREADY #= true

      sleep(100000)
    }
  }
}

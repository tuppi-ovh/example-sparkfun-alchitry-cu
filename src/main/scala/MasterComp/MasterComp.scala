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

package MasterComp

import spinal.core.{UInt, _}
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.fsm._

class MasterComp(config : Apb3Config, waitTicks : Int, data : List[(Int, Int)]) extends Component {
  val io = new Bundle {
    val apb = master(Apb3(config))
  }

  // convert list to vec
  val lenVec = data.length
  val addrVec = Vec(UInt(config.addressWidth bits), lenVec)
  val dataVec = Vec(UInt(config.dataWidth bits), lenVec)
  for (i <- 0 until lenVec) {
    addrVec(i) := data(i)._1
    dataVec(i) := data(i)._2
  }

  // registers
  val paddr = Reg(UInt(config.addressWidth bits)) init 0
  val pwdata = Reg(Bits(config.dataWidth bits)) init 0
  val pwrite = Reg(Bool) init False
  val penable = Reg(Bool) init False
  val psel = Reg(Bool) init False

  // counters
  val waitCounterMax = waitTicks
  val waitCounter = Reg(UInt(log2Up(waitCounterMax) bits)) init 0
  val vecCounter = Reg(UInt(log2Up(lenVec) bits)) init 0

  // state machine
  val fsm = new StateMachine {

    val stateStart: State = new State with EntryPoint {
      whenIsActive {
        paddr := addrVec(vecCounter)
        pwdata := dataVec(vecCounter).asBits
        when(vecCounter < lenVec) {
          vecCounter := vecCounter + 1
        } otherwise {
          vecCounter := 0
        }
        goto(stateWrite)
      }
    }

    val stateWrite: State = new State {
      whenIsActive {
        pwrite := True
        psel := True
        goto(stateEnable)
      }
    }

    val stateEnable: State = new State {
      whenIsActive {
        penable := True
        goto(stateWaitReady)
      }
    }

    val stateWaitReady: State = new State {
      whenIsActive {
        when(io.apb.PREADY) {
          penable := False
          psel := False
          goto(stateWait)
        }
      }
    }

    val stateWait: State = new State {
      onEntry(waitCounter := 0)
      whenIsActive {
        when(waitCounter < waitCounterMax) {
          waitCounter := waitCounter + 1
        } otherwise {
          goto(stateStart)
        }
      }
    }

    // io
    io.apb.PADDR := paddr
    io.apb.PWRITE := pwrite
    io.apb.PENABLE := penable
    io.apb.PWDATA := pwdata
    for (i <- 0 until config.selWidth) {
      io.apb.PSEL(i) := psel
    }
  }
}

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

package MasterBmbComp

import spinal.core.{UInt, _}
import spinal.lib._
import spinal.lib.bus.bmb.{Bmb, BmbAccessParameter, BmbParameter}
import spinal.lib.fsm._
import spinal.lib.generator.Handle

class MasterBmbComp(accessRequirements : Handle[BmbAccessParameter],
                    cmdData : Handle[List[(Int, Int, Int)]],
                    waitTicks : Int) extends Component {

  // log
  println("MasterBmbComp")

  // elaborate Handles
  val logic = Handle(new Area {

    // log
    println("MasterBmbComp logic")

    // handles
    val param = accessRequirements.toBmbParameter()
    val data = cmdData.get

    // component IOs
    val io = new Bundle {
      val bmb = master(Bmb(param))
    }

    // convert list to vec
    val lenVec = data.length
    val addrVec = Vec(UInt(param.access.addressWidth bits), lenVec)
    val dataVec = Vec(UInt(param.access.dataWidth bits), lenVec)
    val opcodeVec = Vec(UInt(1 bits), lenVec)
    for (i <- 0 until lenVec) {
      addrVec(i) := data(i)._1
      dataVec(i) := data(i)._2
      opcodeVec(i) := data(i)._3
    }

    // registers
    val addr = Reg(UInt(param.access.addressWidth bits)) init 0
    val wdata = Reg(Bits(param.access.dataWidth bits)) init 0
    val opcode = Reg(Bits(1 bits)) init 0
    val valid = Reg(Bool) init False
    val ready = Reg(Bool) init True

    // counters
    val waitCounterMax = waitTicks
    val waitCounter = Reg(UInt(log2Up(waitCounterMax) bits)) init 0
    val vecCounter = Reg(UInt(log2Up(lenVec) bits)) init 0

    // state machine
    val fsm = new StateMachine {

      val stateStart: State = new State with EntryPoint {
        whenIsActive {
          addr := addrVec(vecCounter)
          wdata := dataVec(vecCounter).asBits
          opcode := opcodeVec(vecCounter).asBits
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
          valid := True
          goto(stateWaitReady)
        }
      }

      val stateWaitReady: State = new State {
        whenIsActive {
          when(io.bmb.cmd.ready) {
            valid := False
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
      io.bmb.cmd.valid := valid
      io.bmb.cmd.payload.fragment.address := addr
      io.bmb.cmd.payload.fragment.opcode := opcode
      io.bmb.cmd.payload.fragment.data := wdata
      io.bmb.rsp.ready := ready
    }
  })
}

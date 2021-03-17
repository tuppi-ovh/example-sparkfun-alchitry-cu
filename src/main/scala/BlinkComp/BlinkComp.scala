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

package BlinkComp

import scala.util.Random
import spinal.core._
import spinal.lib._


class BlinkComp(seed : Int) extends Component {
  val io = new Bundle {
    val led = out Bool()
  }

  val random = new Random(seed)

  def randomBoolList(n : Int) : List[Bool] = {
    val temp = if (random.nextInt(100) > 50) True else False
    if (n == 0) List(temp) else temp :: randomBoolList(n - 1)
  }

  val divider_max = 10//5000000
  val counter_max = 100

  val counter = Reg(UInt(width = log2Up(counter_max) bits)) init 0
  val divider = Reg(UInt(width = 32 bits)) init 0

  val list = randomBoolList(counter_max)

  // divide the clock
  divider := divider + 1
  when (divider > divider_max) {
    divider := 0
    when (counter >= counter_max) {
      counter := 0
    } otherwise {
      counter := counter + 1
    }
  }

  // output
  io.led := list(counter)
}
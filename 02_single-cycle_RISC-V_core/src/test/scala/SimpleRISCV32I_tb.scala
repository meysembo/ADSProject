// ADS I Class Project
// Single-Cycle RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/10/2023 by Tobias Jauch (@tojauch)

package SimpleRV32I_Tester

import chisel3._
import chiseltest._
import SimpleRV32I._
import org.scalatest.flatspec.AnyFlatSpec

class SimpleRISCV32ITest extends AnyFlatSpec with ChiselScalatestTester {

"SimpleRV32I_Tester" should "work" in {
    test(new SimpleRV32I("src/test/programs/BinaryFile")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      dut.clock.setTimeout(0)

      dut.io.result.expect(0.U)     // ADDI x0, x0, 0
      dut.clock.step(1)
      dut.io.result.expect(4.U)     // ADDI x1, x0, 4
      dut.clock.step(1)
      dut.io.result.expect(5.U)     // ADDI x2, x0, 5
      dut.clock.step(1)
      dut.io.result.expect(9.U)     // ADD x3, x1, x2
      
        /* 
         * Check remaining R-type instruction results from 'BinaryFile'
         */

      dut.clock.step(1)
      dut.io.result.expect("hffffffff".U)  // SUB  x4 = 4 - 5 = -1 -> 0xfffffffff
      dut.clock.step(1)
      dut.io.result.expect(128.U)         // SLL  x5 = 4 << 5 = 128
      dut.clock.step(1)
      dut.io.result.expect(1.U)           // SLT  x6 = (4 < 5) ? 1 : 0
      dut.clock.step(1)
      dut.io.result.expect(1.U)           // SLTU x7 = (4 < 5) ? 1 : 0 (unsigned)
      dut.clock.step(1)
      dut.io.result.expect(1.U)           // XOR  x8 = 4 ^ 5 = 1
      dut.clock.step(1)
      dut.io.result.expect(0.U)           // SRL  x9 = 4 >> 5 = 0 (logical)
      dut.clock.step(1)
      dut.io.result.expect(0.U)           // SRA  x10 = 4 >> 5 = 0 (arithmetic)
      dut.clock.step(1)
      dut.io.result.expect(5.U)           // OR   x11 = 4 | 5 = 5
      dut.clock.step(1)
      dut.io.result.expect(4.U)           // AND  x12 = 4 & 5 = 4
           
    }
  }
}



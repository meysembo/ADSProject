// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

      ///////////////////////////////////////////////////////
      /////////         Simple 8 bits           /////////////
      ///////////////////////////////////////////////////////

      // Test receiving a start bit followed by 8 data bits
      dut.io.rxd.poke(0.U)  // Start bit (0)
      dut.clock.step(1)      // Step 1 clock cycle
      for (i <- 7 to 0 by -1) {
        dut.io.rxd.poke(((0x80 >> i) & 1).U)  // Sending bits from 0x80 (binary 10000000)
        dut.clock.step(1)            // Step 1 clock cycle for each bit
      }

      // After 8 data bits are received, valid should be asserted
      dut.io.valid.expect(1.U)  // Expect valid signal to be high for one clock cycle
      dut.io.data.expect("b10000000".U)  // Expect data to be 0x80 (MSB set)

      dut.clock.step(1)  // Step one more cycle to allow valid to go low again


      ///////////////////////////////////////////////////////
      /////////  Handle multiple bytes in a row /////////////
      ///////////////////////////////////////////////////////

      // Test first byte: 0xAB
      dut.io.rxd.poke(0.U)  // Start bit (0)
      dut.clock.step(1)
      for (i <- 7 to 0 by -1) {
        dut.io.rxd.poke(((0xAB >> i) & 1).U)  // Sending bits from 0xAB
        dut.clock.step(1)
      }
      dut.io.valid.expect(1.U)  // Expect valid signal to be high after the first byte
      dut.io.data.expect("b10101011".U)  // Expect data to be 0xAB

      // Step to next byte
      dut.clock.step(1)

      // Test second byte: 0x55
      dut.io.rxd.poke(0.U)  // Start bit (0)
      dut.clock.step(1)
      for (i <- 7 to 0 by -1) {
        dut.io.rxd.poke(((0x55 >> i) & 1).U)  // Sending bits from 0x55
        dut.clock.step(1)
      }
      dut.io.valid.expect(1.U)  // Expect valid signal to be high after the second byte
      dut.io.data.expect("b01010101".U)  // Expect data to be 0x55

      dut.clock.step(1)  // Step one more cycle



      /////////////////////////////////////////////////////////
      //  Remain in idle state when there is no transmission //
      /////////////////////////////////////////////////////////

      // Simulate idle state: no transmission, rxd should stay high (1)
      dut.io.rxd.poke(1.U)
      dut.clock.step(10)  // Step 10 cycles with idle line

      // Assert valid is low (no new data)
      dut.io.valid.expect(0.U)
      dut.io.data.expect("b11111111".U)  // Data should be 0, as no new byte was received




      ///////////////////////////////////////////////////////
      //////////          reset correctly         ///////////
      ///////////////////////////////////////////////////////

      // Assert reset signal
      dut.io.reset_n.poke(1.U)
      dut.clock.step(1)  // Step 1 cycle with reset active
      dut.io.valid.expect(0.U)  // Expect valid to be low

      // Release reset signal
      dut.io.reset_n.poke(0.U)
      dut.clock.step(1)  // Step 1 cycle after reset

      // Send a valid byte after reset
      dut.io.rxd.poke(0.U)  // Start bit (0)
      dut.clock.step(1)
      for (i <- 7 to 0 by -1) {
        dut.io.rxd.poke(((0x01 >> i) & 1).U)  // Sending bits from 0x01
        dut.clock.step(1)
      }
      dut.io.valid.expect(1.U)  // Expect valid signal to be high after the byte
      dut.io.data.expect("b00000001".U)  // Expect data to be 0x01
    }
    } 
}


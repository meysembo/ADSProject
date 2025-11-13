// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._


/** controller class */
class Controller extends Module{
  
  val io = IO(new Bundle {
    /* Define IO ports of a the component as stated in the documentation */
    val reset_n = Input(Uint(1.W))
    val rxd     = Input(Uint(1.W))
    val cnt_s   = Input(Uint(1.W))
    val cnt_en  = Output(Uint(1.W))
    val valid   = Output(Uint(1.W))
    })

  // internal variables
  val idle :: start :: received :: Nil = Enum(3)
  val state = RegInit(idle)

  io.cnt_en := 0.U
  io.valid  := 0.U  // default outputs

  // state machine
  when(io.reset_n === 1.U){
    state     := idle
    io.valid  := 0.U             // reset states and outputs
    io.cnt_en := 0.U
  } .otherwise {
    switch(state){
      is(idle){
        when(io.rxd === 0.U){    // detect start bit
          state := start
        }
      }
      is(start){
        io.cnt_en := 1.U
        state     := received
      }
      is(received){
        when(io.cnt_s === 1.U) { // 8 bits have been received
          io.valid  := 1.U       // set valid for 1 clock
          io.cnt_en := 0.U       // stop counting
          state     := idle
        }
      }
    }
  }

}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
    /* Define IO ports of a the component as stated in the documentation */
    val cnt_en = Input(Uint(1.W))
    val cnt_s  = Output(Uint(1.W))
    })

  // internal variables
  val count = RegInit(0.U(3.W)) // 3 bits to count from 0 to 7
  val idle :: start_count :: end_count :: Nil = Enum(3) // states
  val state = RegInit(idle)

  // state machine
  switch(state){
    is(idle){
      when(io.cnt_en === 1.U){
        state := start_count
        count := count + 1.U  // when cnt_en goes high, the first data bit is already going through
      }
    }
    is(start_count){
      count := count + 1.U   // counts from 0 to 7 -> 8 bits
      when(count === 7.U){
        state := end_count
      }
    }
    is(end_count){
      count := 0.U
      cnt_s := 1.U
      state := idle
    }
  }


}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
    /* Define IO ports of a the component as stated in the documentation */
    val rxd  = Input(Uint(1.W))
    val data = Output(Uint(8.W))
    })

  // internal variables
  val reg = RegInit(0.U(8.W)) // 8 bits shift reg

  // functionality
  reg := (reg << 1 ) + io.rxd
  data := reg
}

/** 
  * The last warm-up task deals with a more complex component. Your goal is to design a serial receiver.
  * It scans an input line (“serial bus”) named rxd for serial transmissions of data bytes. A transmission 
  * begins with a start bit ‘0’ followed by 8 data bits. The most significant bit (MSB) is transmitted first. 
  * There is no parity bit and no stop bit. After the last data bit has been transferred a new transmission 
  * (beginning with a start bit, ‘0’) may immediately follow. If there is no new transmission the bus line 
  * goes high (‘1’, this is considered the “idle” bus signal). In this case the receiver waits until the next 
  * transmission begins. The outputs of the design are an 8-bit parallel data signal and a valid signal. 
  * The valid signal goes high (‘1’) for one clock cycle after the last serial bit has been transmitted, 
  * indicating that a new data byte is ready.
  */
class ReadSerial extends Module{
  
  val io = IO(new Bundle {
    val reset_n = Input(Uint(1.W))
    val rxd     = Input(Uint(1.W))
    val valid   = Output(Uint(1.W))
    val data    = Output(Uint(8.W))
    })


  // instanciation of modules
  val Controller    = Module(new Controller())
  val Counter       = Module(new Counter())
  val ShiftRegister = Module(new ShiftRegister())

  // connections between modules
  Controller.io.cnt_en   := Counter.io.cnt_en
  Counter.io.cnt_s       := Controller.io.cnt_s

  // global I/O 
  Controller.io.resset_n := reset_n
  Controller.io.rxd      := rxd
  Controller.io.valid    := valid
  ShiftRegister.io.rxd   := rxd
}

// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chisel3.util._


/** 
  * Half Adder Class 
  * 
  * Your task is to implement a basic half adder as presented in the lecture.
  * Each signal should only be one bit wide (inputs and outputs).
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class HalfAdder extends Module{
  
  val io = IO(new Bundle {
    /* Define IO ports of a half adder as presented in the lecture */
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val c = Output(UInt(1.W))
    })
  /* Describe output behaviour based on the input values */
  io.s := io.a ^ io.b
  io.c := io.a & io.b
}

/** 
  * Full Adder Class 
  * 
  * Your task is to implement a basic full adder. The component's behaviour should 
  * match the characteristics presented in the lecture. In addition, you are only allowed 
  * to use two half adders (use the class that you already implemented) and basic logic 
  * operators (AND, OR, ...).
  * Each signal should only be one bit wide (inputs and outputs).
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class FullAdder extends Module{

  val io = IO(new Bundle {
    /* Define IO ports of a half adder as presented in the lecture */
    val a     = Input(UInt(1.W))
    val b     = Input(UInt(1.W))
    val cin   = Input(UInt(1.W))
    val s     = Output(UInt(1.W))
    val carry = Output(UInt(1.W))

    })


  /* Instanciate the two half adders you want to use based on your HalfAdder class */
  val HalfAdder_1 = Module(new HalfAdder())
  val HalfAdder_2 = Module(new HalfAdder())

  /* Describe output behaviour based on the input values and the internal signals */
  // Input to first HalfAdder
  HalfAdder_1.io.a := io.a
  HalfAdder_1.io.b := io.b

  // Input to second HalfAdder
  HalfAdder_2.io.a := HalfAdder_1.io.s
  HalfAdder_2.io.b := io.cin

  // Output of FullAdder
  io.s     := HalfAdder_2.io.s
  io.carry := HalfAdder_2.io.c | HalfAdder_1.io.c
}

/** 
  * 4-bit Adder class 
  * 
  * Your task is to implement a 4-bit ripple-carry-adder. The component's behaviour should 
  * match the characteristics presented in the lecture.  Remember: An n-bit adder can be 
  * build using one half adder and n-1 full adders.
  * The inputs and the result should all be 4-bit wide, the carry-out only needs one bit.
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class FourBitAdder extends Module{

  val io = IO(new Bundle {
    /* Define IO ports of a 4-bit ripple-carry-adder as presented in the lecture */
    val a0  = Input(UInt(1.W))
    val b0  = Input(UInt(1.W))
    val a1  = Input(UInt(1.W))
    val b1  = Input(UInt(1.W))
    val a2  = Input(UInt(1.W))
    val b2  = Input(UInt(1.W))
    val a3  = Input(UInt(1.W))
    val b3  = Input(UInt(1.W))
    val s0  = Output(UInt(1.W))
    val s1  = Output(UInt(1.W))
    val s2  = Output(UInt(1.W))
    val s3  = Output(UInt(1.W))
    val c   = Output(UInt(1.W))
    })
  /* Instanciate the full adders and one half adder based on the previously defined classes */
  val fa1 = Module(new FullAdder())
  val fa2 = Module(new FullAdder())
  val fa3 = Module(new FullAdder())
  val ha  = Module(new HalfAdder())

  /* Describe output behaviour based on the input values and the internal */
  //HalfAdder inputs / outputs
  ha.io.a    := io.a0
  ha.io.b    := io.b0
  io.s0      := ha.io.s
  fa1.io.cin := ha.io.c

  //First FullAdder
  fa1.io.a   := io.a1
  fa1.io.b   := io.b1
  io.s1      := fa1.io.s
  fa2.io.cin := fa1.io.carry

  //Second FullAdder
  fa2.io.a   := io.a2
  fa2.io.b   := io.b2
  io.s2      := fa2.io.s
  fa3.io.cin := fa2.io.carry

  //Third FullAdder
  fa3.io.a := io.a3
  fa3.io.b := io.b3
  io.s3    := fa3.io.s
  io.c     := fa3.io.carry
}

package xodus.top

import chisel3._
import xodus.core.{Core},
       //xodus.memory.Memory,
       xodus.configs.Configs,
       xodus.io._


class Top extends Module with Configs {
  val io: TopIO = IO(new TopIO)

  // Modules
  val core: CoreIO   = Module(new Core).io
  //val iMem: MemoryIO = Module(new Memory).io


  /********************
   * Interconnections *
   ********************/

  //Seq(
  //  core.iMem -> iMem,
  //).map(
  //  x => x._2 <> x._1
  //)



  // Debug
  if (Debug) {
    io.pc.get <> core.pc.get
  }
  //dontTouch(core.iMem.req.addr.valid)
  //dontTouch(core.iMem.req.data.valid)
  //dontTouch(iMem.req.addr.valid)
  //dontTouch(iMem.req.data.valid)
}

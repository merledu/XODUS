package xodus.core.pipeline_regs

import chisel3._
import xodus.configs.Configs,
       xodus.core.decode_stage.{RegFileCtrl, DMemCtrl},
       xodus.core.execute_stage.ALUIO


class RegEMIO extends Bundle with Configs {
  val rd_addr      : UInt        = new RegDEIO().rd_addr
  val store_data   : SInt        = new RegDEIO().int_data(1)
  val reg_file_ctrl: RegFileCtrl = new RegDEIO().reg_file_ctrl
  val alu          : SInt        = new ALUIO().out
  val dmem_ctrl    : DMemCtrl    = new RegDEIO().dmem_ctrl
}


class RegEM extends Module {
  val io = IO(new Bundle {
    val in : RegEMIO = Flipped(new RegEMIO)

    val out: RegEMIO = new RegEMIO
  })


  // Pipeline
  genPipeline(Seq(
    io.in.rd_addr       -> io.out.rd_addr,
    io.in.rs2_data      -> io.out.rs2_data,
    io.in.reg_file_ctrl -> io.out.reg_file_ctrl,
    io.in.alu           -> io.out.alu,
    io.in.dmem_ctrl     -> io.out.dmem_ctrl
  ))
}
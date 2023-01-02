//package Core
//
//import chisel3._
//import FetchStage._
//import DecodeStage._
//import ExecuteStage._
//import MemoryStage._
//import WriteBackStage._
//import PipelineRegs._
//
//class CoreIO(PARAMS:Map[String, Int]) extends Bundle {
//        // Input ports
//        val inst  : UInt = Input(UInt(PARAMS("XLEN").W))
//        val dataIn: SInt = Input(SInt(PARAMS("XLEN").W))
//
//        // Output ports
//        //
//        // - Memory ports
//        val addr: Vec[UInt] = Output(Vec(2, UInt(PARAMS("MDEPTH").W)))
//
//        // - Data Memory ports
//        val dataOut: SInt      = Output(SInt(PARAMS("XLEN").W))
//        val en     : Vec[Bool] = Output(Vec(2, Bool()))
//
//        // - RVFI ports
//        val RegFDInst        : UInt = Output(UInt(PARAMS("XLEN").W))
//        val RegDA_rs1_addr   : UInt = Output(UInt(PARAMS("REGADDRLEN").W))
//        val RegDA_rs2_addr   : UInt = Output(UInt(PARAMS("REGADDRLEN").W))
//        val RegDA_rs1_data   : SInt = Output(SInt(PARAMS("XLEN").W))
//        val RegAM_rs2_data   : SInt = Output(SInt(PARAMS("XLEN").W))
//        val RegMW_wr_en      : Bool = Output(Bool())
//        val RegMW_rd_addr    : UInt = Output(UInt(PARAMS("REGADDRLEN").W))
//        val WriteBack_rd_data: SInt = Output(SInt(PARAMS("XLEN").W))
//        val RegDA_PC         : UInt = Output(UInt(PARAMS("XLEN").W))
//        val nPC              : UInt = Output(UInt(PARAMS("XLEN").W))
//        val RegAM_load_en    : Bool = Output(Bool())
//        val RegAM_str_en     : Bool = Output(Bool())
//        val RegAM_alu        : SInt = Output(SInt(PARAMS("XLEN").W))
//        
//        // - Hazard ports
//        val RegDA_stallControl: Bool = Output(Bool())
//}
//
//class Core(PARAMS:Map[String, Int], OPCODES:Map[String, Map[String, UInt]]) extends Module {
//        // Initializing modules and ports
//        val io           : CoreIO        = IO(new CoreIO(PARAMS))
//        val PC           : PC            = Module(new PC(PARAMS))
//        val InstMemRouter: InstMemRouter = Module(new InstMemRouter(PARAMS))
//        val RegFD        : RegFD         = Module(new RegFD(PARAMS))
//        val Decoder      : Decoder       = Module(new Decoder(PARAMS, OPCODES))
//        val RegFile      : RegFile       = Module(new RegFile)
//        val StallUnit    : StallUnit     = Module(new StallUnit(PARAMS))
//        val JumpUnit     : JumpUnit      = Module(new JumpUnit(PARAMS, OPCODES))
//        val RegDA        : RegDA         = Module(new RegDA)
//        val ControlUnit  : ControlUnit   = Module(new ControlUnit)
//        val ALU          : ALU           = Module(new ALU)
//        val ForwardUnit  : ForwardUnit   = Module(new ForwardUnit)
//        val RegAM        : RegAM         = Module(new RegAM)
//        val Memory       : Memory        = Module(new Memory(PARAMS))
//        val RegMW        : RegMW         = Module(new RegMW)
//        val WriteBack    : WriteBack     = Module(new WriteBack)
//
//        //val inst  : UInt = dontTouch(WireInit(io.inst))
//        val dataIn: SInt = dontTouch(WireInit(io.dataIn))
//        
//        Seq(
//                /*****************************/
//                /*        Fetch Stage        */
//                /*****************************/
//                
//                // PC
//                PC.io.pcControl(0), PC.io.pcIn(0), PC.io.pcControl(1), PC.io.pcIn(1), PC.io.pcControl(2),
//                PC.io.imm,          PC.io.pcIn(2), PC.io.pcControl(3), PC.io.pcIn(3), PC.io.pcControl(4),
//
//                // InstMemRouter
//                InstMemRouter.io.addrIn, InstMemRouter.io.stallEn(0), InstMemRouter.io.stallEn(1), InstMemRouter.io.instIn(0), InstMemRouter.io.instIn(1),
//                
//                // RegFD
//                RegFD.io.pcIn, RegFD.io.instIn, RegFD.io.pc4In, RegFD.io.stallUnitInst,
//                
//                /******************************/
//                /*        Decode Stage        */
//                /******************************/
//                
//                // Decoder
//                Decoder.io.inst,
//                
//                // RegFile
//                RegFile.io.rd_addr, RegFile.io.rd_data, RegFile.io.rs1_addr, RegFile.io.rs2_addr, RegFile.io.wr_en,
//                
//                // StallUnit
//                StallUnit.io.instIn, StallUnit.io.loadEn,  StallUnit.io.regDErdAddr, StallUnit.io.pcIn(0), StallUnit.io.pcIn(1),
//                StallUnit.io.rsAddr,
//                
//                // JumpUnit
//                JumpUnit.io.rs1_data,              JumpUnit.io.rs2_data,      JumpUnit.io.func3,             JumpUnit.io.b_id,       JumpUnit.io.opcode,
//                JumpUnit.io.alu,                   JumpUnit.io.RegAM_alu_out, JumpUnit.io.WriteBack_rd_data, JumpUnit.io.Memory_out, JumpUnit.io.forward_jump_operand1,
//                JumpUnit.io.forward_jump_operand2, JumpUnit.io.j_id,          JumpUnit.io.i_jalr_id,         JumpUnit.io.imm,
//                
//                // RegDA
//                RegDA.io.PC_in,            RegDA.io.opcode_in,           RegDA.io.rd_addr_in,          RegDA.io.func3_in,          RegDA.io.rs1_addr_in,
//                RegDA.io.rs1_data_in,      RegDA.io.rs2_addr_in,         RegDA.io.rs2_data_in,         RegDA.io.func7_in,          RegDA.io.imm_in,
//                RegDA.io.forward_operand1, RegDA.io.forward_operand2,    RegDA.io.RegAM_rd_data,       RegDA.io.WriteBack_rd_data, RegDA.io.stallControl_in,
//                RegDA.io.jal_en_in,        RegDA.io.forward_rs1_rd_data, RegDA.io.forward_rs2_rd_data, RegDA.io.jalr_en_in,
//                
//                /*******************************/
//                /*        Execute Stage        */
//                /*******************************/
//                
//                // Control Unit
//                ControlUnit.io.opcode,    ControlUnit.io.func3,        ControlUnit.io.func7,   ControlUnit.io.imm,  ControlUnit.io.r_id,
//                ControlUnit.io.i_math_id, ControlUnit.io.i_load_id,    ControlUnit.io.jalr_en, ControlUnit.io.s_id, ControlUnit.io.u_auipc_id,
//                ControlUnit.io.u_lui_id,  ControlUnit.io.stallControl, ControlUnit.io.jal_en,
//                
//                // ALU
//                ALU.io.rs1_data,                ALU.io.rs2_data,    ALU.io.imm,          ALU.io.imm_en,         ALU.io.addition_en,
//                ALU.io.shiftLeftLogical_en,     ALU.io.lessThan_en, ALU.io.lessThanU_en, ALU.io.XOR_en,         ALU.io.shiftRightLogical_en,
//                ALU.io.shiftRightArithmetic_en, ALU.io.OR_en,       ALU.io.AND_en,       ALU.io.subtraction_en, ALU.io.jalr_en,
//                ALU.io.jal_en,                  ALU.io.auipc_en,    ALU.io.lui_en,       ALU.io.PC,
//                
//                // ForwardUnit
//                ForwardUnit.io.RegDA_rs1_addr, ForwardUnit.io.RegDA_rs2_addr, ForwardUnit.io.RegAM_rd_addr, ForwardUnit.io.RegAM_wr_en, ForwardUnit.io.RegMW_rd_addr,
//                ForwardUnit.io.RegMW_wr_en,    ForwardUnit.io.RegDA_rd_addr,  ForwardUnit.io.rs1_addr,      ForwardUnit.io.rs2_addr,    ForwardUnit.io.b_en,
//                ForwardUnit.io.load_en,        ForwardUnit.io.RegAM_load_en,  ForwardUnit.io.RegMW_load_en, ForwardUnit.io.jalr_en,
//                
//                // RegAM
//                RegAM.io.alu_in,   RegAM.io.rd_addr_in, RegAM.io.wr_en_in,   RegAM.io.str_en_in,   RegAM.io.sb_en_in,
//                RegAM.io.sh_en_in, RegAM.io.sw_en_in,   RegAM.io.load_en_in, RegAM.io.lb_en_in,    RegAM.io.lh_en_in,
//                RegAM.io.lw_en_in, RegAM.io.lbu_en_in,  RegAM.io.lhu_en_in,  RegAM.io.rs2_data_in,
//                
//                /******************************/
//                /*        Memory Stage        */
//                /******************************/
//                
//                // Memory
//                Memory.io.alu_in, Memory.io.rs2_data, Memory.io.str_en, Memory.io.load_en, Memory.io.sb_en,
//                Memory.io.sh_en,  Memory.io.sw_en,    Memory.io.lb_en,  Memory.io.lh_en,   Memory.io.lw_en,
//                Memory.io.lbu_en, Memory.io.lhu_en,   Memory.io.dataIn,
//                
//                // RegMW
//                RegMW.io.alu_in, RegMW.io.mem_data_in, RegMW.io.rd_addr_in, RegMW.io.wr_en_in, RegMW.io.load_en_in,
//                
//                /*********************************/
//                /*        WriteBack Stage        */
//                /*********************************/
//                
//                // WriteBack
//                WriteBack.io.alu, WriteBack.io.mem_data, WriteBack.io.load_en,
//                
//                // Output ports
//                //
//                // - Instruction Memory ports
//                io.addr,
//
//                // - Data Memory ports
//                io.dataAddr, io.dataOut, io.storeEn, io.loadEn,
//
//                // - RVFI ports
//                io.RegFDInst,         io.RegDA_rs1_addr,    io.RegDA_rs2_addr, io.RegDA_rs1_data, io.RegAM_rs2_data,
//                io.RegMW_rd_addr,     io.WriteBack_rd_data, io.RegDA_PC,       io.nPC,            io.RegAM_load_en,
//                io.RegAM_str_en,      io.RegAM_alu,         io.RegMW_wr_en,
//                io.RegDA_stallControl
//        ) zip Seq(
//                /*****************************/
//                /*        Fetch Stage        */
//                /*****************************/
//                
//                // PC
//                StallUnit.io.stallEn, StallUnit.io.pcOut(1), JumpUnit.io.control(0), StallUnit.io.pcOut(0), JumpUnit.io.control(2),
//                Decoder.io.imm,       RegFD.io.pcOut(0),     JumpUnit.io.control(3), JumpUnit.io.jalrPC,    JumpUnit.io.control(4),
//
//                // InstMemRouter
//                PC.io.instAddr, StallUnit.io.stallEn, JumpUnit.io.control(4), io.inst, StallUnit.io.instOut,
//                
//                // RegFD
//                PC.io.pcOut(0), InstMemRouter.io.instOut, PC.io.pcOut(1), StallUnit.io.instOut,
//                
//                /******************************/
//                /*        Decode Stage        */
//                /******************************/
//
//                // Decoder
//                RegFD.io.instOut,
//                
//                // RegFile
//                RegMW.io.rd_addr_out, WriteBack.io.rd_data, Decoder.io.rsAddr(0), Decoder.io.rsAddr(1),  RegMW.io.wr_en_out,
//                
//                // StallUnit
//                RegFD.io.instOut,   ControlUnit.io.load_en, RegDA.io.rd_addr_out, RegFD.io.pc4Out, RegFD.io.pcOut,
//                Decoder.io.rsAddr,
//                
//                // JumpUnit
//                RegFile.io.rs1_data,                  RegFile.io.rs2_data, Decoder.io.func3,     OPCODES("B")("B"), Decoder.io.opcode,
//                ALU.io.out,                           RegAM.io.alu_out,    WriteBack.io.rd_data, Memory.io.out,   ForwardUnit.io.forward_jump_operand1,
//                ForwardUnit.io.forward_jump_operand2, OPCODES("J")("J"),   OPCODES("I")("jalr"), Decoder.io.imm,
//                
//                // RegDA
//                RegFD.io.pcOut,                 Decoder.io.opcode,                  Decoder.io.rdAddr,                 Decoder.io.func3,     Decoder.io.rsAddr(0),
//                RegFile.io.rs1_data,             Decoder.io.rsAddr(1),              RegFile.io.rs2_data,               Decoder.io.func7,     Decoder.io.imm,
//                ForwardUnit.io.forward_operand1, ForwardUnit.io.forward_operand2,    RegAM.io.alu_out,                   WriteBack.io.rd_data, StallUnit.io.stallControl,
//                JumpUnit.io.jal_en,              ForwardUnit.io.forward_rs1_rd_data, ForwardUnit.io.forward_rs2_rd_data, JumpUnit.io.jalr_en,
//                
//                /*******************************/
//                /*        Execute Stage        */
//                /*******************************/
//
//                // Control Unit
//                RegDA.io.opcode_out,  RegDA.io.func3_out,        RegDA.io.func7_out,   RegDA.io.imm_out, OPCODES("R")("math"),
//                OPCODES("I")("math"), OPCODES("I")("load"),      RegDA.io.jalr_en_out, OPCODES("S")("S"),  OPCODES("U")("auipc"),
//                OPCODES("U")("lui"),  RegDA.io.stallControl_out, RegDA.io.jal_en_out,
//                
//                // ALU
//                RegDA.io.rs1_data_out,                  RegDA.io.rs2_data_out,      RegDA.io.imm_out,            ControlUnit.io.imm_en,         ControlUnit.io.addition_en,
//                ControlUnit.io.shiftLeftLogical_en,     ControlUnit.io.lessThan_en, ControlUnit.io.lessThanU_en, ControlUnit.io.XOR_en,         ControlUnit.io.shiftRightLogical_en,
//                ControlUnit.io.shiftRightArithmetic_en, ControlUnit.io.OR_en,       ControlUnit.io.AND_en,       ControlUnit.io.subtraction_en, RegDA.io.jalr_en_out,
//                RegDA.io.jal_en_out,                    ControlUnit.io.auipc_en,    ControlUnit.io.lui_en,       RegDA.io.PC_out,
//                
//                // ForwardUnit
//                RegDA.io.rs1_addr_out,  RegDA.io.rs2_addr_out, RegAM.io.rd_addr_out, RegAM.io.wr_en_out,  RegMW.io.rd_addr_out,
//                RegMW.io.wr_en_out,     RegDA.io.rd_addr_out,  Decoder.io.rsAddr(0), Decoder.io.rsAddr(1), JumpUnit.io.b_en,
//                ControlUnit.io.load_en, RegAM.io.load_en_out,  RegMW.io.load_en_out, JumpUnit.io.jalr_en,
//                
//                // RegAM
//                ALU.io.out,           RegDA.io.rd_addr_out,  ControlUnit.io.wr_en,   ControlUnit.io.str_en, ControlUnit.io.sb_en,
//                ControlUnit.io.sh_en, ControlUnit.io.sw_en,  ControlUnit.io.load_en, ControlUnit.io.lb_en,  ControlUnit.io.lh_en,
//                ControlUnit.io.lw_en, ControlUnit.io.lbu_en, ControlUnit.io.lhu_en,  RegDA.io.rs2_data_out, 
//                
//                /******************************/
//                /*        Memory Stage        */
//                /******************************/
//
//                // Memory
//                RegAM.io.alu_out,    RegAM.io.rs2_data_out, RegAM.io.str_en_out, RegAM.io.load_en_out, RegAM.io.sb_en_out,
//                RegAM.io.sh_en_out,  RegAM.io.sw_en_out,    RegAM.io.lb_en_out,  RegAM.io.lh_en_out,   RegAM.io.lw_en_out,
//                RegAM.io.lbu_en_out, RegAM.io.lhu_en_out,   dataIn,
//                
//                // RegMW
//                RegAM.io.alu_out, Memory.io.out, RegAM.io.rd_addr_out, RegAM.io.wr_en_out, RegAM.io.load_en_out,
//                
//                /*********************************/
//                /*        WriteBack Stage        */
//                /*********************************/
//
//                // WriteBack
//                RegMW.io.alu_out, RegMW.io.mem_data_out, RegMW.io.load_en_out,
//                
//                // Output ports
//                //
//                // - Instruction Memory ports
//                PC.io.instAddr,
//
//                // - Data Memory ports
//                Memory.io.addrOut, Memory.io.dataOut, Memory.io.storeEn, Memory.io.loadEn,
//
//                // - RVFI ports
//                RegFD.io.instOut,         RegDA.io.rs1_addr_out, RegDA.io.rs2_addr_out, RegDA.io.rs1_data_out, RegAM.io.rs2_data_out,
//                RegMW.io.rd_addr_out,     WriteBack.io.rd_data,  RegDA.io.PC_out,       PC.io.pcOut(2),         RegAM.io.load_en_out,
//                RegAM.io.str_en_out,      RegAM.io.alu_out,      RegMW.io.wr_en_out,
//                RegDA.io.stallControl_out
//        ) foreach {
//                x => x._1 := x._2
//        }
//}

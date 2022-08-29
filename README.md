# Computer-architecture-project

## Description

A simulation for a Von Neumann based Computer Architecture using Java.

## Main Memory Architecture
A von neumann Architecture in which program data and instruction data are stored in the same memory
Main Memory Size 2048 * 32 from 0 to 1023 for instructions and from 1024 to 2047 for data

## Registers
 - 31 General purpose Registers from R1 to R31
 - 1 Zero Registers named R0
 - 1 program counter PC Registers Size 32 bits
 
## Instruction Architecture

| Name | Format | Operation |
| ------- | ---------------------------------- | ------------ |
| ADD     | ADD R1 R2 R3                       | R1 = R2 + R3 |
| SUB     | SUB R1 R2 R3                       | R1 = R2 - R3 |
| MUL     | MUL R1 R2 R3                       | R1 = R2 * R3 |
| MOVI    | MOVI R1 IMM                       | 	R1 = IMM |
| JEQ     | JEQ R1 R2 IMM\label                | IF(R1 == R2) {PC = PC+1+IMM\ PC = label } |
| AND     | AND R1 R2 R3                       | R1 = R2 & R3 |
| XORI    | XORI R1 R2 IMM	                   | R1 = R2 ⊕ IMM |
| JMP     | JMP ADDRESS                        | PC = PC[31:28] |
| LSL     | LSL R1 R2 SHAMT                    | R1 = R2 << SHAMT |
| LSR     | LSR R1 R2 SHAMT                    | R1 = R2 >>> SHAMT |
| MOVR    | 	MOVR R1 R2 IMM                   | R1 = MEM[R2 + IMM] |
| MOVM    | 	MOVM R1 R2 IMM                   | MEM[R2 + IMM] = R1 |

## Datapath
### Stages: 5
 - Instruction Fetch (IF)
 - Instruction Decode (ID)
 - Execute (EX)
 - Memory (MEM)
 - Write Back (WB)

**Pipeline**: maximum 4 instructions running in parallel (IF) and (MEM) cannot be done in parallel.

## Project Structure

![Screenshot from 2022-08-29 14-58-20](https://user-images.githubusercontent.com/75969308/187206652-432e79d7-441d-4c15-9bc5-8b1fd94ec36c.png)



## Features
 - Parser handles labels.
 - Parser handles comments.
 - A logger, not only outputs to the console but also outputs a log file named after the current time stamp.
 - We applied some spectacular design patterns such as:
  - Singleton
  - Observer
  - Command
  - Factory
  - Decorator
  
## Technologies

| Tecnology |
| -------   |
|  Java     |
|  Maven    |

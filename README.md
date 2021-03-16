# Example Sparkfun Alchitry CU

## Introduction

The aim of this repository is to try to implement SpinalHDL designs from scratch as possible going from a very simple one (LED blinking) to a more complex one (SoC with VexRiscv processor).

## Compilation

- `make all`: translate spinalHDL to verilog, synthetize, place and route.

- `make time`: show timing critical path. 

- `make clean`: clean up compilation result.

## Simple Blink Design 

This design makes one board LED to blink with randomly choosen on / off time.

```plantuml
@startuml
rectangle toplevel {
  rectangle blink
}
circle "LED[0]" as led
blink -> led

@enduml
```

## Apb Blink Design 

In this design I start to use a APB bus to set the LEDs state.

```plantuml
@startuml
rectangle toplevel {
  rectangle master
  rectangle "\n\napb\n\n" as bus
  rectangle gpio
}
circle "LED[0..7]" as leds
gpio -> leds
master -> bus 
bus -> gpio


@enduml
```

## License 

Refer to the [LICENSE](LICENSE) file.

## Useful Links

- [Schematic PDF](https://cdn.sparkfun.com/assets/2/6/e/5/e/alchitry_cu_sch_update.pdf)

- [Alchitry Loader for Windows](https://github.com/alchitry/alchitry-loader-gui/blob/master/build/work/alchitry-loader-1.0.0-windows.zip)


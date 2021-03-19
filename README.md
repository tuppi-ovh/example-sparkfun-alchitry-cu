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
rectangle SimpleBlinkDesign  {
  rectangle BlinkComp
}
circle "LED[0]" as led
BlinkComp -> led
@enduml
```

## Apb Blink Design 

In this design I start to use a APB bus to set the LEDs state.

```plantuml
@startuml
rectangle ApbBlinkDesign {
  rectangle MasterComp
  rectangle "\n\napb\nbus\n\n" as apb
  rectangle Apb3gpio as gpio
}
circle "LED[0..7]" as leds
gpio -> leds
MasterComp -> apb 
apb -> gpio
@enduml
```

## Hello World Design

In this design I connect UART to the APB bus with a simple state machine to handle commands.

```plantuml
@startuml
skinparam linetype ortho
rectangle HelloWorldDesign {
  rectangle MasterComp
  rectangle "\n\napb\nbus\n\n" as apb
  rectangle Apb3gpio as gpio
  rectangle Apb3Uart as uart
}
circle "LED[0..7]" as io_leds
circle "RX / TX" as io_uart

MasterComp -> apb 
apb -> gpio
gpio -> io_leds
apb <-> uart
uart <-> io_uart
gpio --[hidden] uart
@enduml
```

![image.png](http://www.plantuml.com/plantuml/png/POv1ImD138Nlyoj2xws21oyY5Ijuq8ie5hf5iZjXEvZD39dPWuZ_tSteMegNJ5w-bpTPvu9Qne5TVW-IK7403aBb8n54BOVebBg2qZF1FJ77rwZiDvH3B_3f08xu4NCXlOr3EXal6ca4Kzj8EsRh5u0Pfi69woQr5tqAqPYz_-0BQZ4uJg_xSbtGpi8VxZQxyzNgSbzJGtnZylcFFctX39wtCwkxxdWdBA_j471JiRv-RCN4d6NiQj9rsOgfEUtNlTmTWlSa-n_NciIF0ty3)


## License 

Refer to the [LICENSE](LICENSE) file.

## Useful Links

- [Schematic PDF](https://cdn.sparkfun.com/assets/2/6/e/5/e/alchitry_cu_sch_update.pdf)

- [Alchitry Loader for Windows](https://github.com/alchitry/alchitry-loader-gui/blob/master/build/work/alchitry-loader-1.0.0-windows.zip)

- [APB Buss Specification PDF](https://web.eecs.umich.edu/~prabal/teaching/eecs373-f12/readings/ARM_AMBA3_APB.pdf)
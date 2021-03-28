# Example Sparkfun Alchitry CU

## Introduction

The aim of this repository is to try to implement SpinalHDL designs from scratch as possible going from a very simple one (LED blinking) to a more complex one (SoC with VexRiscv processor).

## Compilation

- `make all`: translate spinalHDL to verilog, synthetize, place and route.

- `make time`: show timing critical path. 

- `make clean`: clean up compilation result.

## Simple Blink Design 

This design makes one board LED to blink with randomly choosen on / off time.

![image.png](http://www.plantuml.com/plantuml/png/SoWkIImgAStDuIfAJIv9p4lFILK8pimjo4broidCotP9BSvCprDGgEPIK40e04ivv-SMSDLo9MSM9GE5b7nSNQ8DOfKK4eiLSb9JkE1A57Jjm08EgNafG6i0)

## Apb Blink Design 

In this design I start to use a APB bus to set the LEDs state.

![image.png](http://www.plantuml.com/plantuml/png/LKyx3i8m3Drz2gix5H836ofGDZW1PKXRAuf8tIXE9yJTYSDGTKcUt-TPRYIPc9R9GwGX6RQUy1pwYtVyRacSPVm0uYe-Z2IArtaA6xhgk6CJ-exxHHHNQ0GpaLsRveCDRbPDV_Z2uEAW-VkjVUxh-lWgGK-ZG77kJlzXtPifBKR8Z-9Ir12F-OeV)

## Hello World Design

In this design I connect UART to the APB bus with a simple state machine to handle commands.

![image.png](http://www.plantuml.com/plantuml/png/POvFImGn3CNl_HHXxmk2Lv4LL_2WDqLXPv7EDCmKqxJqpq74xsxJSHo6lQH9VY-lxv2oZhauKkdJSj1HEo3BcBy2WezvyYhYa3MFXF28HFxDHp97J7Pa-5O0AtxMAMEyzovirhkEEzQXxxWlIVeTw0HriL7TXVvw3DOBa_S2ljQS0ajzrOyQR1p4_EdXUBewDqlhFmXD-aSlxykwtQWr7-nl9G2eMgHl7qgHOR4HTbFdTYpb3xPv5kzFapK6-JoB3iYcEFe5)


## License 

Refer to the [LICENSE](LICENSE) file.

## Useful Links

- [Schematic PDF](https://cdn.sparkfun.com/assets/2/6/e/5/e/alchitry_cu_sch_update.pdf)

- [APB Buss Specification PDF](https://web.eecs.umich.edu/~prabal/teaching/eecs373-f12/readings/ARM_AMBA3_APB.pdf)

- [Synthesis, placement and routing with IceStorm](http://www.clifford.at/icestorm/)

- [Alchitry Loader for Windows](https://github.com/alchitry/alchitry-loader-gui/blob/master/build/work/alchitry-loader-1.0.0-windows.zip)

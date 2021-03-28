
AVAILABLE_DESIGN = SimpleBlinkDesign ApbBlinkDesign HelloWorldDesign SramDesign

ifndef DESIGN
$(error DESIGN is not set. Choose one between $(AVAILABLE_DESIGN))
endif


bin/toplevel.v :
	mkdir -p bin
#	sbt "runMain $(DESIGN).$(DESIGN)Verilog"
	cp $(DESIGN).v bin/toplevel.v

bin/toplevel.blif : bin/toplevel.v
	yosys -v3 -p "synth_ice40 -top $(DESIGN) -blif bin/toplevel.blif" bin/toplevel.v

bin/toplevel.asc : toplevel.pcf bin/toplevel.blif
	arachne-pnr -p toplevel.pcf -d 8k --max-passes 600 -P cb132 bin/toplevel.blif -o bin/toplevel.asc

bin/toplevel.bin : bin/toplevel.asc
	icepack bin/toplevel.asc bin/toplevel.bin

all : bin/toplevel.bin

time: bin/toplevel.bin
	icetime -tmd hx8k bin/toplevel.asc

clean :
	rm -rf bin


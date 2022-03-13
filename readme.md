# Chisel Mill

A chisel RTL front-end only compile platform. It contains `rocket-chip` and ` boom` core and some peripheral IPs.
All in Scala.

Also `chisel3` and `firrtl` version are staled at the version right before SiFive close-sourced their `rocket-chip` generator,
and stop following main stream any more cause the high error cost in IC design.

We are using `mill` as our scala compilation driver instead of `sbt`.

To compile your first chisel module to verilog:

```
git clone git@github.com:KevinLiuTong/chiselmill.git
cd chiselmill
git submodule update --init
./mill --version
./mill integration.runMain "integration.chiselexampletop"
```

To generate rocket-chip and boom core:

```
./mill integration.runMain "integration.rocket"
./mill integration.runMain "integration.smallboom"

```

bsp install
```
mill mill.bsp.BSP/install
```
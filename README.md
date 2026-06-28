# AlphaSeries4j

Java port of the VB6 AlphaSeries source from `/opt/git/AlphaSeries-src`.

This repository is being built module by module. The current Java code ports the
clear utility and data helpers from:

- `Crypto.bas`
- `DataManager.bas`
- `MySQL.bas`
- `Filesystems.bas`

Large server and UI modules such as `Main.frm`, `Handling.bas`, and `Updater.frm`
still need exact behavior mapping.

## Build

```sh
mvn test
```

## Porting Notes

The VB6 source is partly reconstructed from decompiled output and keeps many
procedure names such as `Proc_3_0_6D2AF0`. Java classes preserve those names for
traceability while also exposing clearer helper methods where useful.

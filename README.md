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

## Local Database Setup

The repo includes `config.ini` configured for the local MariaDB/MySQL instance:

```ini
mySQL_host=127.0.0.1
mySQL_port=3306
mySQL_db=alphaseries
mySQL_username=root
mySQL_password=verysecret
```

Create the database with:

```sh
scripts/setup-database.sh
```

If you have a matching AlphaSeries SQL dump, import it with:

```sh
scripts/setup-database.sh /path/to/alphaseries.sql
```

The sibling repos under `/opt/git` contain Havana/Lisbon/Kepler/Holograph dumps,
but those schemas do not match the AlphaSeries table names used by this port.

## Porting Notes

The VB6 source is partly reconstructed from decompiled output and keeps many
procedure names such as `Proc_3_0_6D2AF0`. Java classes preserve those names for
traceability while also exposing clearer helper methods where useful.

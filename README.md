# ZoneMenu

## About
Is a Spigot plugin which provides an easy user interface wrapper for WorldGuard.

This plugin requires [WorldEdit](https://github.com/sk89q/WorldEdit) and [WorldGuard](https://github.com/sk89q/WorldGuard)!

## Installation
Head over to the [latest release](https://github.com/joestrhq/ZoneMenu/releases/tag/v0.2.0).

Download the `jar` file (currently `zonemenu-0.2.0-SNAPSHOT-shaded.jar`).

Drop the downloaded `jar` file into your Spigot `plugins` folder.

Stop your Spigot server and start it again.

## Build
To build the project you need at least a Java Development Kit (JDK) in version 17 and Maven 3 installed.  

At first get a copy of the source code. Preferrably via `git clone https://github.com/joestrhq/ZoneMenu.git`.  

Initiate a build with `mvn -Dgpg.skip=true clean package`.  

The compiled plugin (`zonemenu-X.X.X-shaded.jar`) will be available in the `target` folder.

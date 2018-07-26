# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [Unreleased]

##  Added
* `Monad#For` instance method
* `Numbers#range` static method
* `Try#get`, obtains valule within try.
* `P1`, predicate of arity one.
* `... data.list.Functions` first class functions for FLists.

### Chnaged
* **Breaking Change**: Reorganized package structure.
* **Breaking Change**: Changed `Traversable#traverse` -> `Traversable#mapA`.
* Using AbstractFactory pattern to represent Applicative and Monad typelcasses
* **Breaking Change**: Changed `MonadUnit` -> `MonadFactory`.
* **Breaking Change**: Changed `ApplicativePure` -> `ApplicativeFactory`.

## 0.1 - 2018-07-05

### Added
* `C1` a version of `java.util.Consumer`
* `Either#visit` and `Either#consume`

### Changed
* **Breaking Change**: Removed `Witness` interface.
* **Breaking Change**: Reorganized package structure.
* **Breaking Change**: Function interfaces no longer instances of `Functor`.

### Fixed
* `Try` recoverWith error corrected.

### Fixed

## 0.0.1-SNAPSHOT - 2018-06-26
###  Added

* early implementation of jfunc.

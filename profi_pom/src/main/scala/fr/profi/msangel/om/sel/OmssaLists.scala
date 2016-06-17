package fr.profi.msangel.om.sel

object OmssaLists {

  val searchTypes = Map(
    "monoisotopic" -> 0,
    "average" -> 1,
    "monon15" -> 2,
    "exact" -> 3,
    "multiisotope" -> 4,
    "max" -> 5,
    "" -> 0 /* default */ )

  val ionTypes = Map(
    "a" -> 0,
    "b" -> 1,
    "c" -> 2,
    "x" -> 3,
    "y" -> 4,
    "z" -> 5,
    "parent" -> 6,
    "internal" -> 7,
    "immonium" -> 8,
    "unknown" -> 9,
    "adot" -> 10,
    "x-CO2" -> 11,
    "adot-CO2" -> 12,
    "max" -> 13,
    "" -> 1 /* default */ )

  val enzymes = Map(
    "trypsin" -> 0,
    "argc" -> 1,
    "cnbr" -> 2,
    "chymotrypsin" -> 3,
    "formicacid" -> 4,
    "lysc" -> 5,
    "lysc-p" -> 6,
    "pepsin-a" -> 7,
    "tryp-cnbr" -> 8,
    "tryp-chymo" -> 9,
    "trypsin-p" -> 10,
    "whole-protein" -> 11,
    "aspn" -> 12,
    "gluc" -> 13,
    "aspngluc" -> 14,
    "top-down" -> 15,
    "semi-tryptic" -> 16,
    "no-enzyme" -> 17,
    "chymotrypsin-p" -> 18,
    "aspn-de" -> 19,
    "gluc-de" -> 20,
    "lysn" -> 21,
    "thermolysin-p" -> 22,
    "semi-chymotrypsin" -> 23,
    "semi-gluc" -> 24,
    "max" -> 25,
    "none" -> 255,
    "" -> 0 /* default */ )

  val calcPlusOne = Map(
    "dontcalc" -> 0,
    "calc" -> 1,
    "" -> 1 /* default */ )

  val calcCharge = Map(
    "calculate" -> 0,
    "usefile" -> 1,
    "userange" -> 2,
    "" -> 2 /* default */ )
}
package us.alphakilo;

import java.util.AbstractMap;
import java.util.Map;

public class Constants {

    public static Map<Integer, String> ITEM_QUALITIES = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(0, "Worn"),
            new AbstractMap.SimpleEntry<>(1, "Normal"),
            new AbstractMap.SimpleEntry<>(2, "Fine"),
            new AbstractMap.SimpleEntry<>(3, "Superior"),
            new AbstractMap.SimpleEntry<>(4, "Epic"),
            new AbstractMap.SimpleEntry<>(5, "Legendary"),
            new AbstractMap.SimpleEntry<>(6, "Mythic")
    );

    public static  Map<Integer, String> TRAITS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(18, "Armor Divines"),
            new AbstractMap.SimpleEntry<>(12, "Armor Impenetrable"),
            new AbstractMap.SimpleEntry<>(16, "Armor Infused"),
            new AbstractMap.SimpleEntry<>(20, "Armor Intricate"),
            new AbstractMap.SimpleEntry<>(25, "Armor Nirnhoned"),
            new AbstractMap.SimpleEntry<>(19, "Armor Ornate"),
            new AbstractMap.SimpleEntry<>(17, "Armor Prosperous"),
            new AbstractMap.SimpleEntry<>(13, "Armor Reinforced"),
            new AbstractMap.SimpleEntry<>(11, "Armor Sturdy"),
            new AbstractMap.SimpleEntry<>(15, "Armor Training"),
            new AbstractMap.SimpleEntry<>(14, "Armor Well Fitted"),
            new AbstractMap.SimpleEntry<>(22, "Jewelry Arcane"),
            new AbstractMap.SimpleEntry<>(31, "Jewelry Bloodthirsty"),
            new AbstractMap.SimpleEntry<>(29, "Jewelry Harmony"),
            new AbstractMap.SimpleEntry<>(21, "Jewelry Healthy"),
            new AbstractMap.SimpleEntry<>(33, "Jewelry Infused"),
            new AbstractMap.SimpleEntry<>(27, "Jewelry Intricate"),
            new AbstractMap.SimpleEntry<>(24, "Jewelry Ornate"),
            new AbstractMap.SimpleEntry<>(32, "Jewelry Protective"),
            new AbstractMap.SimpleEntry<>(23, "Jewelry Robust"),
            new AbstractMap.SimpleEntry<>(28, "Jewelry Swift"),
            new AbstractMap.SimpleEntry<>(30, "Jewelry Triune"),
            new AbstractMap.SimpleEntry<>(2, "Weapon Charged"),
            new AbstractMap.SimpleEntry<>(8, "Weapon Decisive"),
            new AbstractMap.SimpleEntry<>(5, "Weapon Defending"),
            new AbstractMap.SimpleEntry<>(4, "Weapon Infused"),
            new AbstractMap.SimpleEntry<>(9, "Weapon Intricate"),
            new AbstractMap.SimpleEntry<>(26, "Weapon Nirnhoned"),
            new AbstractMap.SimpleEntry<>(10, "Weapon Ornate"),
            new AbstractMap.SimpleEntry<>(1, "Weapon Powered"),
            new AbstractMap.SimpleEntry<>(3, "Weapon Precise"),
            new AbstractMap.SimpleEntry<>(7, "Weapon Sharpened"),
            new AbstractMap.SimpleEntry<>(6, "Weapon Training")
    );


}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.commons.collections4.prefixmap.speedtest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PrefixMapSpeedTestData {

    static final Map<String, String> PREFIX_MAP = new HashMap<>(500);
    static final Map<String, String> FULL_PREFIX_MAP = new HashMap<>(20000);

    // This data was collected as part of the Apache 2.0 licensed project
    // Yauaa: Yet Another UserAgent Analyzer ( https://yauaa.basjes.nl/ )

    static {
        PREFIX_MAP.put("Acer",                    "Acer");
        PREFIX_MAP.put("AKAI",                    "AKAI");
        PREFIX_MAP.put("Alcatel",                 "Alcatel");
        PREFIX_MAP.put("Amazon",                  "Amazon");
        PREFIX_MAP.put("Archos",                  "Archos");
        PREFIX_MAP.put("Asus",                    "Asus");
        PREFIX_MAP.put("Avea",                    "Avea");
        PREFIX_MAP.put("Avvio",                   "Avvio");
        PREFIX_MAP.put("Azumi",                   "Azumi");
        PREFIX_MAP.put("BlackBerry",              "BlackBerry");
        PREFIX_MAP.put("Coolpad",                 "Coolpad");
        PREFIX_MAP.put("Cubot",                   "Cubot");
        PREFIX_MAP.put("HP",                      "HP");
        PREFIX_MAP.put("HTC",                     "HTC");
        PREFIX_MAP.put("Huawei",                  "Huawei");
        PREFIX_MAP.put("Lenovo",                  "Lenovo");
        PREFIX_MAP.put("LG",                      "LG");
        PREFIX_MAP.put("Meizu",                   "Meizu");
        PREFIX_MAP.put("Nokia",                   "Nokia");
        PREFIX_MAP.put("OnePlus",                 "OnePlus");
        PREFIX_MAP.put("Oppo",                    "Oppo");
        PREFIX_MAP.put("Panasonic",               "Panasonic");
        PREFIX_MAP.put("Philips",                 "Philips");
        PREFIX_MAP.put("Samsung",                 "Samsung");
        PREFIX_MAP.put("SonyEricsson",            "SonyEricsson");
        PREFIX_MAP.put("Sony",                    "Sony");
        PREFIX_MAP.put("SpreadTrum",              "SpreadTrum");
        PREFIX_MAP.put("Vivo",                    "Vivo");

        PREFIX_MAP.put("ONE TOUCH",               "Alcatel");
        PREFIX_MAP.put("ONETOUCH",                "Alcatel");
        PREFIX_MAP.put("AM-H",                    "Alcatel");
        PREFIX_MAP.put("S4035",                   "Alcatel");
        PREFIX_MAP.put("DIGICELD",                "Alcatel");
        PREFIX_MAP.put("I21",                     "Alcatel");
        PREFIX_MAP.put("A46",                     "Alcatel");
        PREFIX_MAP.put("400",                     "Alcatel");
        PREFIX_MAP.put("401",                     "Alcatel");
        PREFIX_MAP.put("402",                     "Alcatel");
        PREFIX_MAP.put("403",                     "Alcatel");
        PREFIX_MAP.put("404",                     "Alcatel");
        PREFIX_MAP.put("501",                     "Alcatel");
        PREFIX_MAP.put("502",                     "Alcatel");
        PREFIX_MAP.put("503",                     "Alcatel");
        PREFIX_MAP.put("504",                     "Alcatel");
        PREFIX_MAP.put("505",                     "Alcatel");
        PREFIX_MAP.put("506",                     "Alcatel");
        PREFIX_MAP.put("507",                     "Alcatel");
        PREFIX_MAP.put("508",                     "Alcatel");
        PREFIX_MAP.put("509",                     "Alcatel");
        PREFIX_MAP.put("510",                     "Alcatel");
        PREFIX_MAP.put("511",                     "Alcatel");
        PREFIX_MAP.put("601",                     "Alcatel");
        PREFIX_MAP.put("602",                     "Alcatel");
        PREFIX_MAP.put("603",                     "Alcatel");
        PREFIX_MAP.put("604",                     "Alcatel");
        PREFIX_MAP.put("605",                     "Alcatel");
        PREFIX_MAP.put("606",                     "Alcatel");
        PREFIX_MAP.put("607",                     "Alcatel");
        PREFIX_MAP.put("805",                     "Alcatel");
        PREFIX_MAP.put("806",                     "Alcatel");
        PREFIX_MAP.put("807",                     "Alcatel");
        PREFIX_MAP.put("900",                     "Alcatel");
        PREFIX_MAP.put("901",                     "Alcatel");
        PREFIX_MAP.put("902",                     "Alcatel");

        PREFIX_MAP.put("Aspire",                  "Acer");
        PREFIX_MAP.put("Liquid",                  "Acer");

        PREFIX_MAP.put("AG ",                     "AG Mobile");

        PREFIX_MAP.put("Fire ",                   "Amazon");
        PREFIX_MAP.put("Kindle",                  "Amazon");

        PREFIX_MAP.put("K00",                     "Asus");
        PREFIX_MAP.put("K01",                     "Asus");
        PREFIX_MAP.put("ME1",                     "Asus");
        PREFIX_MAP.put("ME3",                     "Asus");
        PREFIX_MAP.put("P00",                     "Asus");
        PREFIX_MAP.put("P01",                     "Asus");
        PREFIX_MAP.put("P02",                     "Asus");
        PREFIX_MAP.put("PadFone",                 "Asus");
        PREFIX_MAP.put("Transformer",             "Asus");
        PREFIX_MAP.put("Z00D",                    "Asus");
        PREFIX_MAP.put("ZB5",                     "Asus");
        PREFIX_MAP.put("ZC5",                     "Asus");
        PREFIX_MAP.put("ZE5",                     "Asus");

        PREFIX_MAP.put("BBA",                     "BlackBerry");
        PREFIX_MAP.put("BBB",                     "BlackBerry");
        PREFIX_MAP.put("BBC",                     "BlackBerry");
        PREFIX_MAP.put("BBD",                     "BlackBerry");
        PREFIX_MAP.put("BBE",                     "BlackBerry");
        PREFIX_MAP.put("BBF",                     "BlackBerry");
        PREFIX_MAP.put("BBG",                     "BlackBerry");
        PREFIX_MAP.put("BBH",                     "BlackBerry");
        PREFIX_MAP.put("Leap",                    "BlackBerry");
        PREFIX_MAP.put("Passport ",               "BlackBerry");
        PREFIX_MAP.put("PlayBook",                "BlackBerry");
        PREFIX_MAP.put("Q10",                     "BlackBerry");
        PREFIX_MAP.put("Q5",                      "BlackBerry");
        PREFIX_MAP.put("SQW",                     "BlackBerry");
        PREFIX_MAP.put("STH",                     "BlackBerry");
        PREFIX_MAP.put("STJ",                     "BlackBerry");
        PREFIX_MAP.put("STL",                     "BlackBerry");
        PREFIX_MAP.put("STV",                     "BlackBerry");
        PREFIX_MAP.put("Vienna",                  "BlackBerry");
        PREFIX_MAP.put("Z10",                     "BlackBerry");
        PREFIX_MAP.put("Z20",                     "BlackBerry");
        PREFIX_MAP.put("Z30",                     "BlackBerry");

        PREFIX_MAP.put("Chromecast",              "Google");
        PREFIX_MAP.put("Nexus",                   "Google");
        PREFIX_MAP.put("Pixelbook",               "Google");

        PREFIX_MAP.put("Hi 1",                    "Hisense");
        PREFIX_MAP.put("Hi 2",                    "Hisense");
        PREFIX_MAP.put("Hi 3",                    "Hisense");
        PREFIX_MAP.put("HI98",                    "Hisense");
        PREFIX_MAP.put("HITV",                    "Hisense");
        PREFIX_MAP.put("HLTE",                    "Hisense");
        PREFIX_MAP.put("HS-",                     "Hisense");

        PREFIX_MAP.put("HT70",                    "Homtom");

        PREFIX_MAP.put("Apache",                  "HTC");
        PREFIX_MAP.put("Artist",                  "HTC");
        PREFIX_MAP.put("Bahamas",                 "HTC");
        PREFIX_MAP.put("Bravo",                   "HTC");
        PREFIX_MAP.put("Breeze",                  "HTC");
        PREFIX_MAP.put("Butterfly",               "HTC");
        PREFIX_MAP.put("Buzz",                    "HTC");
        PREFIX_MAP.put("Desire",                  "HTC");
        PREFIX_MAP.put("DLX",                     "HTC");
        PREFIX_MAP.put("Dream",                   "HTC");
        PREFIX_MAP.put("Endeavour",               "HTC");
        PREFIX_MAP.put("Evo",                     "HTC");
        PREFIX_MAP.put("Gemini",                  "HTC");
        PREFIX_MAP.put("Glacier",                 "HTC");
        PREFIX_MAP.put("Golf",                    "HTC");
        PREFIX_MAP.put("Hermes",                  "HTC");
        PREFIX_MAP.put("Hero",                    "HTC");
        PREFIX_MAP.put("HuaShan",                 "HTC");
        PREFIX_MAP.put("Inspire HD",              "HTC");
        PREFIX_MAP.put("Leo 70",                  "HTC");
        PREFIX_MAP.put("Liberty",                 "HTC");
        PREFIX_MAP.put("Magic",                   "HTC");
        PREFIX_MAP.put("Marvel",                  "HTC");
        PREFIX_MAP.put("Master",                  "HTC");
        PREFIX_MAP.put("MTeoR",                   "HTC");
        PREFIX_MAP.put("Pico",                    "HTC");
        PREFIX_MAP.put("Polaris",                 "HTC");
        PREFIX_MAP.put("Primo",                   "HTC");
        PREFIX_MAP.put("Prophet",                 "HTC");
        PREFIX_MAP.put("Radiant",                 "HTC");
        PREFIX_MAP.put("Rio",                     "HTC");
        PREFIX_MAP.put("Rome",                    "HTC");
        PREFIX_MAP.put("Runnymede",               "HTC");
        PREFIX_MAP.put("Schubert",                "HTC");
        PREFIX_MAP.put("SongShan",                "HTC");
        PREFIX_MAP.put("Spark",                   "HTC");
        PREFIX_MAP.put("Tiara",                   "HTC");
        PREFIX_MAP.put("U Play",                  "HTC");
        PREFIX_MAP.put("U Ultra",                 "HTC");
        PREFIX_MAP.put("Velocity",                "HTC");
        PREFIX_MAP.put("Vertex",                  "HTC");
        PREFIX_MAP.put("Vision",                  "HTC");
        PREFIX_MAP.put("Wizard",                  "HTC");

        PREFIX_MAP.put("7D-",                     "Huawei");
        PREFIX_MAP.put("AGS2-",                   "Huawei");
        PREFIX_MAP.put("AGS-",                    "Huawei");
        PREFIX_MAP.put("ALE-",                    "Huawei");
        PREFIX_MAP.put("ALP-",                    "Huawei");
        PREFIX_MAP.put("ANE-",                    "Huawei");
        PREFIX_MAP.put("ARE-",                    "Huawei");
        PREFIX_MAP.put("Ascend",                  "Huawei");
        PREFIX_MAP.put("ATH-",                    "Huawei");
        PREFIX_MAP.put("ATU-",                    "Huawei");
        PREFIX_MAP.put("AUM-",                    "Huawei");
        PREFIX_MAP.put("BAC-",                    "Huawei");
        PREFIX_MAP.put("BAH2-",                   "Huawei");
        PREFIX_MAP.put("BAH-",                    "Huawei");
        PREFIX_MAP.put("BAZ-",                    "Huawei");
        PREFIX_MAP.put("BG2-",                    "Huawei");
        PREFIX_MAP.put("BGO-",                    "Huawei");
        PREFIX_MAP.put("BKL-",                    "Huawei");
        PREFIX_MAP.put("BLA-",                    "Huawei");
        PREFIX_MAP.put("BLL-",                    "Huawei");
        PREFIX_MAP.put("BLN-",                    "Huawei");
        PREFIX_MAP.put("BND-",                    "Huawei");
        PREFIX_MAP.put("BTV-",                    "Huawei");
        PREFIX_MAP.put("BZA-",                    "Huawei");
        PREFIX_MAP.put("BZK-",                    "Huawei");
        PREFIX_MAP.put("CAG-",                    "Huawei");
        PREFIX_MAP.put("CAM-",                    "Huawei");
        PREFIX_MAP.put("CAN-",                    "Huawei");
        PREFIX_MAP.put("CAZ-",                    "Huawei");
        PREFIX_MAP.put("CHC-",                    "Huawei");
        PREFIX_MAP.put("Che1-",                   "Huawei");
        PREFIX_MAP.put("Che2-",                   "Huawei");
        PREFIX_MAP.put("CHE-",                    "Huawei");
        PREFIX_MAP.put("CHM-",                    "Huawei");
        PREFIX_MAP.put("CLT-",                    "Huawei");
        PREFIX_MAP.put("CMR-",                    "Huawei");
        PREFIX_MAP.put("COL-",                    "Huawei");
        PREFIX_MAP.put("COR-",                    "Huawei");
        PREFIX_MAP.put("CPN-",                    "Huawei");
        PREFIX_MAP.put("CRO-",                    "Huawei");
        PREFIX_MAP.put("CRR-",                    "Huawei");
        PREFIX_MAP.put("CUN-",                    "Huawei");
        PREFIX_MAP.put("D2-",                     "Huawei");
        PREFIX_MAP.put("DAV-",                    "Huawei");
        PREFIX_MAP.put("DIG-",                    "Huawei");
        PREFIX_MAP.put("DLI-",                    "Huawei");
        PREFIX_MAP.put("DRA-",                    "Huawei");
        PREFIX_MAP.put("DUA-",                    "Huawei");
        PREFIX_MAP.put("DUK-",                    "Huawei");
        PREFIX_MAP.put("EDI-",                    "Huawei");
        PREFIX_MAP.put("EML-",                    "Huawei");
        PREFIX_MAP.put("EVA-",                    "Huawei");
        PREFIX_MAP.put("FDR-",                    "Huawei");
        PREFIX_MAP.put("FIG-",                    "Huawei");
        PREFIX_MAP.put("FLA-",                    "Huawei");
        PREFIX_MAP.put("FRD-",                    "Huawei");
        PREFIX_MAP.put("G6",                      "Huawei");
        PREFIX_MAP.put("G7",                      "Huawei");
        PREFIX_MAP.put("GEM-",                    "Huawei");
        PREFIX_MAP.put("GRA-",                    "Huawei");
        PREFIX_MAP.put("H30-",                    "Huawei");
        PREFIX_MAP.put("H60-",                    "Huawei");
        PREFIX_MAP.put("HDN-",                    "Huawei");
        PREFIX_MAP.put("HMA-",                    "Huawei");
        PREFIX_MAP.put("HN3-",                    "Huawei");
        PREFIX_MAP.put("Hol-",                    "Huawei");
        PREFIX_MAP.put("Honor",                   "Huawei");
        PREFIX_MAP.put("HW-",                     "Huawei");
        PREFIX_MAP.put("HWI-",                    "Huawei");
        PREFIX_MAP.put("HZ-",                     "Huawei");
        PREFIX_MAP.put("INE-",                    "Huawei");
        PREFIX_MAP.put("JDN-",                    "Huawei");
        PREFIX_MAP.put("JKM-",                    "Huawei");
        PREFIX_MAP.put("JMM-",                    "Huawei");
        PREFIX_MAP.put("JSN-",                    "Huawei");
        PREFIX_MAP.put("K1-",                     "Huawei");
        PREFIX_MAP.put("K2-",                     "Huawei");
        PREFIX_MAP.put("KII-",                    "Huawei");
        PREFIX_MAP.put("KIW-",                    "Huawei");
        PREFIX_MAP.put("KNT-",                    "Huawei");
        PREFIX_MAP.put("KOB-",                    "Huawei");
        PREFIX_MAP.put("LDN-",                    "Huawei");
        PREFIX_MAP.put("LEO-",                    "Huawei");
        PREFIX_MAP.put("LLD-",                    "Huawei");
        PREFIX_MAP.put("LND-",                    "Huawei");
        PREFIX_MAP.put("LON-",                    "Huawei");
        PREFIX_MAP.put("LUA-",                    "Huawei");
        PREFIX_MAP.put("LYA-",                    "Huawei");
        PREFIX_MAP.put("LYO-",                    "Huawei");
        PREFIX_MAP.put("M2-",                     "Huawei");
        PREFIX_MAP.put("MediaPad",                "Huawei");
        PREFIX_MAP.put("MHA-",                    "Huawei");
        PREFIX_MAP.put("MLA-",                    "Huawei");
        PREFIX_MAP.put("MT1-",                    "Huawei");
        PREFIX_MAP.put("MT2-",                    "Huawei");
        PREFIX_MAP.put("MT7-",                    "Huawei");
        PREFIX_MAP.put("MYA-",                    "Huawei");
        PREFIX_MAP.put("NCE-",                    "Huawei");
        PREFIX_MAP.put("NEM-",                    "Huawei");
        PREFIX_MAP.put("NEO-",                    "Huawei");
        PREFIX_MAP.put("NMO-",                    "Huawei");
        PREFIX_MAP.put("NXT-",                    "Huawei");
        PREFIX_MAP.put("P2-",                     "Huawei");
        PREFIX_MAP.put("P6-",                     "Huawei");
        PREFIX_MAP.put("P7-",                     "Huawei");
        PREFIX_MAP.put("PAR-",                    "Huawei");
        PREFIX_MAP.put("PE-",                     "Huawei");
        PREFIX_MAP.put("PIC-",                    "Huawei");
        PREFIX_MAP.put("PLE-",                    "Huawei");
        PREFIX_MAP.put("PLK-",                    "Huawei");
        PREFIX_MAP.put("PLK",                     "Huawei");
        PREFIX_MAP.put("PRA-",                    "Huawei");
        PREFIX_MAP.put("RIO-",                    "Huawei");
        PREFIX_MAP.put("RNE-",                    "Huawei");
        PREFIX_MAP.put("RVL-",                    "Huawei");
        PREFIX_MAP.put("s10-",                    "Huawei");
        PREFIX_MAP.put("S10-",                    "Huawei");
        PREFIX_MAP.put("S7-",                     "Huawei");
        PREFIX_MAP.put("S8-",                     "Huawei");
        PREFIX_MAP.put("SCC-",                    "Huawei");
        PREFIX_MAP.put("SC-",                     "Huawei");
        PREFIX_MAP.put("SCL-",                    "Huawei");
        PREFIX_MAP.put("SCU-",                    "Huawei");
        PREFIX_MAP.put("SHT-",                    "Huawei");
        PREFIX_MAP.put("SLA-",                    "Huawei");
        PREFIX_MAP.put("SNE-",                    "Huawei");
        PREFIX_MAP.put("STF-",                    "Huawei");
        PREFIX_MAP.put("SXX-",                    "Huawei");
        PREFIX_MAP.put("T1-",                     "Huawei");
        PREFIX_MAP.put("TAG-",                    "Huawei");
        PREFIX_MAP.put("TIT-",                    "Huawei");
        PREFIX_MAP.put("TRT-",                    "Huawei");
        PREFIX_MAP.put("VAT-",                    "Huawei");
        PREFIX_MAP.put("VEN-",                    "Huawei");
        PREFIX_MAP.put("VIE-",                    "Huawei");
        PREFIX_MAP.put("VKY-",                    "Huawei");
        PREFIX_MAP.put("VNS-",                    "Huawei");
        PREFIX_MAP.put("VTR-",                    "Huawei");
        PREFIX_MAP.put("W1-",                     "Huawei");
        PREFIX_MAP.put("W2-",                     "Huawei");
        PREFIX_MAP.put("WAS-",                    "Huawei");

        PREFIX_MAP.put("Arc 10",                  "Kobo");
        PREFIX_MAP.put("Arc 6",                   "Kobo");
        PREFIX_MAP.put("Arc 7",                   "Kobo");
        PREFIX_MAP.put("Arc 8",                   "Kobo");
        PREFIX_MAP.put("Arc 9",                   "Kobo");

        PREFIX_MAP.put("ThinkPad",                "Lenovo");
        PREFIX_MAP.put("Yoga",                    "Lenovo");

        PREFIX_MAP.put("G Watch",                 "LG");
        PREFIX_MAP.put("LG-",                     "LG");
        PREFIX_MAP.put("402LG",                   "LG");
        PREFIX_MAP.put("AS98",                    "LG");
        PREFIX_MAP.put("DM-01",                   "LG");
        PREFIX_MAP.put("L-01",                    "LG");
        PREFIX_MAP.put("RS98",                    "LG");
        PREFIX_MAP.put("VK",                      "LG");
        PREFIX_MAP.put("VS",                      "LG");

        PREFIX_MAP.put("MID0",                    "Manta");
        PREFIX_MAP.put("MID1",                    "Manta");
        PREFIX_MAP.put("MID2",                    "Manta");
        PREFIX_MAP.put("MID3",                    "Manta");
        PREFIX_MAP.put("MID4",                    "Manta");
        PREFIX_MAP.put("MID5",                    "Manta");
        PREFIX_MAP.put("MID6",                    "Manta");
        PREFIX_MAP.put("MID7",                    "Manta");
        PREFIX_MAP.put("MID8",                    "Manta");
        PREFIX_MAP.put("MID9",                    "Manta");

        PREFIX_MAP.put("MZ-",                     "Meizu");
        PREFIX_MAP.put("M3",                      "Meizu");
        PREFIX_MAP.put("M5",                      "Meizu");
        PREFIX_MAP.put("MX",                      "Meizu");

        PREFIX_MAP.put("Surface",                 "Microsoft");
        PREFIX_MAP.put("XBOX",                    "Microsoft");
        PREFIX_MAP.put("Zune HD",                 "Microsoft");

        PREFIX_MAP.put("Trium",                   "Mitsubishi");

        PREFIX_MAP.put("Moto",                    "Motorola");
        PREFIX_MAP.put("Xoom",                    "Motorola");
        PREFIX_MAP.put("XT",                      "Motorola");
        PREFIX_MAP.put("Z2",                      "Motorola");

        PREFIX_MAP.put("MP",                      "Mpman");

        PREFIX_MAP.put("MS50L",                   "Multilaser");

        PREFIX_MAP.put("Lumia",                   "Nokia");
        PREFIX_MAP.put("RM-",                     "Nokia");
        PREFIX_MAP.put("TA-",                     "Nokia");

        PREFIX_MAP.put("SHIELD",                  "Nvidia");

        PREFIX_MAP.put("ELEMENT10",               "Odys");

        PREFIX_MAP.put("A0001",                   "OnePlus");
        PREFIX_MAP.put("A1001",                   "OnePlus");
        PREFIX_MAP.put("A2001",                   "OnePlus");
        PREFIX_MAP.put("A2003",                   "OnePlus");
        PREFIX_MAP.put("A2005",                   "OnePlus");
        PREFIX_MAP.put("A3000",                   "OnePlus");
        PREFIX_MAP.put("A3003",                   "OnePlus");
        PREFIX_MAP.put("A3010",                   "OnePlus");
        PREFIX_MAP.put("A5000",                   "OnePlus");
        PREFIX_MAP.put("A5010",                   "OnePlus");
        PREFIX_MAP.put("A6000",                   "OnePlus");
        PREFIX_MAP.put("A6003",                   "OnePlus");
        PREFIX_MAP.put("A6013",                   "OnePlus");
        PREFIX_MAP.put("E1000",                   "OnePlus");
        PREFIX_MAP.put("E1001",                   "OnePlus");
        PREFIX_MAP.put("E1003",                   "OnePlus");
        PREFIX_MAP.put("E1005",                   "OnePlus");
        PREFIX_MAP.put("One",                     "OnePlus");

        PREFIX_MAP.put("A1601",                   "Oppo");
        PREFIX_MAP.put("A37f",                    "Oppo");
        PREFIX_MAP.put("CPH1",                    "Oppo");
        PREFIX_MAP.put("CPH",                     "Oppo");
        PREFIX_MAP.put("F1f",                     "Oppo");
        PREFIX_MAP.put("Mirror 5",                "Oppo");
        PREFIX_MAP.put("R7 Plus",                 "Oppo");
        PREFIX_MAP.put("R9s",                     "Oppo");
        PREFIX_MAP.put("R9t",                     "Oppo");
        PREFIX_MAP.put("X9009",                   "Oppo");

        PREFIX_MAP.put("POV_",                    "Point of view");

        PREFIX_MAP.put("S420",                    "Positivo");
        PREFIX_MAP.put("Twist",                   "Positivo");

        PREFIX_MAP.put("PSP",                     "Prestigio");

        PREFIX_MAP.put("Linq ",                   "QMobile");
        PREFIX_MAP.put("Noir ",                   "QMobile");
        PREFIX_MAP.put("QNote",                   "QMobile");
        PREFIX_MAP.put("QTab",                    "QMobile");

        PREFIX_MAP.put("QW",                      "Qware");

        PREFIX_MAP.put("Galaxy",                  "Samsung");
        PREFIX_MAP.put("GT -",                    "Samsung");
        PREFIX_MAP.put("GT-",                     "Samsung");
        PREFIX_MAP.put("SM -",                    "Samsung");
        PREFIX_MAP.put("SM-",                     "Samsung");

        PREFIX_MAP.put("SM7",                     "Smartisan");
        PREFIX_MAP.put("SM8",                     "Smartisan");
        PREFIX_MAP.put("SM9",                     "Smartisan");
        PREFIX_MAP.put("YQ6",                     "Smartisan");

        PREFIX_MAP.put("401S",                    "Sony");
        PREFIX_MAP.put("402S",                    "Sony");
        PREFIX_MAP.put("501S",                    "Sony");
        PREFIX_MAP.put("502S",                    "Sony");
        PREFIX_MAP.put("601S",                    "Sony");
        PREFIX_MAP.put("602S",                    "Sony");
        PREFIX_MAP.put("Arc BUILD",               "Sony");
        PREFIX_MAP.put("C1",                      "Sony");
        PREFIX_MAP.put("C2",                      "Sony");
        PREFIX_MAP.put("C5",                      "Sony");
        PREFIX_MAP.put("C6",                      "Sony");
        PREFIX_MAP.put("D2",                      "Sony");
        PREFIX_MAP.put("D5",                      "Sony");
        PREFIX_MAP.put("D6",                      "Sony");
        PREFIX_MAP.put("E2",                      "Sony");
        PREFIX_MAP.put("E5",                      "Sony");
        PREFIX_MAP.put("E6",                      "Sony");
        PREFIX_MAP.put("F3",                      "Sony");
        PREFIX_MAP.put("F5",                      "Sony");
        PREFIX_MAP.put("F8",                      "Sony");
        PREFIX_MAP.put("G3",                      "Sony");
        PREFIX_MAP.put("G8",                      "Sony");
        PREFIX_MAP.put("H31",                     "Sony");
        PREFIX_MAP.put("H32",                     "Sony");
        PREFIX_MAP.put("H33",                     "Sony");
        PREFIX_MAP.put("H8",                      "Sony");
        PREFIX_MAP.put("L39",                     "Sony");
        PREFIX_MAP.put("L50",                     "Sony");
        PREFIX_MAP.put("LT2",                     "Sony");
        PREFIX_MAP.put("LT3",                     "Sony");
        PREFIX_MAP.put("NSX-",                    "Sony");
        PREFIX_MAP.put("NSZ-",                    "Sony");
        PREFIX_MAP.put("SGP",                     "Sony");
        PREFIX_MAP.put("SmartWatch",              "Sony");
        PREFIX_MAP.put("SOL",                     "Sony");
        PREFIX_MAP.put("SonyC",                   "Sony");
        PREFIX_MAP.put("SonyD",                   "Sony");
        PREFIX_MAP.put("SonyL",                   "Sony");
        PREFIX_MAP.put("SonyS",                   "Sony");
        PREFIX_MAP.put("SO-",                     "Sony");
        PREFIX_MAP.put("SOV",                     "Sony");
        PREFIX_MAP.put("ST2",                     "Sony");
        PREFIX_MAP.put("Xperia",                  "Sony");

        PREFIX_MAP.put("H60 ",                    "Symphony");

        PREFIX_MAP.put("X98",                     "Teclast");

        PREFIX_MAP.put("Armor",                   "Ulephone");

        PREFIX_MAP.put("Vodacom",                 "Vodafone");
        PREFIX_MAP.put("VFD ",                    "Vodafone");
        PREFIX_MAP.put("VF-",                     "Vodafone");
        PREFIX_MAP.put("VF69",                    "Vodafone");

        PREFIX_MAP.put("Barry",                   "Wiko");
        PREFIX_MAP.put("Birdy",                   "Wiko");
        PREFIX_MAP.put("Bloom",                   "Wiko");
        PREFIX_MAP.put("Cink",                    "Wiko");
        PREFIX_MAP.put("Darkfull",                "Wiko");
        PREFIX_MAP.put("Darkmoon",                "Wiko");
        PREFIX_MAP.put("Darknight",               "Wiko");
        PREFIX_MAP.put("Darkside",                "Wiko");
        PREFIX_MAP.put("Fever",                   "Wiko");
        PREFIX_MAP.put("Fizz",                    "Wiko");
        PREFIX_MAP.put("Freddy",                  "Wiko");
        PREFIX_MAP.put("Getaway",                 "Wiko");

        PREFIX_MAP.put("Harry",                   "Wiko");
        PREFIX_MAP.put("Highway",                 "Wiko");
        PREFIX_MAP.put("Iggy",                    "Wiko");
        PREFIX_MAP.put("Jerry",                   "Wiko");
        PREFIX_MAP.put("Jimmy",                   "Wiko");
        PREFIX_MAP.put("Kar 3",                   "Wiko");
        PREFIX_MAP.put("Kenny",                   "Wiko");
        PREFIX_MAP.put("Kite",                    "Wiko");
        PREFIX_MAP.put("K-Kool",                  "Wiko");
        PREFIX_MAP.put("Lenny",                   "Wiko");
        PREFIX_MAP.put("Lubi",                    "Wiko");
        PREFIX_MAP.put("Ozzy",                    "Wiko");
        PREFIX_MAP.put("Pulp",                    "Wiko");
        PREFIX_MAP.put("Rainbow",                 "Wiko");
        PREFIX_MAP.put("Ridge",                   "Wiko");
        PREFIX_MAP.put("Riff",                    "Wiko");
        PREFIX_MAP.put("Robby",                   "Wiko");
        PREFIX_MAP.put("Selfy",                   "Wiko");
        PREFIX_MAP.put("Slide",                   "Wiko");
        PREFIX_MAP.put("Stairway",                "Wiko");
        PREFIX_MAP.put("Sunny",                   "Wiko");
        PREFIX_MAP.put("Sunset",                  "Wiko");
        PREFIX_MAP.put("Tommy",                   "Wiko");
        PREFIX_MAP.put("ufeel",                   "Wiko");
        PREFIX_MAP.put("U Feel",                  "Wiko");
        PREFIX_MAP.put("U Pulse",                 "Wiko");
        PREFIX_MAP.put("View",                    "Wiko");
        PREFIX_MAP.put("Wax",                     "Wiko");
        PREFIX_MAP.put("W C",                     "Wiko");
        PREFIX_MAP.put("Wim",                     "Wiko");
        PREFIX_MAP.put("W K",                     "Wiko");
        PREFIX_MAP.put("W P",                     "Wiko");
        PREFIX_MAP.put("Zdroid",                  "Wiko");

        PREFIX_MAP.put("AT-",                     "Wolfgang Mobile");

        PREFIX_MAP.put("HM2014",                  "Xiaomi");
        PREFIX_MAP.put("HM ",                     "Xiaomi");
        PREFIX_MAP.put("Hongmi",                  "Xiaomi");
        PREFIX_MAP.put("Mi4",                     "Xiaomi");
        PREFIX_MAP.put("Mi6",                     "Xiaomi");
        PREFIX_MAP.put("MiBox",                   "Xiaomi");
        PREFIX_MAP.put("MiPad",                   "Xiaomi");
        PREFIX_MAP.put("MiTV",                    "Xiaomi");
        PREFIX_MAP.put("Mi ",                     "Xiaomi");
        PREFIX_MAP.put("Mi-",                     "Xiaomi");
        PREFIX_MAP.put("Mix ",                    "Xiaomi");
        PREFIX_MAP.put("Pocophone",               "Xiaomi");
        PREFIX_MAP.put("Poco ",                   "Xiaomi");
        PREFIX_MAP.put("Redmi ",                  "Xiaomi");

        List<String> letters = Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

        for (String l1: letters) {
            for (String l2: letters) {
                for (String l3: letters) {
                    FULL_PREFIX_MAP.put(l1+l2+l3+"-", "Unknown");
                }
            }
        }
        FULL_PREFIX_MAP.putAll(PREFIX_MAP);

    }

    static final List<String> TEST_MODELS = Arrays.asList(
        "2013023",
        "2014011",
        "2014811",
        "2014812",
        "2014813",
        "2014817",
        "2014818",
        "2014819",
        "401SO",
        "4034G",
        "502SO",
        "5045D",
        "601SO",
        "8063",
        "A0001",
        "A1",
        "A1601",
        "A3",
        "A37f",
        "A510",
        "AFTB",
        "ALE-L21",
        "Amazon Kindle Fire",
        "ANE-LX1",
        "Arc",
        "arc 10HD",
        "arc 7",
        "arc 7HD",
        "Archos 50 Helium 4G",
        "ARCHOS 79 Platinum",
        "Archos_80_Helium_4G",
        "Armor",
        "Ascend G630",
        "ASUS_A007",
        "ASUS Transformer Pad TF300T",
        "ASUS_X00LD",
        "ASUS_X00QD",
        "ASUS_X013D",
        "ASUS_Z017DC",
        "ASUS ZenFone 2E",
        "ASUS ZenWatch 2",
        "B1",
        "B3 Simply",
        "BAC-TL00",
        "Bebook",
        "BEBOOK070I00",
        "BIRDY",
        "BKL-AL00",
        "Blade L3 Plus",
        "BLA-L29",
        "BTV-W09",
        "Bush Spira B1 8\\\"\"",
        "C1905",
        "C2005",
        "C6903",
        "C6943",
        "CAM-TL00H",
        "CHC-U01",
        "Che1-L04",
        "CINK FIVE",
        "CINK PEAX 2",
        "CLT-L29",
        "COL-L29",
        "COR-AL00",
        "CPH1609",
        "CUBOT CHEETAH 2",
        "CUBOT DINOSAUR",
        "CUBOT ECHO",
        "CUBOT H3",
        "CUBOT KING KONG",
        "CUBOT MAGIC",
        "CUBOT_MANITO",
        "CUBOT MAX",
        "CUBOT NOTE Plus",
        "CUBOT_NOTE_S",
        "CUBOT X18",
        "CUBOT_X18_Plus",
        "D2533",
        "D5322",
        "D5803",
        "D6603",
        "DM-01G",
        "Doro 8030/8031/8028",
        "DROIDX",
        "E2363",
        "E5563",
        "E5823",
        "E6853",
        "Elephone G7",
        "EVA",
        "EVA-L09",
        "EVOLVEO Smart TV box Q4",
        "F1f",
        "F3111",
        "F3213",
        "F5121",
        "F5321",
        "F8331",
        "F8332",
        "FIG-AL10",
        "FIG-LX1",
        "Fire",
        "Fire Phone",
        "FRD-L09",
        "G3121",
        "G3421",
        "G630",
        "G7",
        "G8141",
        "G8231",
        "G8341",
        "Galaxy Nexus",
        "GT-I9070",
        "GT-I9128V",
        "GT-I9195I",
        "GT-I9300",
        "GT-I9500",
        "GT-I9505",
        "GT-N7100",
        "GT-N7105",
        "GT-N8000",
        "GT-N8010",
        "GT-P5100",
        "GT-P5110",
        "GT-S6810",
        "GTV100",
        "GTV1100",
        "G Watch",
        "G Watch R",
        "H30-U10",
        "H3213",
        "H60",
        "H60-L04",
        "H8266",
        "hisense_gx1200v",
        "HM 1SC",
        "HM NOTE 1LTETD",
        "HM NOTE 1LTEW",
        "Honor Note 8",
        "HP Pavilion dv6500 Notebook PC",
        "HP SlateBook 10 x2 PC",
        "HT70",
        "HTC D820mu",
        "HTC Desire",
        "HTC_DesireHD_A9191",
        "HTC_DesireS_S510e",
        "HTC_DesireZ_A7272",
        "HTC_IncredibleS_S710e",
        "HTC Legend",
        "HTC Pyramid",
        "HTC Sensation",
        "HTC Sensation XE with Beats Audio Z715e",
        "HTC U11",
        "HTC U11 life",
        "HTC Vision",
        "HTC_Wildfire_A3333",
        "HUAWEI ALEL04",
        "HUAWEI G610-U00",
        "HUAWEI G6-U10",
        "HUAWEI GRA-L09",
        "HUAWEI MT1-U06",
        "HUAWEI MT2-L01",
        "HUAWEI MT7-L09",
        "HUAWEI NXT-TL00",
        "HUAWEI VNS-DL00",
        "HUAWEI VNS-L31",
        "K015",
        "K8 Watch",
        "KFFOWI",
        "KFTHWI",
        "Kindle Fire HDX",
        "L-01F",
        "L39t",
        "L50t",
        "LAVA A1",
        "Lenco 7\\\"\"\"\" tablet",
        "LENNY2",
        "LENNY3",
        "Lenovo A3500-FL",
        "Lenovo A3500-H",
        "Lenovo A7600-H",
        "Lenovo K33b36",
        "Lenovo K53b36",
        "Lenovo PB1-750M",
        "Lenovo TB3-X70F",
        "Le X620",
        "LG-D337",
        "LG-D850",
        "LG-D855",
        "LG-D855/D85520e",
        "LG-D855/V20e",
        "LG-GT540",
        "LG-H440n/V10e",
        "LG-H815",
        "LG-H850",
        "LG-H870",
        "LG-H955",
        "LG-K200",
        "LG-K220",
        "LG-K430",
        "LG-L160L",
        "LG-LU3000",
        "LG-M250",
        "LG-M320",
        "LG-M400",
        "LG-P505R",
        "LG Watch Urbane",
        "LG-X230",
        "LT28h",
        "LT30p",
        "M353",
        "m3 note",
        "M5 Note",
        "ME301T",
        "MHA-AL00",
        "MHA-L29",
        "MI 2S",
        "Mi-4c",
        "MI 4W",
        "MI 5",
        "MI 5s",
        "MI 6",
        "Mi A1",
        "Micromax AQ4501",
        "MID713",
        "MI MAX",
        "Mi MIX 2",
        "MI NOTE LTE",
        "MIX 2S",
        "Moto C",
        "Moto C Plus",
        "MotoE2(4G-LTE)",
        "Moto E (4)",
        "moto e5",
        "moto e5 play",
        "moto e5 plus",
        "MotoG3",
        "Moto G (4)",
        "Moto G (4)",
        "Moto G (5)",
        "Moto G (5) Plus",
        "Moto G (5S",
        "Moto G (5S)",
        "moto g(6)",
        "Moto G Play",
        "motorola one",
        "moto x4",
        "Moto Z2 Play",
        "MPQC785 IPS",
        "MS50L",
        "MX4",
        "MX4 Pro",
        "MZ-M3s",
        "MZ-MEIZU_M5",
        "N918St",
        "Nexus 10",
        "Nexus 4",
        "Nexus 5",
        "Nexus 5X",
        "Nexus 6",
        "Nexus 6P",
        "Nexus One",
        "Nokia 1",
        "Nokia 6.1",
        "Nokia 7 plus",
        "NSZ-GS7/GX70",
        "ONE A2003",
        "ONEPLUS A3000",
        "ONEPLUS A3003",
        "ONEPLUS A5000",
        "ONEPLUS A5010",
        "ONEPLUS A6003",
        "OPPO A77",
        "P01T_1",
        "PadFone T008",
        "Personal Huawei G620S",
        "PE-TL20",
        "Pixel",
        "Pixel 2",
        "Pixel 2 XL",
        "Pixel C",
        "Pixel XL",
        "PlayBook",
        "PLK",
        "PLK-AL10",
        "PO 8217 TAD-90022",
        "PocketBook SURFpad 3 (7,85\\\"\")",
        "POCOPHONE F1",
        "POV_TV-HDMI-200BT",
        "PULP",
        "PULP 4G",
        "PULP FAB",
        "Qualcore 1027 3G",
        "QW TB-1317Q",
        "QW TB-1380QHD",
        "QW TB-1517D",
        "RAINBOW JAM",
        "RAINBOW UP 4G",
        "Redmi 3",
        "Redmi 4",
        "Redmi 4X",
        "Redmi Note 3",
        "Redmi Note 4",
        "Redmi Note 5",
        "RNE-L21",
        "RS988",
        "S420",
        "S8",
        "SAMSUNG GT-I8190N/I8190NXXANI1",
        "SAMSUNG GT-I9300/I9300BUALF1",
        "SAMSUNG SM-G920F",
        "SAMSUNG SM-G920F/G920FXXEinterim",
        "SAMSUNG SM-G925K",
        "SAMSUNG SM-G930F",
        "SCL-TL00H",
        "SD4930UR",
        "SGP511",
        "SGP512",
        "SGPT12",
        "SHIELD Android TV",
        "SM801",
        "SM919",
        "SM-A320FL",
        "SM-A500FU",
        "SM-A510F",
        "SM-A510M",
        "SM-A520F",
        "SM-A530F",
        "SM-A605FN",
        "SM-A700F",
        "SM-A910F",
        "SmartWatch 3",
        "SM-C7000",
        "SM-G355HN",
        "SM-G355M",
        "SM-G360F",
        "SM-G530BT",
        "SM-G530H",
        "SM-G531BT",
        "SM-G531H",
        "SM-G532M",
        "SM-G532MT",
        "SM-G570M",
        "SM-G600FY",
        "SM-G610F",
        "SM-G610M",
        "SM-G611MT",
        "SM-G7102T",
        "SM-G850F",
        "SM-G900F",
        "SM - G900H",
        "SM-G900M",
        "SM-G901F",
        "SM-G903F",
        "SM-G903M",
        "SM-G920F",
        "SM-G925F",
        "SM-G9300",
        "SM-G930F",
        "SM-G935F",
        "SM-G950F",
        "SM-G955F",
        "SM-G960F",
        "SM-G965F",
        "SM-J105B",
        "SM-J110M",
        "SM-J120G",
        "SM-J120H",
        "SM-J250M",
        "SM-J320FN",
        "SM-J320M",
        "SM-J500M",
        "SM-J510FN",
        "SM-J510MN",
        "SM-J530F",
        "SM-J530G",
        "SM-J600FN",
        "SM-J600GT",
        "SM-J700M",
        "SM-J701MT",
        "SM-J710MN",
        "SM-J730F",
        "SM-J730G",
        "SM-J810M",
        "SM-N7505",
        "SM-N9005",
        "SM-N9008",
        "SM-N9008V",
        "SM-N920S",
        "SM-N950F",
        "SM-N960F",
        "SM-P600",
        "SM-P605",
        "SM-P900",
        "SM-T116NU",
        "SM-T320",
        "SM-T365",
        "SM-T520",
        "SM-T530",
        "SM-T550",
        "SM-T555",
        "SM-T580",
        "SM-T585",
        "SM-T713",
        "SM-T800",
        "SM-T810",
        "SM-T813",
        "SM-T820",
        "SO-02H",
        "SOL25",
        "SonyC2104",
        "SonyEricssonE10a",
        "SonyEricssonLT15i",
        "SonyEricssonX10i",
        "SonyLT30at",
        "SonySO-04F",
        "SonyST26a",
        "Sony Tablet S",
        "SOV32",
        "ST23i",
        "STV100-1",
        "SUNNY",
        "T1 7.0",
        "TA-1000",
        "TA-1003",
        "TA-1004",
        "TA-1033",
        "TAB10-201",
        "Tab1060",
        "ThinkPad Tablet",
        "thl 4000",
        "thl 5000",
        "thl T6 pro",
        "thl T6S",
        "T-Mobile myTouch 3G Slide",
        "Transformer TF101",
        "Twist Max",
        "Twist Metal 32GB",
        "U FEEL",
        "U FEEL LITE",
        "UTime G7",
        "VF695",
        "VF-795",
        "VFD 200",
        "vivo 1609",
        "vivo Y22",
        "VK810 4G",
        "VM_Vertis 4010 You",
        "Vodacom Power Tab 10",
        "VS985 4G",
        "VTR-L09",
        "WAX",
        "WETAB07D04B",
        "X",
        "X9009",
        "X98 Air 3G(C9J8)",
        "X98 Air III(M5C5)",
        "Xoom 3G",
        "Xperia P",
        "XT1033",
        "XT1039",
        "XT1068",
        "XT1069",
        "XT1078",
        "XT1097",
        "XT1225",
        "XT1635-02",
        "Y635-L21",
        "YC-3135D",
        "Z00D",
        "Z2 Play",
        "Z820",
        "ZE553KL",
        "ZP951"
    );
}

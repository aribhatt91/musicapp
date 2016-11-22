package com.optimus.music.player.onix.WhatsHotActivity;

import com.optimus.music.player.onix.Common.Instances.YTVideo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by apricot on 6/4/16.
 */
public class VideoLibrary {
    public static String imageUri = "http://img.youtube.com/vi/";
    public static String queryUrl = "http://www.youtube.com/results?search_query=";



    public static String[] urlInternational = {
            "PT2_F-1esPk",
            "34Na4j8AVgA",
            "BiQIc7fG9pA",
            "K44j-sb1SRY",

            "lFqmHzaBOGM",
            "hdw1uKiTI5c",

            "oe0oLinjeiE",
            "u9Dg-g7t2l4",
            "FGdaSDwKPG8",
            "fk4BbF7B29w",
            "1ekZEVeXwek",
            "5WL672bjJgM",
            "pTOC_q0NLTk",
            "F9kXstb9FF4",
            "8yGipyel-3I",
            "eimgRedLkkU",

            "p5RobDomh5U",
            "QxRiwnmYjMg",
            "5Nrv5teMc9Y",
            "31crA53Dgu0",
            "Pgmx7z49OEk",

            "HL1UzIK-flA",
            "oyEuk8j8imI",
            "LHCob76kigA",
            "pXRviuL6vMY",
            "fRh_vgS2dFE",
            "tD4HCZe-tew",
            "bSfpSOBD30U",
            "F2QZKV3oRaU",
            "5yXQJBU8A28",
            "PAzH-YAlFYc",
            "uo35R9zQsAI",
            "9WbCfHutDSE",
            "cMTAUr3Nm6I",
            "qDcFryDXQ7U",
            "BxuY9FET9Y4",
            "VbfpW0pbvaU",
            "YQHsXMglC9A",
            "RgKAFK5djSk",
            "AJtDXIazrMo",
            "DDWKuo3gXMQ",
            "QcIy9NiNbmo",
            "e-ORhEE9VVg",
            "YykjpeuMNEk",
            "GTyN-DB_v5M",
            "NywWB67Z7zQ",
            "C_3d6GntKbk",
            "foE1mO2yM04",
            "5GL9JoH4Sws",
            "g_uYn8AVqeU",
            "o0citpYDaVg",
            "oiY_iKSpWLM",
            "K_9tX4eHztY",
            "FMlcn-_jpWY",
            "G5Mv2iV0wkU",
            "XgJFqVvb2Ws",
            "d7cVLE4SaN0",
            "wfN4PVaOU5Q",
            "GsPq9mzFNGY",
            "neSv8fjmDB0",
            "Q821mNXNw-I",
            "wyK7YuwUWsU"



    };

    public static String[] namesInternational = {
            "The Chainsmokers - Closer (Lyric) ft. Halsey ",
            "The Weeknd - Starboy ft. Daft Punk",
            "gnash - I hate u, I love u (ft. Olivia o'Brien)",
            "Big Baby D.R.A.M. - Broccoli feat. Lil Yachty",

            "Daya - Sit Still, Look Pretty",
            "Katy Perry - Rise",
            "Hundred Handed - She Was The One",
            "Disturbed - The Sound Of Silence",
            "X Ambassadors (Erich Lee Gravity Remix) - Unsteady | Me Before You",
            "Adele - Send My Love (To Your New Lover) ",
            "Ariana Grande - Into You ",
            "Ariana Grande - Let Me Love You (Official) ft. Lil Wayne",
            "The Lumineers - Ophelia",
            "The Strumbellas - Spirits",
            "Bored To Death - blink-182",
            "Empire Of The Sun - Walking On A Dream",

            "Justin Timberlake - Can't stop the feeling",
            "Ghost twon DJs - My Boo",
            "P!nk - Just Like Fire ",
            "Sia - Cheap Thrills",
            "Jennifer Lopez - Ain't Your Mama",

            "Rihanna - Work",
            "Justin Bieber - Love Yourself",
            "Lukas Graham - 7 Years",
            "Twenty one pilots - Stressed Out",
            "Justin Bieber - Sorry",
            "Zara Larsson - Lush Life",
            "G-Eazy x Bebe Rexha - Me, Myself & I",
            "Sigala - Say You Do",
            "Jonas Blue - Fast Car",
            "DNCE - Cake By The Ocean",
            "Flo Rida - My House",
            "Ariana Grande - Dangerous Woman",
            "Meghan Trainor - NO",
            "Major Lazer - Light It Up ",
            "Charlie Puth - One Call Away",
            "Shawn Mendes - Stitches",
            "Adele - Hello",
            "Wiz Khalifa - See You Again ",
            "Ellie Goulding - Love Me Like You Do",
            "Adele - When We Were Young",
            "Taylor Swift - Bad Blood",
            "Taylor Swift - Blank Space",
            "Coldplay - Hymn For The Weekend",
            "Zara Larsson, MNEK - Never Forget You",
            "Justin Bieber - What Do You Mean?",
            "ZAYN - Pillowtalk",
            "Mike Posner - I Took A Pill In Ibiza",
            "Fifth Harmony - Work from Home",
            "Drake - One Dance (feat. Wizkid & Kyla)",
            "The Chainsmokers - Don't Let Me Down",
            "Kevin Gates - 2 Phones",
            "Future - Low Life ft. The Weeknd ",
            "Selena Gomez - Hands To Myself",
            "The Chainsmokers - Roses",
            "Nick Jonas - Close ft. Tove Lo",
            "Bryson Tiller - Don't",
            "Rihanna - Needed Me",
            "James Bay - Let It Go",
            "Justin Bieber - Company ",
            "Kanye West - Famous",
            "Taylor Swift - New Romantics"

    };

    public static String[] bollywoodurl = {
            "JEHHTxNBNxc",
            "7t9bT5QohRo",
            "z-diRlyLGzo",
            "CvPdtf8Ijj4",


            "k4yXQkG2s1E",
            "356hZ_Glwno",
            "dMByhaezv9A",
            "ChfgjlxiZu0",
            "5az4Q1BaSG8",

            "wsxUywlCbiA",
            "k7qWPisOdXs",
            "y1FF64dIWa0",
            "y-pCtglTc8g",
            "QNCqw1QmvRM",
            "XawNiKe_0wo",
            "s4JLu2INYMM",
            "aUs1yzZMbMQ",
            "ht-HDRGDz9w",
            "sd84Wqq6UzM",
            "PIyf0hMc498",
            "cMHu9tngYNs",
            "3FXh6E7OBhA",
            "ZN_FPvihdAw",
            "f3UAyGAwzA8",
            "ix5FgZOKIpU",

            "dI_mRxrr2fk",
            "t1tzKrOPCx0",
            "tGkVxa9rA0U",
            "Xs3--MgVRks",
            "bNjxNe-y068",
            "13nC5UdyoWg",
            "5gwy0gcjIkI",
            "l-UBwamTNV8",
            "rF0d0pA24Uw",

            "UiN3AY7bdBg",
            "fUC6LJEbwHo",

            "0w62ddeVwGE",
            "AjE4Q4HKHv0",
            "r2ecLFsdbzI",
            "6TPcwWHZN_0",
            "Jis04VOZyEU",
            "vosWN0CkDP4",
            "nczqAH5ejRA",


            "bvDK4_Plw3g",
            "BDTeebS_DT4",
            "3Kjv841JoLk",
            "QzJfW1sij9k",
            "lQh3iy86QUE",
            "r4eaRdaky_c",
            "BpXYx8NiNrE",
            "r5uI1XPmwyc",
            "hTBJOj1GyJ4",
            "6XNTtcvErZc",
            "Xx4xA-2xQ8U",//11

//

            "Ob4wvIHUmnA",
            "WRQHV3kDcyo",
            "9WBb_HG_foY",
            "N_KpjLhJa1k",
            "d4_szl5EEww",
            "hNubz3rGwrA",
            "GYFDRoJtfGM",
            "Zo4WsI14s3g",
            "0GQX3GWcUmk",
            "qMfvbKBvOtI",
            "-sWXx1mbgtU",
            "2zoIA42nJJc",
            "SahyX6U1is0",
            "TjnYjsHPNK8",

            "DS-raAyMxl4",
            "Iqu_W5W4YO4",
            "uxTXp0-iZrY",
            "GZIh0bhuFtg",
            "BkA0lq-0f14",

            "I3dxAXgIhCU",
            "oVVq7RDy53s",
            "51xWny9Wsdo",
            "PdrTQmsxzpU",
            "MUHJT5YRs1A",
            "MRtRcTfszjY",
            "Dp6lbdoprZ0",
            "C5Z8WQv1Wf4",
            "R_ha0AMNUn0",
            "GYbPrev2aQU"

    };

    public static String[] bollyName = {
            "Shankar Ehsaan Loy - Hota Hai | Mirzya",
            "Shankar Ehsaan Loy - Chakora | Mirzya",
            "Pritam | Arijit - Channa Mereya | Ae Dil Hai Mushkil",
            "Arijit I Badshah | Jonita - The Breakup Song",

            "Badshah, Neha Kakkar, Indeep Bakshi - Kaala Chashma | Bar Bar Dekho",
            "Jasleen R | Harshdeep K, Siddharth MD - Nachde De Saare | Bar Bar Dekho",
            "Bilal Saeed  - Teri Khair Mangdi | Bar Bar Dekho",
            "Shreya Ghoshal | Ankit Tiwari  - Jab Tum Hote Ho | Rustom",
            "Armaan Malik, Neeti Mohan - Sau Aasmaan | Bar Bar Dekho",

            "Arijit Singh - Lo Maan Liya Lyrical | Raaz Reboot",
            "Raaz Reboot - Raaz Reboot Diaries | V3",
            "Arijit Singh - Besabriyaan | M. S. Dhoni ",
            "Rahul Vaidya - Do Chaar Din | Karan Kundra, Ruhi Singh",
            "Arijit Singh - Raaz Ankhei Teri | Raaz Reboot",
            "Sakina Khan - Phir Tu",
            "Wajid - Parinda Hai Parinda",
            "Arijit Singh | DJ Kiran Kamath - The Arijit Singh Mashup",
            "Armaan Malik | Neeti Mohan - Pyar Maanga Hai | Zarine Khan",
            "Dhishoom - Jaaneman Aah",
            "Pritam - Sau Tarha Ke | Dhishoom",
            "Dhishoom - Subaah Hone Na De",
            "Kartik Dhiman - Ishq Di Gadi",
            "Sunidhi Chauhan - Badal | Akira",
            "Sonakshi Sina | Vishal Dadlani - Rajj Rajj Ke | Akira",
            "Himesh Reshammiya - Ishq Samundar Reloaded | Tera Surroor",

            "Sachin, Jigar, Vayu & Kanika Kapoor - Beat Pe Booty",
            "Arijit Singh & Palak Muchhal - Dekha Hazaro Dafa | Rustom",
            "Sachin-Jigar - Toota Jo Kabhi Tara",
            "Ankit Tiwari - Tay Hai",
            "Rahul Jain - Teri Yaad (Unplugged) | Fever",
            "Arijit Singh - Bas Ek Bar | Fever",
            "Atif Aslam, Arko - Tere Sang Yara | Rustom",
            "Tony Kakkar - Mile Ho Tum | Fever",
            "Darshan, Kanika Kapoor - Teri Kamar Ko | GGM",

            "A.R Rahman - Tu Hai | Mohenjodaro",
            "Meet Bros ADT | Sam Bombay - Befikra",

            "Rahat Fateh Ali Khan - Tumhe Dillagi",
            "Amaal Mallik Feat. Shaan - Tum Ho Toh Lagta Hai",
            "Jonita Gandhi & Amit Mishra - Sau Tarah Ke",
            "Rahat Fateh Ali Khan - Jag Ghoomeya | Sultan",
            "Armaan Malik - Kuch To Hai | Do Lafzon Ki",
            "Sona Mohapatra - Qatl-E-Aam 2.0 (Unplugged)",
            "Nyvaan, ft. Astha Bakshi - Making Love",

            "Zack Knight - ENEMY",
            "Amitabh Bachhan - Kyun Re",
            "Ankit Tiwari - BADTAMEEZ",
            "Armaan Malik, Jeet Gannguli - Mujhko Barsaat Bana Lo | Joonuniyat",
            "Neeraj Shridhar,Tulsi Kumar, Meet Bros - Naachenge Saari Raat | Joonuniyat",
            "Neeti Mohan - Akhan Vich",
            "Neeti Mohan - Mohe Aaye Na Jag Se Laaj | Cabaret",
            "Shashaa Tirupati, Altamash - Allah Hu Allah | Sarabjit",
            "Sonu Nigam, Jeet Gannguli - Dard | Sarabjit Concert",
            "Tulsi Kumar, Amaal Mallik - Salamat | Sarabjit",
            "Jeet Gannguli, Rahat Fateh Ali Khan - Chahat | Blood Money",//11



            "Arijit Singh, Meet Bros - Ijazat ",
            "Armaan Malik - Sab Tera",
            "Meet Bros, Monali Thakur - Cham Cham",
            "Yo Yo Honey Singh - High Heels Te Nache",
            "Shah Rukh Khan - Jabra FAN",
            "Mithoon - Ji Huzoori",
            "Arijit Singh - Bolna",
            "Armaan Malik - Buddhu Sa Mann",
            "Benny Dayal, Nucleya - Lets Nacho",
            "Arjun Kanungo - Fursat",
            "Kapoor & Sons - Kar Gayi Chull",
            "Arijit Singh, Antara Mitra - Gerua",
            "Sangeet Haldipur & Rasika Shekar - Awargi",
            "SMohan Kannan - Nirvana",

            "Arijit Singh - Sanam Re",
            "Ariji Singh, Amit Trivedi - Yeh Fitoor Mera",
            "Amit Trivedi - Pashmina",
            "Kavita Seth - Jeete Hain Chal",
            "Ankit Tiwari, Arijit Singh - Dil Cheez Tujhe Dedi",

            "Aditi Singh Sharma & Arman Malik - Oye Oye",
            "Sonu Kakkar - Phir Teri Bahon Mein ",
            "Sonu Nigam, Jeet Ganguli - Dard",
            "Shaarib & Toshi - Aaj Ro Len De",
            "Arijit Singh, Tulsi Kumar, Amaal Mallik - Salamat",
            "Arijit  Singh, Tulsi Kumar - Soch Na Sake",
            "Amaal Mallik, Armaan Malik - Main Rahoon Ya Na Rahoon",
            "Armaan Malik, Amaal Mallik - Bol Do Na Zara",
            "Arijit Singh, Meet Bros - Girl I Need You",
            "Neha Kakkar, Tony Kakkar - Do Peg Maar"


    };

    public static String[] newSenNames = {
            "Tiffany Alvord - Hello | Adele (Cover)",
            "Tiffany Alvord & Chester See - Love Me Like You Do",
            "Tiffany Alvord - Baby, I Love You",
            "Prateek Nandan- Zoya (Original)",
            "Ft.Sandeep Kulkarni | Jai, Parthiv - Kishore Kumar (Mashup)",
            "Sandeep Kulkarni - R.D Burman Mashup ",
            "Rajdeep Chatterjee IMX Unplugged - Ek Pyar Ka Nagma Hai",
            "Unplugged Cover By Udit Shandilya - Kya Hua Tera Wada",
            "Rajdeep IMX Unplugged - Lag Ja Gale",
            "Rajdeep Chatterjee | IMX Unplugged - Mora Saiyyan",
            "Siddharth Slathia - Abhi Mujhme Kahin (Revisited)",
            "Siddharth Slathia feat. Shraddha Sharma - Batein (Official Music Video)",
            "Siddharth Slathia - Yeh Fitoor Mera & Pashmina | Fitoor | Cover",
            "Rashmeet Kaur - Tere Bina (A.R. Rahman) | Cover",
            "Feat. Bhavya Pandit & Abhay Jodhpurkar - Abhi Na Jao",
            "Bhavya Pandit | Anurag Mishra - Tere Bina Zindagi Se Koi",
            "Ft. Sahiljeet Singh & Mann Taneja - Din Dhal Jaaye",
            "Raghav Sachar - Mitwa (Cover Version)",
            "Raghav Sachar - Gulabi Ankhen (Cover)",
            "Pankhuri Awasthi - Yaadein (Official Video)",
            "Priyanka Negi - Main Jaha Rahoon (cover)",
            "Jai, Parthiv - Strings Mashup (Feat.Sandeep Kulkarni)",
            "Tony Kakkar ft. Neha Kakkar, Meiyang Chang - Hanju",
            "Neha Kakkar - I'm A Rockstar",
            "Neha Kakkar - Teri Yaad",
            "Neha Kakkar & Tony Kakkar - Mora Saiyaan / Phir Le aaya / Khafa Mahiya",
            "Sunakshi Raina - Tum hi ho | Aashiqui 2 (Arijit Singh) | Female Cover",
            "Lisa Mishra - When We Were Young | Adele",
            "Aakash Gupta - Yeh Chand Sa Roshan Chehra",
            "Ash King - Samne Yeh Kaun Aaya (The Unwind Mix)",
            "Jonita Gandhi - Tu Tu Hai Wohi (The Unwind Mix)",
            "Jonita Gandhi | The Jam Room 3 - Aao Huzoor Tumko",
            "Ashmi Bose - Ajeeb Dastan Hai Yeh (Cover)",
            "Anny Ahmed | Rabindra Sangeet - Dariye Achho",
            "Megan Nicole & Alex Zaichkowski - Never Forget You | Zara Larsson & MNEK (cover)",


            "Bolna (cover) - Siddharth Slathia",
            "Agar Tum Saath Ho (Revisited) - Siddharth Slathia",
            "Sanu Ik Pal Chain - Sidharth Slathia",
            "Sanam Re - Siddharth Slathia",
            "Soch Na Sake - Siddharth Slathia",
            "Janam Janam (Dilwale) - Siddharth Slathia",
            "Hello Adele (Acoustic Cover) - Aakash Gandhi ",
            "Yeh Honsla (Candlelight Cover) - Aakash Gandhi | Jonita Gandhi",
            "Tujhe Bhula Diya / Hello - Gaurav Dagaonkar (Synchronicity)",
            "Pehla Nasha / You're Still The One - Gaurav Dagaonkar Ft. Shweta Subram",
            "Ek Ajnabee Haseena Se - Gaurav Dagaonkar | Retro Rewind",
            "Ek Ladki Ko Dekha (Acoustic) - Sanam",
            "Kuch Na Kaho - Sanam ft. Shirley Setia",
            "Sanu Ik Pal / Tere Bin Nahi Lagda (Mashup) - Tony Kakkar, Neha Kakkar",
            "Tumse Milke Aisa Laga (Studio Version) - Sachet Tandon ",
            "Pucho Na Yaar Kya Hua (The Unwind Mix) - Prajakta Shukre",
            "Hothon Se Chhu Lo Tum (The Unwind Mix) - Mohammed Irfan",
            "Pyar Deewana Hota Hai (Acoustic Cover) - Timir Biswas, Kunal Biswas & Chayan Chakraborty",
            "Neele Neele Ambar Par (The Unwind Mix) - Ash King",
            "Dilbar Mere (The Unwind Mix) - Rahul Vaidya",
            "Sanam Re - Female Cover by Shirley Setia",
            "Tum Se Hi | Jab We Met - Shirley Setia",


            "Yeh Fitoor Mera | Fitoor | Cover - Tushar Joshi",
            "Dil Darbadar | PK | Cover - Tushar Joshi",
            "Galliyan | Ek Villain | Cover - Tushar Joshi",
            "Tu Jo Mila - Shraddha Sharma Ft. Clinton Charles",
            "Muskurane | Citylights (Cover) - Shraddha Sharma ",
            "Katra Katra | Alone [2015] - Shraddha Sharma Ft. Avish Sharma",
            "Yeh Vaada Raha - Shraddha Sharma",
            "Kabira (Cover) - Lisa Mishra",
            "Sorry Beyoncé (Lemonade) Live Cover - Lisa Mishra",
            "Airplanes | Aap Ki Kashish (Mashup Cover) - Hanu Dixit",
            "Galliyan | Sun Raha Hai Na Tu (Acoustic Mashup Cover) - Hanu Dixit",
            "Pareshaan | Ishaqzaade (Full Cover) - Ankita Sachdev",
            "Be Intehaan unplugged - Sachet Tandon",
            "Hum Bewafa Hargiz Na The - Sanam",
            "Muskurane Ki Wajah Tum Ho (Acoustic Cover) - Siddharth Slathia feat. Abhishek Nath",
            "Tumhe Apna Banane Ka (Revisited) - Siddharth Slathia",
            "Pee Loon / You Sang To Me - Gaurav Dagaonkar",
            "Tu Chahiye (Unplugged) | Bajrangi Bhaijaan - Siddharth Slathia",
            "Kabhi Jo Badal Barse | Cover - Siddharth Slathia",
            "Chaand Chhupa - Armaan Mallik",

            "A Tribute To Atif | The Kroonerz Project - Anurag Mishra",
            "Kuch Kuch Hota Hai Unplugged - Siddharth Slathia (Cover) ",
            "Tere Sang Yaara | Rustom | Atif Aslam - Siddharth Slathia (Cover)",
            "Tanha Tanha | Rangeela | The Kroonerz Project - Sahiljeet Singh | Natalya | Harshvardhan Gadhvi ",
            "Shauk Hai | The Kroonerz Project - Shivangi Amrit | Mann Taneja",
            "Googly Woogly Wooksh Full Video - Monali Thakur Ft. Dilwale"

    };

    public static String[] newSenUrl = {
            "_-Ze6Td01h4",
            "38MmkTUfSPs",
            "0hCx37sRN74",
            "nd5A6TeCijk",
            "_AzU7eRe96s",
            "ZuZxTD2qR_0",
            "HMn3Fmcmf_U",
            "N_S8OPs8HAE",
            "NnbmpECZEUk",
            "z3n-1rsqdOc",
            "Fs1d01J96mA",
            "B4mc4YScXVM",
            "ui9zrVDXiPs",
            "PZ8YZ-Pj-1I",
            "e3VHvtU1Azg",
            "1pF7IAXMbbA",
            "C6BRHxT5DmY",
            "JU7vtmq9ZSQ",
            "eFo1VUusZ00",
            "4CbtcbYzwZY",
            "S_tSOkCw-u4",
            "wZ_i-dFtYII",
            "MVyWRAq81yI",
            "Q7BHT57XRmw",
            "v88mdr9ne_c",
            "izEpCJdx7QA",
            "L4lv_qxFQgE",
            "MgrRSw4Aa54",
            "BOYlxHAwqYo",
            "Co8muxbYvSI",
            "BEYCEq1m6kk",
            "9ebidZnxUPc",
            "FECMhUcoN_0",
            "SM7hUBaO-hA",
            "gtuhxBN09U0",

            "JEk5Bg3wP6Y",
            "kBlTugtEyqc",
            "ipJXMq7s9e0",
            "DeIOwxWAoTs",
            "2ZlCp4wLipg",
            "B1buG52M14k",
            "KeVJq89ruKs",
            "HReN67juhCg",
            "nYyTENkOrmc",
            "Yl7CXhMC1MI",
            "nsbtVEwYkVs",
            "XKbiIahG6ok",
            "3A1LgFkIPfA",
            "2V1fk3mbur4",
            "a-v-ORVS1oA",
            "I_8SBZDtFjw",
            "oDZK9Vwoehk",
            "mT1NFMoLJCs",
            "dc5lTQWorVw",
            "tff0jb2-tFE",
            "34_vBu2zVbo",
            "F2v75uIMLSQ",


            "xRgM6URg5f0",
            "T2UnVEXlhkI",
            "M7JonFEbcWg",
            "uAgExO56nPE",
            "LP9tIf8oxkk",
            "GPk5HTntBmI",
            "CgrRxo8LBw4",
            "8OKnt_AOoQs",
            "wKYDWgaWxig",
            "lrkMhMmTHLk",
            "6KcNKBu0h_I",
            "UaLbTiNZ_C0",
            "oGyrU9IMLP0",
            "AvRcKyEMkns",
            "buR7BOHRNPM",
            "B1QOxK9-o1o",
            "ovkcCGtsulU",
            "ztlEwjJDDLo",
            "zvzYPLydt8Q",
            "-KHX4edGT5Q",

            "H_I-2N598qc",
            "HxpjChVWQzY",
            "5bj_pBl_lkI",
            "28NGR0HE0FY",
            "OQ2Jrquh6ws",
            "WeB2jgV1UAM"

    };

    public static String[] rbNames = {

            "YG ft. Drake, Kamaiyah - Why You Always Hatin? ",
            "French Montana - No Shopping ft. Drake",
            "Dae Dae - Wat U Mean (Aye, Aye, Aye)",
            "Big Baby D.R.A.M. - Broccoli feat. Lil Yachty",
            " Lil Wayne, Wiz Khalifa & Imagine Dragons - Sucker for Pain",

            "DJ Khaled - For Free (ft. Drake)",
            "Drake - CONTROLLA",
            "Drake - Pop Style",
            "Desiigner - Panda",
            "Future - Low Life ft. The Weeknd",

            "Rihanna - Kiss It Better",
            "Rihanna - Man Down",
            "Skizzy Mars - I'm Ready",
            "Kevin Gates - Really Really",
            "Kevin Gates - Pride",
            "Krizz Kaliko - Stop The World ",
            "The Weeknd - The Hills",
            "The Weeknd - Often",
            "The Weeknd - Wicked Games",
            "TWENTY88 - Out Of Love",
            "Royce da 5'9'' - Tabernacle",
            "Bryson Tiller - Sorry Not Sorry ",
            "Bryson Tiller - Don't",
            "Chris Brown - Back To Sleep",
            "G-Eazy - Drifting ft. Chris Brown, Tory Lanez ",


    };

    public static String[] rbUrl = {

            "HkVS79y4p4Y",
            "mDU-5jwPKmQ",
            "yC27zqpO_KI",
            "K44j-sb1SRY",
            "-59jGD4WrmE",


            "S_eIhC0vRtE",
            "Zt2ay6eWf1U",
            "TfOQ7Dy-Qk4",
            "E5ONTXHS2mM",
            "K_9tX4eHztY",

            "49lY0HqqUVc",
            "sEhy-RXkNo0",
            "SdV4stMqbn4",
            "pR0VsbyZxWg",
            "vUvcbT7-KCA",
            "RqLkA3pak1o",
            "yzTuBuRdAyA",
            "JPIhUaONiLU",
            "O1OTWCd40bc",
            "g7Nvc65HI7o",
            "4STstdvnMfU",
            "U4MHrrIQuis",
            "d7cVLE4SaN0",
            "OQLuhelCaDQ",
            "gE1Gbwn-LU0",


    };

    public static String[] electronic = {
            "The Chainsmokers - Closer (Lyric) ft. Halsey XXX PT2_F-1esPk",
            "Calvin Harris - This Is What You Came For XXX kOkQ4T5WO9E",
            "DVBBS & Shaun Frank - LA LA LAND ft. Delaney Jane XXX OTAqCmGXf7s",
            "The Chainsmokers - Don't Let Me Down XXX Io0fBr1XBUA",
            "DJ Snake - Middle ft. Bipolar Sunshine XXX IvPT2QuCIOA",
            "Major Lazer & DJ Snake - Lean On XXX YqeW9_5kURI",
            "Flume - Never Be Like You XXX -KPnyf8vwXI",
            "Alan Walker - Faded XXX 60ItHLz5WEA",
            "Robin Schulz - Sugar (feat. Francesco Yates) XXX bvC_0foemLY",
            "ZHU x Skrillex x THEY. - Working For It XXX qv9YI8Oqs30",
            "Kygo - Stay ft. Maty Noyes XXX z9porfO8C_Q",
            "Yellow Claw & DJ Mustard - In My Room XXX 3G32BQ3JJ1c",
            "Dillon Francis, Kygo - Coming Over ft. James Hersey XXX 5AOtEnH87Mg",
            "Zedd, Aloe Blacc - Candyman XXX QCylGd7VF9U",
            "Matoma & Astrid S - Running Out XXX 6K9wPrxbLQ0",
            "Alesso - I Wanna Know XXX TnSfwONWlk8",
            "Benny Benassi & Chris Brown - Paradise XXX 2_wpOmM1d8w",
            "AlunaGeorge - I Remember XXX rCSpXp8s2Qo",
            "Kygo - Raging ft. Kodaline XXX ZhzN7-Q00KU",
            "Hermitude ft. Big K.R.I.T. - The Buzz XXX xvNptS-caU0",
            "Shoffy feat. Lincoln Jesser - Takes My Body Higher XXX Xy3rcoz-8rU",
            "Clean Bandit and Louisa Johnson - 'Tears' XXX wvHq6S_Nwpg"
    };

    public static String getVideoImageUrl(String videoId){
        return imageUri+videoId+"/0.jpg";
    }

    public static ArrayList<YTVideo> createDancePlaylist(){
        ArrayList<YTVideo> videos = new ArrayList<>();
            int len = electronic.length;
            for(int i=0; i<len; i++){
                String temp = electronic[i];
                String[] tempo = temp.split("XXX");


                String url = tempo[1].trim();
                String rem = tempo[0].trim();

                String[] detail = rem.split("-");
                if(detail.length==2) {
                    String artist = detail[0].trim();
                    String title = detail[1].trim();
                    String imgUrl = getVideoImageUrl(url);
                    videos.add(new YTVideo(
                            url,
                            imgUrl,
                            title,
                            artist,
                            false
                    ));
                }
            }
            //Collections.shuffle(videos);

        return videos;
    }

    public static ArrayList<YTVideo> createInternationalPlaylist(){
        ArrayList<YTVideo> videos = new ArrayList<>();
        if(namesInternational.length == urlInternational.length){
            int len = namesInternational.length;
            for(int i=0; i<len; i++){
                String url = urlInternational[i];
                String name = namesInternational[i];
                String[] detail = name.split("-");
                if(detail.length==2) {
                    String artist = detail[0].trim();
                    String title = detail[1].trim();
                    String imgUrl = getVideoImageUrl(url);
                    videos.add(new YTVideo(
                            url,
                            imgUrl,
                            title,
                            artist,
                            false
                    ));
                }
            }
            //Collections.shuffle(videos);
        }
        return videos;
    }

    public static ArrayList<YTVideo> createRBPlaylist(){
        ArrayList<YTVideo> videos = new ArrayList<>();
        if(rbNames.length == rbUrl.length){
            int len = rbNames.length;
            for(int i=0; i<len; i++){
                String url = rbUrl[i];
                String name = rbNames[i];
                String[] detail = name.split("-");
                if(detail.length==2) {
                    String artist = detail[0].trim();
                    String title = detail[1].trim();
                    String imgUrl = getVideoImageUrl(url);
                    videos.add(new YTVideo(
                            url,
                            imgUrl,
                            title,
                            artist,
                            false
                    ));
                }
            }
            //Collections.shuffle(videos);
        }
        return videos;
    }

    public static ArrayList<YTVideo> createBollyPlaylist(){
        ArrayList<YTVideo> videos = new ArrayList<>();
        if(bollyName.length == bollywoodurl.length){
            int len = bollyName.length;
            for(int i=0; i<len; i++){
                String url = bollywoodurl[i];
                String name = bollyName[i];
                String[] detail = name.split("-");
                if(detail.length==2) {
                    String artist = detail[0].trim();
                    String title = detail[1].trim();
                    String imgUrl = getVideoImageUrl(url);
                    videos.add(new YTVideo(
                            url,
                            imgUrl,
                            title,
                            artist,
                            false
                    ));
                }
            }
            //Collections.shuffle(videos);
        }
        return videos;
    }


    public static ArrayList<YTVideo> createNewSensPlaylist(){
            String artist="", title="";
        ArrayList<YTVideo> videos = new ArrayList<>();
        if(newSenNames.length == newSenUrl.length){
            int len = newSenNames.length;
            for(int i=0; i<len; i++){
                String url = newSenUrl[i];
                String name = newSenNames[i];
                String[] detail = name.split("-");
                if(detail.length==2) {
                        if(i<=34){
                            artist = detail[0].trim();
                            title = detail[1].trim();
                        }else {
                             artist = detail[1].trim();
                             title = detail[0].trim();
                        }
                    String imgUrl = getVideoImageUrl(url);
                    videos.add(new YTVideo(
                            url,
                            imgUrl,
                            title,
                            artist,
                            false
                    ));
                }
            }
            //Collections.shuffle(videos);
        }
        return videos;
    }

    public static String prepareSearchString(String text){
        String txt = text.trim().replaceAll("\\s","+");
        String query = queryUrl + txt;
        return query.trim();

    }
}

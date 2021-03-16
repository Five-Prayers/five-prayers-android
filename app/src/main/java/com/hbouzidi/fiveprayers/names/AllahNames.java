package com.hbouzidi.fiveprayers.names;

import com.hbouzidi.fiveprayers.names.model.AllahName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AllahNames {

    private static final List<AllahName> NAMES = createList();

    private AllahNames() {
    }

    private static List<AllahName> createList() {
        List<AllahName> list = new ArrayList<>();

        list.add(new AllahName(1, "\u0627\u0644\u0631\u064e\u0651\u062d\u0652\u0645\u064e\u0646\u064f", "Ar Rahmaan", "2"));
        list.add(new AllahName(2, "\u0627\u0644\u0631\u064e\u0651\u062d\u0650\u064a\u0645\u064f", "Ar Raheem", "3"));
        list.add(new AllahName(3, "\u0627\u0644\u0652\u0645\u064e\u0644\u0650\u0643\u064f", "Al Malik", "4"));
        list.add(new AllahName(4, "\u0627\u0644\u0652\u0642\u064f\u062f\u064f\u0651\u0648\u0633\u064f", "Al Quddus", "5"));
        list.add(new AllahName(5, "\u0627\u0644\u0633\u064e\u0651\u0644\u0627\u064e\u0645\u064f", "As Salaam", "6"));
        list.add(new AllahName(6, "\u0627\u0644\u0652\u0645\u064f\u0624\u0652\u0645\u0650\u0646\u064f", "Al Mu'min", "7"));
        list.add(new AllahName(7, "\u0627\u0644\u0652\u0645\u064f\u0647\u064e\u064a\u0652\u0645\u0650\u0646\u064f", "Al Muhaymin", "8"));
        list.add(new AllahName(8, "\u0627\u0644\u0652\u0639\u064e\u0632\u0650\u064a\u0632\u064f", "Al Azeez", "9"));
        list.add(new AllahName(9, "\u0627\u0644\u0652\u062c\u064e\u0628\u064e\u0651\u0627\u0631\u064f", "Al Jabbaar", "a"));
        list.add(new AllahName(10, "\u0627\u0644\u0652\u0645\u064f\u062a\u064e\u0643\u064e\u0628\u0650\u0651\u0631\u064f", "Al Mutakabbir", "b"));

        list.add(new AllahName(11, "\u0627\u0644\u0652\u062e\u064e\u0627\u0644\u0650\u0642\u064f",  "Al Khaaliq", "c"));
        list.add(new AllahName(12,  "\u0627\u0644\u0652\u0628\u064e\u0627\u0631\u0650\u0626\u064f", "Al Baari", "d"));
        list.add(new AllahName(13, "\u0627\u0644\u0652\u0645\u064f\u0635\u064e\u0648\u0650\u0651\u0631\u064f" , "Al Musawwir", "e"));
        list.add(new AllahName(14, "\u0627\u0644\u0652\u063a\u064e\u0641\u064e\u0651\u0627\u0631\u064f", "Al Ghaffaar", "f"));
        list.add(new AllahName(15, "\u0627\u0644\u0652\u0642\u064e\u0647\u064e\u0651\u0627\u0631\u064f", "Al Qahhaar", "g"));
        list.add(new AllahName(16, "\u0627\u0644\u0652\u0648\u064e\u0647\u064e\u0651\u0627\u0628\u064f", "Al Wahhaab", "h"));
        list.add(new AllahName(17, "\u0627\u0644\u0631\u064e\u0651\u0632\u064e\u0651\u0627\u0642\u064f", "Ar Razzaaq", "i"));
        list.add(new AllahName(18, "\u0627\u0644\u0652\u0641\u064e\u062a\u064e\u0651\u0627\u062d\u064f", "Al Fattaah", "j"));
        list.add(new AllahName(19, "\u0627\u064e\u0644\u0652\u0639\u064e\u0644\u0650\u064a\u0652\u0645\u064f", "Al 'Aleem", "k"));
        list.add(new AllahName(20, "\u0627\u0644\u0652\u0642\u064e\u0627\u0628\u0650\u0636\u064f","Al Qaabid" , "l"));

        list.add(new AllahName(21, "\u0627\u0644\u0652\u0628\u064e\u0627\u0633\u0650\u0637\u064f", "Al Baasit", "m"));
        list.add(new AllahName(22, "\u0627\u0644\u0652\u062e\u064e\u0627\u0641\u0650\u0636\u064f", "Al Khaafid", "n"));
        list.add(new AllahName(23, "\u0627\u0644\u0631\u064e\u0651\u0627\u0641\u0650\u0639\u064f", "Ar Raafi'", "o"));
        list.add(new AllahName(24, "\u0627\u0644\u0652\u0645\u064f\u0639\u0650\u0632\u064f\u0651", "Al Mu'iz", "p"));
        list.add(new AllahName(25, "\u0627\u0644\u0645\u064f\u0630\u0650\u0644\u064f\u0651", "Al Mudhil", "q"));
        list.add(new AllahName(26, "\u0627\u0644\u0633\u064e\u0651\u0645\u0650\u064a\u0639\u064f", "As Samee'", "r"));
        list.add(new AllahName(27, "\u0627\u0644\u0652\u0628\u064e\u0635\u0650\u064a\u0631\u064f", "Al Baseer", "s"));
        list.add(new AllahName(28, "\u0627\u0644\u0652\u062d\u064e\u0643\u064e\u0645\u064f", "Al Hakam", "t"));
        list.add(new AllahName(29, "\u0627\u0644\u0652\u0639\u064e\u062f\u0652\u0644\u064f",  "Al 'Adl", "u"));
        list.add(new AllahName(30, "\u0627\u0644\u0644\u064e\u0651\u0637\u0650\u064a\u0641\u064f", "Al Lateef", "v"));

        list.add(new AllahName(31,  "\u0627\u0644\u0652\u062e\u064e\u0628\u0650\u064a\u0631\u064f", "Al Khabeer", "w"));
        list.add(new AllahName(32, "\u0627\u0644\u0652\u062d\u064e\u0644\u0650\u064a\u0645\u064f","Al Haleem" , "x"));
        list.add(new AllahName(33, "\u0627\u0644\u0652\u0639\u064e\u0638\u0650\u064a\u0645\u064f", "Al 'Azeem", "y"));
        list.add(new AllahName(34, "\u0627\u0644\u0652\u063a\u064e\u0641\u064f\u0648\u0631\u064f", "Al Ghafoor", "z"));
        list.add(new AllahName(35, "\u0627\u0644\u0634\u064e\u0651\u0643\u064f\u0648\u0631\u064f", "Ash Shakoor", "A"));
        list.add(new AllahName(36, "\u0627\u0644\u0652\u0639\u064e\u0644\u0650\u064a\u064f\u0651", "Al 'Aliyy", "B"));
        list.add(new AllahName(37, "\u0627\u0644\u0652\u0643\u064e\u0628\u0650\u064a\u0631\u064f", "Al Kabeer", "C"));
        list.add(new AllahName(38, "\u0627\u0644\u0652\u062d\u064e\u0641\u0650\u064a\u0638\u064f","Al Hafeez" , "D"));
        list.add(new AllahName(39,  "\u0627\u0644\u0645\u064f\u0642\u064a\u0650\u062a", "Al Muqeet", "E"));
        list.add(new AllahName(40, "\u0627\u0644\u0652\u062d\u0633\u0650\u064a\u0628\u064f", "Al Haseeb", "F"));

        list.add(new AllahName(41,"\u0627\u0644\u0652\u062c\u064e\u0644\u0650\u064a\u0644\u064f" ,"Al Jaleel" , "G"));
        list.add(new AllahName(42, "\u0627\u0644\u0652\u0643\u064e\u0631\u0650\u064a\u0645\u064f", "Al Kareem", "H"));
        list.add(new AllahName(43,"\u0627\u0644\u0631\u064e\u0651\u0642\u0650\u064a\u0628\u064f" , "Ar Raqeeb", "I"));
        list.add(new AllahName(44, "\u0627\u0644\u0652\u0645\u064f\u062c\u0650\u064a\u0628\u064f", "Al Mujeeb", "J"));
        list.add(new AllahName(45, "\u0627\u0644\u0652\u0648\u064e\u0627\u0633\u0650\u0639\u064f", "Al Waasi'", "K"));
        list.add(new AllahName(46,"\u0627\u0644\u0652\u062d\u064e\u0643\u0650\u064a\u0645\u064f" , "Al Hakeem", "L"));
        list.add(new AllahName(47,"\u0627\u0644\u0652\u0648\u064e\u062f\u064f\u0648\u062f\u064f" , "Al Wudood", "M"));
        list.add(new AllahName(48, "\u0627\u0644\u0652\u0645\u064e\u062c\u0650\u064a\u062f\u064f", "Al Majeed", "N"));
        list.add(new AllahName(49, "\u0627\u0644\u0652\u0628\u064e\u0627\u0639\u0650\u062b\u064f", "Al Baa'ith", "O"));
        list.add(new AllahName(50, "\u0627\u0644\u0634\u064e\u0651\u0647\u0650\u064a\u062f\u064f", "Ash Shaheed", "P"));

        list.add(new AllahName(51, "\u0627\u0644\u0652\u062d\u064e\u0642\u064f\u0651", "Al Haqq", "Q"));
        list.add(new AllahName(52, "\u0627\u0644\u0652\u0648\u064e\u0643\u0650\u064a\u0644\u064f", "Al Wakeel", "R"));
        list.add(new AllahName(53, "\u0627\u0644\u0652\u0642\u064e\u0648\u0650\u064a\u064f\u0651", "Al Qawiyy", "S"));
        list.add(new AllahName(54, "\u0627\u0644\u0652\u0645\u064e\u062a\u0650\u064a\u0646\u064f", "Al Mateen", "T"));
        list.add(new AllahName(55, "\u0627\u0644\u0652\u0648\u064e\u0644\u0650\u064a\u064f\u0651","Al Waliyy" , "U"));
        list.add(new AllahName(56, "\u0627\u0644\u0652\u062d\u064e\u0645\u0650\u064a\u062f\u064f", "Al Hameed", "V"));
        list.add(new AllahName(57, "\u0627\u0644\u0652\u0645\u064f\u062d\u0652\u0635\u0650\u064a", "Al Muhsi", "W"));
        list.add(new AllahName(58, "\u0627\u0644\u0652\u0645\u064f\u0628\u0652\u062f\u0650\u0626\u064f","Al Mubdi" , "X"));
        list.add(new AllahName(59, "\u0627\u0644\u0652\u0645\u064f\u0639\u0650\u064a\u062f\u064f",  "Al Mu'eed", "Y"));
        list.add(new AllahName(60, "\u0627\u0644\u0652\u0645\u064f\u062d\u0652\u064a\u0650\u064a", "Al Muhiy", "Z"));

        list.add(new AllahName(61, "\u0627\u064e\u0644\u0652\u0645\u064f\u0645\u0650\u064a\u062a\u064f", "Al Mumeet", "\""));
        list.add(new AllahName(62, "\u0627\u0644\u0652\u062d\u064e\u064a\u064f\u0651","Al Haiyy" , "#"));
        list.add(new AllahName(63, "\u0627\u0644\u0652\u0642\u064e\u064a\u064f\u0651\u0648\u0645\u064f","Al Qayyoom" , "$"));
        list.add(new AllahName(64, "\u0627\u0644\u0652\u0648\u064e\u0627\u062c\u0650\u062f\u064f", "Al Waajid", "%"));
        list.add(new AllahName(65, "\u0627\u0644\u0652\u0645\u064e\u0627\u062c\u0650\u062f\u064f", "Al Maajid", "&"));
        list.add(new AllahName(66, "\u0627\u0644\u0652\u0648\u0627\u062d\u0650\u062f\u064f", "Al Waahid", "'"));
        list.add(new AllahName(67, "\u0627\u064e\u0644\u0627\u064e\u062d\u064e\u062f\u064f", "Al Ahad", ")"));
        list.add(new AllahName(68, "\u0627\u0644\u0635\u064e\u0651\u0645\u064e\u062f\u064f", "As Samad", "("));
        list.add(new AllahName(69, "\u0627\u0644\u0652\u0642\u064e\u0627\u062f\u0650\u0631\u064f", "Al Qaadir", "*"));
        list.add(new AllahName(70, "\u0627\u0644\u0652\u0645\u064f\u0642\u0652\u062a\u064e\u062f\u0650\u0631\u064f", "Al Muqtadir", "+"));

        list.add(new AllahName(71, "\u0627\u0644\u0652\u0645\u064f\u0642\u064e\u062f\u0650\u0651\u0645\u064f", "Al Muqaddim" , ","));
        list.add(new AllahName(72, "\u0627\u0644\u0652\u0645\u064f\u0624\u064e\u062e\u0650\u0651\u0631\u064f", "Al Mu\u2019akhir", "-"));
        list.add(new AllahName(73, "\u0627\u0644\u0623\u0648\u064e\u0651\u0644\u064f", "Al Awwal", "."));
        list.add(new AllahName(74, "\u0627\u0644\u0622\u062e\u0650\u0631\u064f", "Al Aakhir", "/"));
        list.add(new AllahName(75, "\u0627\u0644\u0638\u064e\u0651\u0627\u0647\u0650\u0631\u064f", "Az Zaahir", "0"));
        list.add(new AllahName(76, "\u0627\u0644\u0652\u0628\u064e\u0627\u0637\u0650\u0646\u064f", "Al Baatin", ":"));
        list.add(new AllahName(77, "\u0627\u0644\u0652\u0648\u064e\u0627\u0644\u0650\u064a", "Al Waali", ";"));
        list.add(new AllahName(78, "\u0627\u0644\u0652\u0645\u064f\u062a\u064e\u0639\u064e\u0627\u0644\u0650\u064a","Al Muta\u2019ali" , ">"));
        list.add(new AllahName(79, "\u0627\u0644\u0652\u0628\u064e\u0631\u064f\u0651", "Al Barr", "="));
        list.add(new AllahName(80, "\u0627\u0644\u062a\u064e\u0651\u0648\u064e\u0627\u0628\u064f", "At Tawwaab", "<"));

        list.add(new AllahName(81, "\u0627\u0644\u0652\u0645\u064f\u0646\u0652\u062a\u064e\u0642\u0650\u0645\u064f","Al Muntaqim" , "?"));
        list.add(new AllahName(82, "\u0627\u0644\u0639\u064e\u0641\u064f\u0648\u064f\u0651", "Al Afuww", "@"));
        list.add(new AllahName(83, "\u0627\u0644\u0631\u064e\u0651\u0624\u064f\u0648\u0641\u064f", "Ar Ra\u2019oof", "]"));
        list.add(new AllahName(84, "\u0645\u064e\u0627\u0644\u0650\u0643\u064f \u0627\u0644\u0652\u0645\u064f\u0644\u0652\u0643\u0650", "Maalik Ul Mulk", "\\"));
        list.add(new AllahName(85, "\u0630\u064f\u0648\u0627\u0644\u0652\u062c\u064e\u0644\u0627\u064e\u0644\u0650 \u0648\u064e\u0627\u0644\u0625\u0643\u0652\u0631\u064e\u0627\u0645\u0650", "Dhu Al Jalaali Wa Al Ikraam", "["));
        list.add(new AllahName(86, "\u0627\u0644\u0652\u0645\u064f\u0642\u0652\u0633\u0650\u0637\u064f","Al Muqsit" , "^"));
        list.add(new AllahName(87, "\u0627\u0644\u0652\u062c\u064e\u0627\u0645\u0650\u0639\u064f", "Al Jaami'", "_"));
        list.add(new AllahName(88, "\u0627\u0644\u0652\u063a\u064e\u0646\u0650\u064a\u064f\u0651", "Al Ghaniyy", "`"));
        list.add(new AllahName(89, "\u0627\u0644\u0652\u0645\u064f\u063a\u0652\u0646\u0650\u064a", "Al Mughi", "}"));
        list.add(new AllahName(90,"\u0627\u064e\u0644\u0652\u0645\u064e\u0627\u0646\u0650\u0639\u064f" , "Al Maani'", "|"));

        list.add(new AllahName(91, "\u0627\u0644\u0636\u064e\u0651\u0627\u0631\u064e\u0651", "Ad Daaarr", "{"));
        list.add(new AllahName(92, "\u0627\u0644\u0646\u064e\u0651\u0627\u0641\u0650\u0639\u064f", "An Naafi\u2019", "~"));
        list.add(new AllahName(93, "\u0627\u0644\u0646\u064f\u0651\u0648\u0631\u064f", "An Noor", "¡"));
        list.add(new AllahName(94, "\u0627\u0644\u0652\u0647\u064e\u0627\u062f\u0650\u064a", "Al Haadi", "¢"));
        list.add(new AllahName(95, "\u0627\u0644\u0652\u0628\u064e\u062f\u0650\u064a\u0639\u064f", "Al Badi'", "£"));
        list.add(new AllahName(96, "\u0627\u064e\u0644\u0652\u0628\u064e\u0627\u0642\u0650\u064a", "Al Baaqi", "¤"));
        list.add(new AllahName(97, "\u0627\u0644\u0652\u0648\u064e\u0627\u0631\u0650\u062b\u064f", "Al Waarith", "¥"));
        list.add(new AllahName(98, "\u0627\u0644\u0631\u064e\u0651\u0634\u0650\u064a\u062f\u064f", "Ar Rasheed", "¦"));
        list.add(new AllahName(99, "\u0627\u0644\u0635\u064e\u0651\u0628\u064f\u0648\u0631\u064f", "As Saboor", "!"));

        return Collections.unmodifiableList(list);
    }

    public static List<AllahName> getAll() {
        return NAMES;
    }



}

package uj.wmii.pwj.introduction;

import java.util.Locale;

public class Banner {
    public String[] toBanner(String input) {
        if (input == null) return new String[0];
        input = input.toLowerCase(Locale.ROOT);
        char[] charInput = input.toCharArray();
        String[] result = new String[7];

        for (char myChar : charInput) {
            if (myChar == ' ') {
                for (int i = 0; i < result.length; i++) {
                    if (result[i] != null) {
                        result[i] = result[i] + "   ";
                    } else {
                        result[i] = "   ";
                    }
                }
                continue;
            }

            String[] letter = Letters[(int) myChar - 'a'];

            for (int i = 0; i < letter.length; i++) {
                if (result[i] != null) {
                    result[i] = result[i] + letter[i];
                } else {
                    result[i] = letter[i];
                }
            }
        }

        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].replaceAll("\\s+$", "");
        }
        return result;
    }

    final String[][] Letters = {
            {"   #    ", "  # #   ", " #   #  ", "#     # ", "####### ", "#     # ", "#     # "},
            {"######  ", "#     # ", "#     # ", "######  ", "#     # ", "#     # ", "######  "},
            {" #####  ", "#     # ", "#       ", "#       ", "#       ", "#     # ", " #####  "},
            {"######  ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "######  "},
            {"####### ", "#       ", "#       ", "#####   ", "#       ", "#       ", "####### "},
            {"####### ", "#       ", "#       ", "#####   ", "#       ", "#       ", "#       "},
            {" #####  ", "#     # ", "#       ", "#  #### ", "#     # ", "#     # ", " #####  "},
            {"#     # ", "#     # ", "#     # ", "####### ", "#     # ", "#     # ", "#     # "},
            {"### ", " # ", " #  ", " #  ", " #  ", " #  ", "### "},
            {"      # ", "       # ", "      # ", "      # ", "#     # ", "#     # ", " #####  "},
            {"#    # ", "#   #  ", "#  #   ", "###    ", "#  #   ", "#   #  ", "#    # "},
            {"#       ", "#       ", "#       ", "#       ", "#       ", "#       ", "####### "},
            {"#     # ", "##   ## ", "# # # # ", "#  #  # ", "#     # ", "#     # ", "#     # "},
            {"#     # ", "##    # ", "# #   # ", "#  #  # ", "#   # # ", "#    ## ", "#     # "},
            {"####### ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "####### "},
            {"######  ", "#     # ", "#     # ", "######  ", "#       ", "#       ", "#       "},
            {" #####  ", "#     # ", "#     # ", "#     # ", "#   # # ", "#    #  ", " #### # "},
            {"######  ", "#     # ", "#     # ", "######  ", "#   #   ", "#    #  ", "#     # "},
            {" #####  ", "#     # ", "#       ", " #####  ", "      # ", "#     # ", " #####  "},
            {"####### ", "   #    ", "   #    ", "   #    ", "   #    ", "   #    ", "   #    "},
            {"#     # ", "#     # ", "#     # ", "#     # ", "#     # ", "#     # ", " #####  "},
            {"#     # ", "#     # ", "#     # ", "#     # ", " #   #  ", "  # #   ", "   #    "},
            {"#     # ", "#  #  # ", "#  #  # ", "#  #  # ", "#  #  # ", "#  #  # ", " ## ##  "},
            {"#     # ", " #   #  ", "  # #   ", "   #    ", "  # #   ", " #   #  ", "#     # "},
            {"#     # ", " #   #  ", "  # #   ", "   #    ", "   #    ", "   #    ", "   #    "},
            {"####### ", "     # ", "    #   ", "   #    ", "  #     ", " #      ", "####### "},
            {"   ", "   ", "   ", "   ", "   ", "   ", "   "}
    };
}
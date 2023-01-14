package model;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 *  A class that provides language translation features.
 */
public class Languages {

    private static final Locale english = new Locale("en", "EN");
    private static final Locale dutch = new Locale("nl", "NL");
    private static final Locale french = new Locale("fr", "FR");

    private static final String[] supportedLanguages = {"English", "French", "Dutch"};
    private static String userLanguageCode;  // Default value
    private static String userLanguage; // Default value
    private static ResourceBundle rb;



    /**
     *  Sets language automatically based on user's local language.
     */
    public static void setLanguage() {

        switch (Locale.getDefault().getLanguage()) {
            case "fr" -> {
                userLanguageCode = "fr";
                userLanguage = "French";
                Locale.setDefault(french);  // Here we are setting the default, so when we use getDefault, it will use this value
            }
            case "nl" -> {
                userLanguageCode = "nl";
                userLanguage = "Dutch";
                Locale.setDefault(dutch);
            }
            case "en" -> {
                userLanguageCode = "en";
                userLanguage = "English";
                Locale.setDefault(english);
            }
            default -> {
                Tools.consoleMessage(Tools.MsgType.ERROR, "Unable to set language, defaulting to English", "Languages.setLanguages()");
                userLanguageCode = "en";
                userLanguage = "English";
                Locale.setDefault(english);
            }

        }

        // Get bundle will check the resource folder, scanning files starting with Lang_X until it finds where X matches input
        rb = ResourceBundle.getBundle("resources/Lang", Locale.getDefault());

        Tools.consoleMessage(Tools.MsgType.INFO, "Language is ".concat(Languages.userLanguage), "Languages.setLanguage()");
    }


    /**
     *  Sets language manually based on input.
     *  @param language Name of language to be set.
     */
    public static void setLanguageManually(String language) {

        switch (language) {
            case "French" -> {
                userLanguageCode = "fr";
                userLanguage = "French";
                Locale.setDefault(french);  // Here we are setting the default, so when we use getDefault, it will use this value
            }
            case "Dutch" -> {
                userLanguageCode = "nl";
                userLanguage = "Dutch";
                Locale.setDefault(dutch);
            }
            case "English" -> {
                userLanguageCode = "en";
                userLanguage = "English";
                Locale.setDefault(english);
            }
            default -> Tools.consoleMessage(Tools.MsgType.ERROR, "Unable to set language", "Languages.setLanguages");
        }

            // Get bundle will check resources folder, scanning files starting with Lang_X until it finds where X matches input
            rb = ResourceBundle.getBundle("resources/Lang", Locale.getDefault());

            Tools.consoleMessage(Tools.MsgType.INFO, "Language is ".concat(Languages.userLanguage), "Languages.setLanguage");
    }

    /**
     * @return Current language.
     */
    public static String getLanguage() {
        return Languages.userLanguage;
    }

    /**
     *  Verifies language string is supported.
     *  @param language Name of language.
     *  @return True if language is supported.
     */
    public static boolean checkLangSupported(String language) {

        for (int i = 0; i < Languages.supportedLanguages.length; i ++) {
            if(Objects.equals(language, Languages.supportedLanguages[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     *  Translates a single english word to local language.
     *  @param word The word to be translated.
     *  @param capitalize Word can be returned with first character capitalized.
     *  @return Translated word.
     */
    public static String translateWord(String word, boolean capitalize) {

        String translatedWord = rb.getString(word);  // Translate word to correct language

        if (capitalize) {  // Capitalize first char of each word in string

            StringBuilder uncappedWord = new StringBuilder(translatedWord);

            for(int i=0; i < uncappedWord.length(); i++) {
                if (i == 0 || uncappedWord.charAt(i - 1) == ' ') {
                    uncappedWord.setCharAt(i, Character.toUpperCase(uncappedWord.charAt(i)));
                }
            }

            return uncappedWord.toString();  // Return modified translated string

        }
        else {
            return translatedWord;  // Return standard string
        }
    }


    /**
     *  Translates an entire english sentence to local language.
     *  @param sentence The sentence to be translated.
     *  @param capitalize If true, leading character of each word is capitalized.
     *  @return Translated sentence.
     */
    public static String translateSentence(String sentence, boolean capitalize) {
        String[] wordArray = sentence.split(" ");

        for (int i = 0; i < wordArray.length; i++) {
            String translatedWord = translateWord(wordArray[i], capitalize);
            wordArray[i] = translatedWord;
        }
        String translatedString = String.join(" ", wordArray);
        return translatedString;
    }

}
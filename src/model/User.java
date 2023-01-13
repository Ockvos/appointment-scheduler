package model;

import DAO.UserQuery;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Objects;


/**
 * A class used to create user objects.
 */
public class User {

    private static int userId;
    private static String profilePicture = Globals.PROFILE_1;  // Default profile picture
    private static String password;
    private static String username;

    /**
     * Sets up user if user ID is correct.
     * @param userId ID of user attempting to sign in.
     * @return True if valid user ID.
     */
    public static boolean initializeUser(String userId) throws SQLException {

        boolean validId = setUserId(userId);  // Will return false and show error msg if ID is invalid

        if (validId) {
            setUserPassword(User.userId);
            setUsername(User.userId);

            return true;

        } else {

            return false;
        }
    }


    /**
     * Creates default user, allowing user to skip login.
     * Used for testing only.
     * @param userNameInput Name to be set.
     * @param userIdInput ID to be set.
     * @param userPasswordInput Password to be set.
     */
    public static void createTestUser(String userNameInput, int userIdInput, String userPasswordInput) {
        username = userNameInput;
        userId = userIdInput;
        password = userPasswordInput;
    }



    /**
     * Sets username.
     */
    public static void setUsername(int userId) throws SQLException {

        User.username = UserQuery.fetchUsername(userId);

    }


    /**
     * @return Username.
     */
    public static String getUsername() {
        return User.username;
    }



    /**
     * Sets profile picture.
     */
    public static void setProfilePicture(String img) {
        User.profilePicture = img;
    }


    /**
     * @return Profile picture.
     */
    public static String getProfilePicture() {
        return User.profilePicture;
    }


    /**
     * @return User ID.
     */
    public static int getUserId() {
        return User.userId;
    }


    /**
     * Sets user ID, verifies user exists in database.
     * @param userId Numeric user ID.
     */
    public static boolean setUserId(int userId) throws SQLException {

        if(UserQuery.idExists(userId)) {
            User.userId = userId;

            return true;
        } else {
            User.userId = -1;

            return false;
        }
    }


    /**
     * Sets user ID, verifies user exists in database.
     * @param userId String user ID.
     */
    public static boolean setUserId(String userId) {

        try {

            if (Globals.autoLogin) {
                User.userId = Globals.defaultUserId;

                return true;
            }

            // Grab user ID from field, verify it's an integer
            int parsedUserId = Integer.parseInt(userId);


            if(UserQuery.idExists(parsedUserId)) {  // Verify user input id is valid, attempt to parse
                User.userId = parsedUserId;

                return true;
            } else {  // Id input type was correct, but does not exist in database
                User.userId = -1;
                Tools.errorMessage(Languages.translateSentence("invalid ID", true), Languages.translateSentence("no user with that ID exists", true));


                return false;
            }

        }
        catch (Exception parseFailed) {  // Input was not of type int

            Tools.errorMessage(Languages.translateSentence("invalid input", true), Languages.translateSentence("user ID field has invalid input", true));
            Tools.consoleMessage(Tools.MsgType.INFO, "User ID not parsed to integer, disregard if using auto login", "User.setUserId");
            return false;
        }
    }


    /**
     * Sets user password, verifies user password exists in database.
     * @param userId Numeric user ID.
     */
    public static void setUserPassword(int userId) throws SQLException {
        User.password = UserQuery.locatePassword(userId);
    }


    /**
     * Sets user password, verifies user password exists in database.
     * @param userId String user ID.
     */
    public static void setUserPassword(String userId) throws SQLException {

        int userIdParsed = Integer.parseInt(userId);

        User.password = UserQuery.locatePassword(userIdParsed);
    }


    /**
     * @return Password.
     */
    public static String getUserPassword() {
        return User.password;
    }


    /**
     * Verifies password is correct.
     * @param inputPassword Password to be checked.
     * @return True if password is correct.
     */
    public static boolean correctUserPassword(String inputPassword) {
        return Objects.equals(User.password, inputPassword);
    }

    /**
     * @return User's timezone.
     */
    public static ZoneId getTimeZone() {
        return (TimeAndDate.getTimeZone());
    }
}

package rr.reflexreactor;

import android.content.Context;
import android.content.SharedPreferences;

public class GlobalUtils {
    public static String getRegistrationId(Context context)
    {
        SharedPreferences sharedPref =context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.saved_reg_id),"");
    }
    public static String getPhnNumber(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.saved_phn),"");
    }
    public static String url_online = "http://192.168.43.51/reflexreactor/show_online.php";
    public  static String url_send_game_request="http://192.168.43.51/reflexreactor/Send_game_request.php";
    public  static  String url_get_all_requests="http://192.168.43.51/reflexreactor/get_all_requests.php";
    public static  String url_show_request="http://192.168.43.51/reflexreactor/show_request.php";
    public static  String url_check_valid_phn="http://192.168.43.51/reflexreactor/check_valid_phn.php";
    public  static String PROJECT_NUMBER="89287980627";
    public static  String url_update_reg_id="http://192.168.43.51/reflexreactor/update_reg_id.php";
    public static String url_accept_request = "http://192.168.43.51/reflexreactor/accept_request2.php";
    public static String url_getAcceptedUsers="http://192.168.43.51/reflexreactor/getAcceptedUsers.php";
    public static String url_start_game="http://192.168.43.51/reflexreactor/start_game.php";
    public static String url_update_result="http://192.168.43.51/reflexreactor/update_result.php";
    public static String url_check_all_updated="http://192.168.43.51/reflexreactor/check_all_updated.php";
    public static String url_fetch_result="http://192.168.43.51/reflexreactor/fetch_results.php";
    public static String url_check_if_game_valid="http://192.168.43.51/reflexreactor/check_if_game_valid.php";
    public static String url_update_rating="http://192.168.43.51/reflexreactor/update_rating.php";
    public static String url_delete_game_request="http://192.168.43.51/reflexreactor/delete_game_request.php";
    public static String url_decline_request="http://192.168.43.51/reflexreactor/decline_request.php";


}

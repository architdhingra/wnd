package com.wnd.myapp.lenovate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhruv on 1/13/2017.
 */

public class filter {

    public static String[] categ,categ_id, color, room, material, style; // for inflating filter options in product listings

    public static String[] price = {"100-500", "500-2000", "2000-5000"};

    public static String clr_opt, categ_opt, room_opt, mat_opt, style_opt, price_opt, catid_opt, totalprice;

    Map<String, String> remove_filterMap = new HashMap<String, String>();

    public static void clearoption() {

        /*--------- clear selected filters-----------*/
        clr_opt = null;
        categ_opt = null;
        room_opt = null;
        mat_opt = null;
        style_opt = null;
        price_opt = null;
        catid_opt=null;

        /*------------- clear arrays ---------------*/
        categ =null;
        categ_id = null;
        color = null;
        room= null;
        material = null;
        style = null;
    }

    public static String showoption() {
        String result =
                "color: " + clr_opt + "\n" +
                        "category: " + categ_opt + "\n" +
                        "Room: " + room_opt + "\n" +
                        "Material: " + mat_opt + "\n" +
                        "Style: " + style_opt + "\n" +
                        "Price: " + price_opt + "\n";
        return result;
    }

}

package org.lifeautomation.mosambeebharatqr;

import com.google.gson.Gson;

public class GsonSingleton {

    public static Gson gson=null;

    public static Gson getInstance()
    {
        if (gson==null)
        {
            gson=new Gson();
        }
        return gson;
    }
}
